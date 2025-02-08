package com.postsuite.derailer.infrastructure;

import com.postsuite.derailer.entities.DerailedEntity;
import com.postsuite.derailer.entities.DerailmentEntity;
import com.postsuite.derailer.infrastructure.managers.railway.RailwayInfrastructureManager;
import com.postsuite.derailer.mapper.DerailmentMapper;
import com.postsuite.derailer.models.response.DerailmentModel;
import com.postsuite.derailer.repository.DerailerRunRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
public class InfrastructureService {

    // todo - figure out how to inject a certain service manager
    private final RailwayInfrastructureManager infrastructureManager;
    private final DerailerRunRepository runRepository;
    private final DerailmentMapper derailmentMapper;

    @Inject
    public InfrastructureService(final RailwayInfrastructureManager infrastructureManager, final DerailerRunRepository runRepository, final DerailmentMapper derailmentMapper) {
        this.infrastructureManager = infrastructureManager;
        this.runRepository = runRepository;
        this.derailmentMapper = derailmentMapper;
    }

    public Uni<DerailmentModel> triggerDerailment(final int amount, final Duration time, final List<String> blacklistedServices) {
        final Uni<List<DerailedEntity>> derailedEntities = this.infrastructureManager.derail(amount, blacklistedServices);
        return derailedEntities.map(entities -> new DerailmentEntity()
                        .setAffectedEntities(entities)
                        .setStartTimestamp(Instant.now())
                        .setRollbackTimestamp(Instant.now().plus(time.getSeconds(), ChronoUnit.SECONDS)))
                .flatMap(this.runRepository::persistOrUpdate)
                .map(this.derailmentMapper::toModel);
    }

}
