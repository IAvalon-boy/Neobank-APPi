package com.banco;

import com.banco.api.CorsRequestFilter;
import com.banco.api.CorsResponseFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Punto de entrada JAX-RS (Jersey). El servlet está mapeado en {@code /api/*}.
 */
@ApplicationPath("/")
public class RestApplication extends ResourceConfig {

    public RestApplication() {
        packages("com.banco.api");
        register(JacksonFeature.class);
        register(CorsRequestFilter.class);
        register(CorsResponseFilter.class);
    }
}
