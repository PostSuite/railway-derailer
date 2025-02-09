package com.postsuite.derailer.endpoint;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Derailment Manager API")
@Path("/")
public class BaseEndpoint {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Uni<Response> redirectToDestination() {
        return Uni.createFrom().item(Response.ok(this.getClass().getClassLoader()
                .getResourceAsStream("META-INF/resources/index.html")).build());
    }

}
