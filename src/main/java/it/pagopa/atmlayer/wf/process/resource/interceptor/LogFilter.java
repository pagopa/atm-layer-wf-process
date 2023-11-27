package it.pagopa.atmlayer.wf.process.resource.interceptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import it.pagopa.atmlayer.wf.process.util.Utility;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Pasquale Sansonna
 * 
 * <p>The {@code LogFilter} class is a JAX-RS container filter that intercepts incoming and outgoing HTTP requests
 * and logs relevant information for debugging and monitoring purposes. It logs details such as received request
 * parameters, HTTP method, request body, and response details. </p>
 * 
 * <p>When a request is received, the filter logs information about the request, including parameters, method, and body.
 * The request body is then reset for further processing. For responses, the filter logs response status and body
 * (if available), excluding the root path ("/"). </p>
 * 
 * 
 * @see jakarta.ws.rs.container.ContainerRequestFilter
 * @see jakarta.ws.rs.container.ContainerResponseFilter
 */
@Provider
@Slf4j
public class LogFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Intercepts the incoming request and logs relevant details such as parameters, method, and body.
     *
     * @param requestContext The context representing the incoming request.
     * @throws IOException If an I/O exception occurs during the filter processing.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        log.info("============== RECEIVED REQUEST ==============");
        if (requestContext.getUriInfo().getPathParameters() != null && !requestContext.getUriInfo().getPathParameters().isEmpty()) {
            log.info("PARAMS: {}", requestContext.getUriInfo().getPathParameters());
        }
        log.info("METHOD: {}", requestContext.getMethod());
        byte[] entity = requestContext.getEntityStream().readAllBytes();
        log.info("BODY: {}", new String(entity));
        requestContext.setEntityStream(new ByteArrayInputStream(entity));
        log.info("============== RECEIVED REQUEST ==============");
    }

    /**
     * Intercepts the outgoing response and logs relevant details such as status and body.
     *
     * @param requestContext  The context representing the incoming request.
     * @param responseContext The context representing the outgoing response.
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (!requestContext.getUriInfo().getPath().equals("/")) {
            log.info("============== RESPONSE ==============");
            log.info("Response: Status: {}", responseContext.getStatus());
            if (responseContext.getEntity() != null) {
                log.info("BODY: {}", Utility.getJson(responseContext.getEntity()));
            }
            log.info("============== RESPONSE ==============");
        }
    }

}