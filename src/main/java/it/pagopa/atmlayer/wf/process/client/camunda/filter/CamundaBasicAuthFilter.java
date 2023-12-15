package it.pagopa.atmlayer.wf.process.client.camunda.filter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


import it.pagopa.atmlayer.wf.process.util.Properties;

/**
 * @author Pasquale Sansonna
 * 
 * The {@code CamundaBasicAuthFilter} class is a JAX-RS client request filter that adds Basic Authentication headers
 * to outgoing requests made to the Camunda REST API. It uses the credentials provided in the injected
 * {@link Properties} instance.
 *
 *
 * @see ClientRequestFilter
 * @see Properties
 */
@ApplicationScoped
@Provider
public class CamundaBasicAuthFilter implements ClientRequestFilter {

    @Inject
    private Properties properties;

    /**
     * Filters the client request context to add Basic Authentication headers.
     *
     * @param requestContext The context of the outgoing client request.
     */
    @Override
    public void filter(ClientRequestContext requestContext) {
        String authString = properties.camundaAuthUsername() + ":" + properties.camundaAuthPassword();
        String base64Auth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, "Basic " + base64Auth);
    }
}
