package com.postsuite.derailer.orchestrators;

import com.postsuite.derailer.mapper.DerailmentMapper;
import com.postsuite.derailer.models.response.DerailmentModel;
import com.postsuite.derailer.services.DerailmentService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class DerailmentOrchestrator {

    private final DerailmentService derailmentService;
    private final DerailmentMapper derailmentMapper;

    public Uni<List<DerailmentModel>> getDerailments(final int page, final int perPage) {
        return this.derailmentService.getEntities(page, perPage)
                .map(this.derailmentMapper::toModels);
    }

    public Uni<DerailmentModel> getDerailment(final String identifier) {
        return this.derailmentService.getDerailment(identifier)
                .map(this.derailmentMapper::toModel);
    }

}
