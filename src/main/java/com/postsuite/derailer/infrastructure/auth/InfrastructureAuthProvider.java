package com.postsuite.derailer.infrastructure.auth;

import io.smallrye.mutiny.Uni;

public interface InfrastructureAuthProvider {

    Uni<String> getBearerToken();

}
