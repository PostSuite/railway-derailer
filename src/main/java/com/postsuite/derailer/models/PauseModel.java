package com.postsuite.derailer.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

/**
 * Model representing a 'take a break' request
 */
@Data
@Accessors(chain = true)
public class PauseModel {

    private Instant startTimestamp;
    private Instant endTimestamp;
    private String reason;

}
