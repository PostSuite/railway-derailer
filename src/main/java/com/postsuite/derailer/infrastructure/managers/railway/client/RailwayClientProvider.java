package com.postsuite.derailer.infrastructure.managers.railway.client;

import com.postsuite.derailer.infrastructure.client.InfrastructureClientProvider;
import com.postsuite.derailer.infrastructure.managers.railway.client.model.DeploymentList;
import com.postsuite.derailer.infrastructure.managers.railway.client.model.DeploymentResponse;
import com.postsuite.derailer.infrastructure.managers.railway.client.model.ProjectResponse;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.graphql.client.typesafe.api.Header;
import io.smallrye.mutiny.Uni;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

@GraphQLClientApi(configKey = "railway")
public interface RailwayClientProvider extends InfrastructureClientProvider {

    @Query
    Uni<ProjectResponse> project(@NotNull String id, @Header(name = "Authorization") String authorization);

    @Query
    Uni<DeploymentResponse> deployments(@NotNull DeploymentList input, Integer first, String after, @Header(name = "Authorization") String authorization);

    @Mutation
    Uni<Void> deploymentRemove(@NotNull String id, @Header(name = "Authorization") String authorization);

    @Mutation
    Uni<Void> serviceInstanceDeploy(@NotNull String serviceId, @NotNull String environmentId, String commitSha, boolean latestCommit, @Header(name = "Authorization") String authorization);
}
