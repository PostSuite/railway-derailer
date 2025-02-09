package com.postsuite.derailer.infrastructure;

import com.postsuite.derailer.entities.DerailedEntity;
import com.postsuite.derailer.entities.DerailmentEntity;
import com.postsuite.derailer.entities.DerailmentState;
import com.postsuite.derailer.infrastructure.managers.railway.RailwayInfrastructureManager;
import com.postsuite.derailer.mapper.DerailmentMapper;
import com.postsuite.derailer.models.response.DerailmentModel;
import com.postsuite.derailer.repository.DerailerRunRepository;
import com.postsuite.derailer.services.DerailmentService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Slf4j
@ApplicationScoped
public class InfrastructureService {

    // todo - figure out how to inject a certain service manager
    private final RailwayInfrastructureManager infrastructureManager;
    private final DerailerRunRepository runRepository;
    private final DerailmentMapper derailmentMapper;
    private final DerailmentService derailmentService;

    @Inject
    public InfrastructureService(final RailwayInfrastructureManager infrastructureManager, final DerailerRunRepository runRepository, final DerailmentMapper derailmentMapper, final DerailmentService derailmentService) {
        this.infrastructureManager = infrastructureManager;
        this.runRepository = runRepository;
        this.derailmentMapper = derailmentMapper;
        this.derailmentService = derailmentService;
    }

    public Uni<DerailmentModel> triggerDerailment(final int amount, final Duration time, final List<String> blacklistedServices) {
        final Uni<List<DerailedEntity>> derailedEntities = this.infrastructureManager.derail(amount, blacklistedServices);
        return derailedEntities.map(entities -> new DerailmentEntity()
                        .setAffectedEntities(entities.stream().filter(Objects::nonNull).toList())
                        .setStartTimestamp(Instant.now())
                        .setRollbackTimestamp(Instant.now().plus(time.getSeconds(), ChronoUnit.SECONDS)))
                .flatMap(this.runRepository::persistOrUpdate)
                .map(this.derailmentMapper::toModel);
    }

    public Uni<Void> rollbackCurrentDerailment(final boolean forced, final String identifier) {
        final Uni<DerailmentEntity> currentEntity = identifier == null ?
                this.derailmentService.getEntities(1, 1).map(entities -> entities.stream().findFirst().orElse(null)) :
                this.derailmentService.getDerailment(identifier);
        return currentEntity.invoke(entity -> {
                    if (entity == null) {
                        throw new RuntimeException("Null entity");
                    }
                    if (entity.getState() == DerailmentState.COMPLETE) {
                        throw new IllegalArgumentException("Complete, cannot rollback.");
                    }
                    if (entity.getRollbackTimestamp().isAfter(Instant.now()) && !forced) {
                        throw new RuntimeException("Hasn't yet reached rollback time");
                    }
                })
                .flatMap(entity -> {
                    log.info("Rolling back " + entity.getBlastRadius() + " derailed entities.");
                    final List<DerailedEntity> subEntities = entity.getAffectedEntities().stream().filter(sub -> sub.getState() != DerailmentState.COMPLETE).toList();
                    return this.infrastructureManager.rollback(subEntities)
                            .call(changedEntities -> this.runRepository.update(entity));
                }).replaceWithVoid();
    }

}
