package com.postsuite.derailer.models.response;

import com.postsuite.derailer.entities.DerailmentState;
import com.postsuite.derailer.entities.EntityType;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import java.util.Map;

@Data
@Accessors(chain = true)
public class DerailedModel {

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

    private Map<String, String> meta;

}
