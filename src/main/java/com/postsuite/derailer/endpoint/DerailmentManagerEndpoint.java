package com.postsuite.derailer.endpoint;

import com.postsuite.derailer.models.PauseModel;
import com.postsuite.derailer.models.response.DerailmentModel;
import com.postsuite.derailer.orchestrators.ManagementOrchestrator;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Tag(name = "Derailment Manager API")
@Path("/manage_derailments")
@RequiredArgsConstructor
public class DerailmentManagerEndpoint {

    private final ManagementOrchestrator managementOrchestrator;

    @Operation(
            operationId = "Derailments.summon",
            summary = "Summon a derailment")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/summon")
    public Uni<DerailmentModel> summonDerailment() {
        return managementOrchestrator.triggerDerailment();
    }

    @Operation(
            operationId = "Derailments.pause",
            summary = "Take a break from derailments for a time period")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/pause")
    public Uni<PauseModel> pause() {
        return Uni.createFrom().nullItem();
    }

    @Operation(
            operationId = "Derailments.getPause",
            summary = "Check if there's an ongoing pause on derailments")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/pause")
    public Uni<PauseModel> isPaused() {
        return Uni.createFrom().nullItem();
    }

    @Operation(
            operationId = "Derailments.unpause",
            summary = "Unpause an ongoing derailment pause early")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/pause")
    public Uni<PauseModel> unpause() {
        return Uni.createFrom().nullItem();
    }

    @Operation(
            operationId = "Derailments.rollback",
            summary = "Rollback an ongoing derailment")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/rollback/{identifier}")
    public Uni<Void> summonDerailment(@PathParam("identifier") final UUID identifier) {
        return Uni.createFrom().nullItem();
    }

}
