package it.pagopa.atmlayer.wf.process.client.camunda;

import java.io.File;
import java.util.List;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaBodyRequestDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaResourceDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaStartProcessInstanceDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaTaskDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaVariablesDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.InstanceDto;
import it.pagopa.atmlayer.wf.process.client.camunda.filter.CamundaBasicAuthFilter;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@RegisterRestClient(configKey = "camunda-rest-client")
@RegisterProvider(CamundaBasicAuthFilter.class)
public interface CamundaRestClient {

    @POST
    @Path("/deployment/create")
    RestResponse<Object> deploy(@RestForm File data);

    @GET
    @Path("/deployment/{id}/resources")
    RestResponse<List<CamundaResourceDto>> getResources(@PathParam("id") String id);

    @GET
    @Path("/deployment/{id}/resources/{resourceId}/data")
    RestResponse<String> getResourceBinary(@PathParam("id") String id, @PathParam("resourceId") String resourceId);

    @POST
    @Path("/process-definition/{id}/start")
    RestResponse<CamundaStartProcessInstanceDto> startInstance(@PathParam("id") String id, CamundaBodyRequestDto body);

    @POST
    @Path("/task")
    RestResponse<List<CamundaTaskDto>> getList(CamundaBodyRequestDto body);

    @POST
    @Path("/task/{id}/complete")
    RestResponse<Object> complete(@PathParam("id") String id, CamundaBodyRequestDto body);

    @GET
    @Path("/task/{id}/variables")
    RestResponse<CamundaVariablesDto> getTaskVariables(@PathParam("id") String id);

    @GET
    @Path("/process-instance")
    RestResponse<List<InstanceDto>> getInstanceActivity(@QueryParam("businessKey") String businessKey);

}
