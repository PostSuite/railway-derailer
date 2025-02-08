package com.postsuite.derailer.models.response;

import com.postsuite.derailer.entities.DerailedEntity;
import com.postsuite.derailer.entities.DerailmentState;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Model representing a derailment
 */
@Data
@Accessors(chain = true)
public class DerailmentModel {
    private UUID identifier;

    /**
     * The timestamp we started the derailment
     */
    private Instant startTimestamp;

    /**
     * The timestamp we should roll back the derailment
     */
    private Instant rollbackTimestamp;

    private int blastRadius;
    private DerailmentState state;
    private boolean isRolledBack;
    private List<DerailedEntity> affectedEntities;

}
