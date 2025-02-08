package com.postsuite.derailer.endpoint;

import com.postsuite.derailer.models.response.DerailmentModel;
import com.postsuite.derailer.orchestrators.DerailmentOrchestrator;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "Derailments API")
@Path("/derailments")
@RequiredArgsConstructor
public class DerailmentsEndpoint {

    private final DerailmentOrchestrator derailmentOrchestrator;

    @Operation(
            operationId = "Derailments.list",
            summary = "Get a list of service derailments")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/")
    public Uni<List<DerailmentModel>> establishConnection(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("per_page") @DefaultValue("100") final int perPage) {
        return this.derailmentOrchestrator.getDerailments(page, perPage);
    }

    @Operation(
            operationId = "Derailments.get",
            summary = "Get details about a specific derailment")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{identifier}")
    public Uni<DerailmentModel> details(@PathParam("identifier") final UUID identifier) {
        return this.derailmentOrchestrator.getDerailment(identifier.toString());
    }

}
