package com.postsuite.derailer.endpoint;

import com.postsuite.derailer.models.PauseModel;
import com.postsuite.derailer.models.response.DerailmentModel;
import com.postsuite.derailer.orchestrators.ManagementOrchestrator;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.ZonedDateTime;

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
        return this.managementOrchestrator.triggerDerailment();
    }

    @Operation(
            operationId = "Derailments.next",
            summary = "Get the time of the next automatic derailment (ignoring pauses)")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/next")
    public Uni<ZonedDateTime> nextDerailment() {
        return this.managementOrchestrator.getNextDerailment();
    }

    @Operation(
            operationId = "Derailments.pause",
            summary = "Take a break from derailments for a time period")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/pause")
    public Uni<PauseModel> pause(@Valid final PauseModel request) {
        return this.managementOrchestrator.pause(request);
    }

    @Operation(
            operationId = "Derailments.getPause",
            summary = "Check if there's an ongoing pause on derailments")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/pause")
    public Uni<PauseModel> getActivePause() {
        return this.managementOrchestrator.getPause();
    }

    @Operation(
            operationId = "Derailments.unpause",
            summary = "Unpause an ongoing derailment pause early")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/pause")
    public Uni<Void> unpause() {
        return this.managementOrchestrator.unpause();
    }

    @Operation(
            operationId = "Derailments.rollback",
            summary = "Rollback an ongoing derailment")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/rollback/{identifier}")
    public Uni<Void> rollbackDerailment() {
        return this.managementOrchestrator.triggerRollback(true);
    }

}
