package com.banco.api;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Responde a preflight OPTIONS para que el navegador permita llamadas desde otro origen (desarrollo).
 */
@Provider
public class CorsRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            requestContext.abortWith(Response.noContent()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept")
                    .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                    .build());
        }
    }
}
