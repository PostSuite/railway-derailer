package com.postsuite.derailer.orchestrators;

import com.postsuite.derailer.infrastructure.InfrastructureService;
import com.postsuite.derailer.models.response.DerailmentModel;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ManagementOrchestrator {

    private final InfrastructureService infrastructureService;
    private final DerailmentOrchestrator derailmentOrchestrator;
    
    @ConfigProperty(name = "derailment.duration")
    int derailmentDurationMinutes;

    @ConfigProperty(name = "derailment.radius")
    int blastRadius;

    @ConfigProperty(name = "derailment.blacklist")
    List<String> blacklistedServices;

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

    public Uni<DerailmentModel> triggerDerailment() {
        final Uni<Boolean> anyActiveUni =
                this.derailmentOrchestrator.getDerailments(1, 1)
                        .map(list -> list.stream().anyMatch(model -> !model.isRolledBack()));
        return anyActiveUni.flatMap(active -> {
            if (active) {
                throw new IllegalArgumentException("Already an active derailment ongoing");
            }
            return this.infrastructureService.triggerDerailment(
                    this.blastRadius,
                    Duration.ofMinutes(this.derailmentDurationMinutes),
                    this.blacklistedServices);
        });
    }


}
