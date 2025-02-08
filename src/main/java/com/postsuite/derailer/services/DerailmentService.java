package com.postsuite.derailer.services;

import com.postsuite.derailer.entities.DerailmentEntity;
import com.postsuite.derailer.repository.DerailerRunRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class DerailmentService {

    private final DerailerRunRepository runRepository;

    public Uni<List<DerailmentEntity>> getEntities(final int page, final int perPage) {
        return this.runRepository.find(new Document("_id", new Document("$ne", "test")), new Document("startTimestamp", -1))
                .page(page - 1, perPage)
                .list();
    }

    public Uni<DerailmentEntity> getDerailment(final String id) {
        return this.runRepository.findById(id);
    }

}
