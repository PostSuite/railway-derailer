package com.postsuite.derailer.services;

import com.postsuite.derailer.entities.PauseEntity;
import com.postsuite.derailer.models.PauseModel;
import com.postsuite.derailer.repository.PauseRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@ApplicationScoped
@RequiredArgsConstructor
public class PauseService {

    private final PauseRepository pauseRepository;

    public Uni<PauseEntity> savePause(final PauseModel pauseModel) {
        return this.pauseRepository.persistOrUpdate(new PauseEntity().setEndTimestamp(pauseModel.getEndTimestamp()).setStartTimestamp(pauseModel.getStartTimestamp()).setReason(pauseModel.getReason()));
    }

    public Uni<Void> unpause() {
        return this.pauseRepository.deleteAll().replaceWithVoid();
    }

    public Uni<Boolean> isPaused() {
        return this.getPause().map(pause -> pause != null && pause.getEndTimestamp().isBefore(Instant.now()));
    }

    public Uni<PauseEntity> getPause() {
        return this.pauseRepository.findById("pause");
    }

}
