package com.postsuite.derailer.entities;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Represents an instance of a run of the derailer
 */
@Data
@Accessors(chain = true)
@MongoEntity(collection = "runs", database = "derailer")
public class DerailmentEntity {

    @BsonId
    private String identifier = UUID.randomUUID().toString();

    /**
     * The timestamp we started the derailment
     */
    private Instant startTimestamp;

    /**
     * The timestamp we should roll back the derailment
     */
    private Instant rollbackTimestamp;

    @BsonIgnore
    public int getBlastRadius() {
        if (this.affectedEntities == null) {
            return 0;
        }
        return this.affectedEntities.size();
    }

    /**
     * Get the lowest state returned by all objects
     *
     * @return the current state of the derailment
     */
    @BsonIgnore
    public DerailmentState getState() {
        if (this.affectedEntities == null || this.affectedEntities.isEmpty()) {
            return DerailmentState.DERAILING;
        }
        DerailmentState minState = DerailmentState.COMPLETE;
        for (final DerailedEntity entity : this.affectedEntities) {
            final DerailmentState entityState = entity.getState();
            if (entityState.ordinal() < minState.ordinal()) {
                minState = entityState;
            }
        }
        return minState;
    }

    private List<@NonNull DerailedEntity> affectedEntities;

}
