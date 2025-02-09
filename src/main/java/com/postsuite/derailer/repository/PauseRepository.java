package com.postsuite.derailer.repository;

import com.postsuite.derailer.entities.PauseEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PauseRepository implements ReactivePanacheMongoRepositoryBase<PauseEntity, String> {
}