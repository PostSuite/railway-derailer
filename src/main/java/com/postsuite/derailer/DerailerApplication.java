package com.postsuite.derailer;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@OpenAPIDefinition(
        info =
        @Info(
                title = "Railway Derailer",
                version = "1.0.0",
                description =
                        """
                                Derailer is a tool that helps test for how services handle random dependency failures, using Railway.
                                """),
        externalDocs =
        @ExternalDocumentation(
                description =
                        """
                                Derailer is a tool that helps test for how services handle random dependency failures, using Railway.
                                """,
                url = "https://<OpenIPALocation>"))
public class DerailerApplication extends Application {
}
