package com.banco.api;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "false");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Accept");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    }
}
