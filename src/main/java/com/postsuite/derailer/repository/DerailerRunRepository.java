package com.postsuite.derailer.repository;

import com.postsuite.derailer.entities.DerailmentEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DerailerRunRepository implements ReactivePanacheMongoRepositoryBase<DerailmentEntity, String> {
}