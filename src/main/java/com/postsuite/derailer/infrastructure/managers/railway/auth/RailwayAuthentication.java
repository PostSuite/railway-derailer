package com.postsuite.derailer.infrastructure.managers.railway.auth;

import com.postsuite.derailer.infrastructure.auth.InfrastructureAuthProvider;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Getter
public class RailwayAuthentication implements InfrastructureAuthProvider {

    @ConfigProperty(name = "railway.api.token")
    String railwayApiToken;

    @Override
    public Uni<String> getBearerToken() {
        return Uni.createFrom().item(this.railwayApiToken);
    }
}
