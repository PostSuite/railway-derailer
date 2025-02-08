package com.postsuite.derailer.entities;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.Instant;

@Data
@Accessors(chain = true)
@MongoEntity(collection = "pause", database = "derailer")
public class PauseEntity {

    @BsonId
    private String identifier = "pause";

    private Instant startTimestamp;
    private Instant endTimestamp;
    private String reason;

}
