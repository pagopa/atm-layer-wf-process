package it.pagopa.atmlayer.wf.process.client;

import java.io.File;
import java.util.List;

import org.camunda.bpm.engine.rest.dto.runtime.StartProcessInstanceDto;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.process.client.bean.CamundaBodyRequestDto;
import it.pagopa.atmlayer.wf.process.client.bean.CamundaVariablesDto;
import it.pagopa.atmlayer.wf.process.client.filter.CamundaBasicAuthFilter;
import it.pagopa.atmlayer.wf.process.client.bean.CamundaTaskDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@RegisterRestClient(configKey = "camunda-rest-client")
@RegisterProvider(CamundaBasicAuthFilter.class)
public interface CamundaRestClient {
    
    @POST
    @Path("/deployment/create")
    RestResponse<Object> deploy(@RestForm File data);

    @POST
    @Path("/process-definition/key/{key}/start")
    RestResponse<StartProcessInstanceDto> startInstance(@PathParam("key") String key, CamundaBodyRequestDto body);

    @POST
    @Path("/task")
    RestResponse<List<CamundaTaskDto>> getList(CamundaBodyRequestDto body);

    @POST
    @Path("/task/{id}/complete")
    RestResponse<Object> complete(@PathParam("id") String id, CamundaBodyRequestDto body);

    @GET
    @Path("/task/{id}/variables")
    RestResponse<CamundaVariablesDto> getTaskVariables(@PathParam("id") String id);

}
