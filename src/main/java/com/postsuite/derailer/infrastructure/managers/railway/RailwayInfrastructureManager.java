package com.postsuite.derailer.infrastructure.managers.railway;

import com.postsuite.derailer.entities.DerailedEntity;
import com.postsuite.derailer.entities.DerailmentState;
import com.postsuite.derailer.entities.EntityType;
import com.postsuite.derailer.infrastructure.managers.InfrastructureManager;
import com.postsuite.derailer.infrastructure.managers.railway.auth.RailwayAuthentication;
import com.postsuite.derailer.infrastructure.managers.railway.client.RailwayClientProvider;
import com.postsuite.derailer.infrastructure.managers.railway.client.model.DeploymentList;
import com.postsuite.derailer.infrastructure.managers.railway.client.model.DeploymentResponse;
import com.postsuite.derailer.infrastructure.managers.railway.client.model.ProjectResponse;
import com.postsuite.derailer.util.MutinyUtil;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.*;

@Slf4j
@ApplicationScoped
public class RailwayInfrastructureManager implements InfrastructureManager {

    @Inject
    RailwayClientProvider railwayClientProvider;

    @Inject
    RailwayAuthentication railwayAuthentication;

    @ConfigProperty(name = "railway.project.id")
    String railwayProjectId;

    @ConfigProperty(name = "railway.environment.id")
    String railwayEnvironmentId;

    @Override
    public Uni<List<DerailedEntity>> derail(final int amount, final List<String> blacklistedServices) {
        return this.railwayAuthentication.getBearerToken()
                .map(token -> "Bearer " + token)
                .flatMap(token -> this.getEligibleProjects(token, blacklistedServices)
                        .map(projects -> this.chooseRandomProjects(new ArrayList<>(projects), amount))
                        .flatMap(services -> MutinyUtil.uniListFailFast(services, service -> this.handleServiceDerailment(service, token))));
    }

    private Uni<DerailedEntity> handleServiceDerailment(final ProjectResponse.Node service, final String token) {
        return this.getEligibleDeployment(token, service.getId())
                .flatMap(node -> {
                    if (node.isEmpty()) {
                        return Uni.createFrom().nullItem();
                    }
                    return this.removeAndCreateDerailedEntity(node.get(), service, token);
                });
    }

    private Uni<DerailedEntity> removeAndCreateDerailedEntity(final DeploymentResponse.Node deploy, final ProjectResponse.Node service, final String token) {
        return this.removeDeployment(token, deploy.getId())
                .map(success -> this.createDerailedEntity(deploy, service));
    }

    private DerailedEntity createDerailedEntity(final DeploymentResponse.Node deploy, final ProjectResponse.Node service) {
        return new DerailedEntity()
                .setEntityType(EntityType.INSTANCE)
                .setIdentifier(deploy.getId())
                .setParentIdentifier(service.getId())
                .setParentDisplayName(service.getName())
                .setState(DerailmentState.DERAILED)
                .setLink(String.format("https://railway.com/project/%s/service/%s?environmentId=%s&id=%s#deploy", this.railwayProjectId, service.getId(), this.railwayEnvironmentId, deploy.getId())); // todo
    }


    private Uni<Void> removeDeployment(final String authorizationToken, final String id) {
        return this.railwayClientProvider.deploymentRemove(id, authorizationToken)
                .onFailure().recoverWithNull().replaceWithVoid();
    }

    private Uni<Optional<DeploymentResponse.Node>> getEligibleDeployment(final String authorizationToken, final String serviceId) {
        return this.railwayClientProvider.deployments(
                        new DeploymentList()
                                .setServiceId(serviceId)
                                .setProjectId(this.railwayProjectId)
                                .setEnvironmentId(this.railwayEnvironmentId),
                        10,
                        null,
                        authorizationToken)
                .map(deploymentResponse -> deploymentResponse.getEdges().stream().map(DeploymentResponse.Edge::getNode)
                        .filter(node -> node.getStatus().equalsIgnoreCase("SUCCESS")).findFirst());
    }

    private Uni<List<ProjectResponse.Node>> getEligibleProjects(final String authorizationToken, final List<String> blacklistedServices) {
        final Uni<ProjectResponse> projectInfoUni = this.railwayClientProvider.project(this.railwayProjectId, authorizationToken)
                .onFailure().invoke(throwable -> log.error("error: ", throwable))
                .invoke(response -> log.info("Details of project: {}", response));

        return projectInfoUni.map(projectResponse -> {
            final List<ProjectResponse.Node> allNodes = projectResponse.getServices().getEdges().stream().map(ProjectResponse.Edge::getNode).toList();
            return allNodes.stream().filter(project -> !blacklistedServices.contains(project.getId())).toList();
        });
    }

    private List<ProjectResponse.Node> chooseRandomProjects(final List<ProjectResponse.Node> availableProjects, final int amount) {
        if (availableProjects.size() <= amount) {
            return availableProjects;
        }
        Collections.shuffle(availableProjects, new Random());
        return availableProjects.subList(0, amount);
    }

    @Override
    public Uni<List<DerailedEntity>> getUpdates(final List<DerailedEntity> updateRequests) {
        return null;
    }

    @Override
    public Uni<List<DerailedEntity>> rollback(final List<DerailedEntity> entitiesToRollback) {
        return this.railwayAuthentication.getBearerToken()
                .map(token -> "Bearer " + token)
                .flatMap(token -> MutinyUtil.uniListFailFast(entitiesToRollback, entity -> this.railwayClientProvider.serviceInstanceDeploy(entity.getParentIdentifier(), this.railwayEnvironmentId, null, true, token)
                        .onFailure().recoverWithNull()
                        .replaceWithVoid()
                        .onItem()
                        .invoke(unused -> entity.setState(DerailmentState.COMPLETE))
                        .replaceWith(entity)));

    }
}
