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

@Provider
@Slf4j
public class LogFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        log.info("============== RECEIVED REQUEST ==============");
        if (requestContext.getUriInfo().getPathParameters() != null && !requestContext.getUriInfo().getPathParameters().isEmpty()) {
            log.info("QUERY PARAMS: {}", requestContext.getUriInfo().getPathParameters());
        }
        log.info("METHOD: {}", requestContext.getMethod());
        byte[] entity = requestContext.getEntityStream().readAllBytes();
        log.info("BODY: {}", new String(entity));
        requestContext.setEntityStream(new ByteArrayInputStream(entity));
        log.info("============== RECEIVED REQUEST ==============");
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (!requestContext.getUriInfo().getPath().equals("/")) {
            log.info("============== RESPONSE ==============");
            log.info("Response: Status: {}", responseContext.getStatus());
            if (responseContext.getEntity() != null) {
                log.info("Body: {}", Utility.getJson(responseContext.getEntity()));
            }
            log.info("============== RESPONSE ==============");
        }
    }

}