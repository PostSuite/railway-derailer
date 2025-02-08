package com.postsuite.derailer.infrastructure.managers;

import com.postsuite.derailer.entities.DerailedEntity;
import io.smallrye.mutiny.Uni;

import java.util.List;

/**
 * An infrastructure manager defines a service provider who hosts services we'd like to interrupt.
 */
public interface InfrastructureManager {

    /**
     * Derail a given number of
     *
     * @param amount the amount of services to impact
     * @return a list of derailed entities
     */
    Uni<List<DerailedEntity>> derail(final int amount, final List<String> blacklistedServices);

    /**
     * Request for updates on the status of impacted entities
     *
     * @param updateRequests the entities that were previously derailed that we want updates on.
     * @return a list of derailment entities that reflect the current states.
     */
    Uni<List<DerailedEntity>> getUpdates(final List<DerailedEntity> updateRequests);

    /**
     * Rollback a list of entities to a working state.
     *
     * @param entitesToRollback the list of entities we'd like to go back to a previous state.
     * @return a list of entities that are rolling back (or have already rolled back if sync).
     */
    Uni<List<DerailedEntity>> rollback(final List<DerailedEntity> entitesToRollback);

}
