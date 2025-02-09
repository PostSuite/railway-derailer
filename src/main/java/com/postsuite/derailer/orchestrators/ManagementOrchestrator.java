package com.postsuite.derailer.orchestrators;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.postsuite.derailer.infrastructure.InfrastructureService;
import com.postsuite.derailer.mapper.PauseMapper;
import com.postsuite.derailer.models.PauseModel;
import com.postsuite.derailer.models.response.DerailmentModel;
import com.postsuite.derailer.services.PauseService;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ManagementOrchestrator {

    private final InfrastructureService infrastructureService;
    private final DerailmentOrchestrator derailmentOrchestrator;
    private final PauseService pauseService;
    private final PauseMapper pauseMapper;

    @ConfigProperty(name = "derailment.duration")
    int derailmentDurationMinutes;

    @ConfigProperty(name = "derailment.radius")
    int blastRadius;

    @ConfigProperty(name = "derailment.blacklist")
    List<String> blacklistedServices;

    @ConfigProperty(name = "derailment.frequency")
    String cronExpression;

    @Scheduled(cron = "{derailment.frequency}")
    void scheduledDerailment() {
        try {
            this.triggerDerailment().subscribe().with(
                    derailment -> log.info("Derailment triggered: {}", derailment),
                    failure -> log.error("Derailment failed: {}", failure.getMessage())
            );
        } catch (final Exception e) {
            log.error("Scheduled derailment failed: {}", e.getMessage());
        }
    }

    @Scheduled(every = "1m")
    void autoRollback() {
        try {
            this.triggerRollback(false, null).subscribe().with(
                    derailment -> {
                    },
                    failure -> log.warn("Rollback failed: {}", failure.getMessage())
            );
        } catch (final Exception e) {
            log.warn("Rollback failed: {}", e.getMessage());
        }
    }

    public Uni<ZonedDateTime> getNextDerailment() {
        final CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
        final ExecutionTime executionTime = ExecutionTime.forCron(parser.parse(this.cronExpression));
        final ZonedDateTime now = ZonedDateTime.now();
        return Uni.createFrom().item(executionTime.nextExecution(now).orElseThrow());
    }

    public Uni<DerailmentModel> triggerDerailment() {
        final Uni<PauseModel> pauseUni = this.getPause();
        final Uni<Boolean> anyActiveUni =
                this.derailmentOrchestrator.getDerailments(1, 1)
                        .map(list -> list.stream().anyMatch(model -> !model.isRolledBack()));
        return Uni.combine().all().unis(pauseUni, anyActiveUni).asTuple()
                .flatMap(tuple2 -> {
                    if (tuple2.getItem1() != null) {
                        throw new IllegalArgumentException("Pause is in effect");
                    }
                    if (tuple2.getItem2()) {
                        throw new IllegalArgumentException("Already an active derailment ongoing");
                    }
                    return this.infrastructureService.triggerDerailment(
                            this.blastRadius,
                            Duration.ofMinutes(this.derailmentDurationMinutes),
                            this.blacklistedServices);
                });
    }

    public Uni<Void> triggerRollback(final boolean force, @Nullable final String identifier) {
        return this.infrastructureService.rollbackCurrentDerailment(force, identifier)
                .onFailure().recoverWithItem(throwable -> {
                    if (force) {
                        throw new RuntimeException(throwable);
                    }
                    return null;
                });
    }

    public Uni<PauseModel> pause(final PauseModel pauseModel) {
        return this.pauseService.savePause(pauseModel)
                .replaceWith(pauseModel);
    }

    public Uni<Void> unpause() {
        return this.pauseService.unpause();
    }

    public Uni<PauseModel> getPause() {
        return this.pauseService.getPause()
                .map(pause -> {
                    if (pause != null && pause.getEndTimestamp().isBefore(Instant.now())) {
                        return null;
                    }
                    return pause;
                })
                .map(this.pauseMapper::toModel);
    }


}
