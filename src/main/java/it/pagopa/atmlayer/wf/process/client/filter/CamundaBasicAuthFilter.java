package it.pagopa.atmlayer.wf.process.client.filter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Provider
public class CamundaBasicAuthFilter implements ClientRequestFilter {

    @ConfigProperty(name = "wf-process.camunda.auth-username")
    private String username;

    @ConfigProperty(name = "wf-process.camunda.auth-password")
    private String password;

    @Override
    public void filter(ClientRequestContext requestContext) {
        String authString = username + ":" + password;
        String base64Auth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, "Basic " + base64Auth);
    }
}
