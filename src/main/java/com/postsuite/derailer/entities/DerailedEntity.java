package com.postsuite.derailer.entities;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * An entity representing a derailed object (server, instance, project, database)
 */
@Data
@Accessors(chain = true)
public class DerailedEntity {

    // identifier of the thing removed
    private String identifier;

    // identifier of the parent of the thing removed (nullable)
    @Nullable
    private String parentIdentifier;

    @Nullable
    private String parentDisplayName;
    private DerailmentState state;
    private String link;
    private EntityType entityType;

    // generic extra data
    private Map<String, String> meta;

}
