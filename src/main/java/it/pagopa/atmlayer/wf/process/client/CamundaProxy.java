package it.pagopa.atmlayer.wf.process.client;

import java.io.File;
import java.util.List;

import org.camunda.bpm.engine.rest.dto.runtime.StartProcessInstanceDto;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.process.client.bean.CamundaBodyRequestDto;
import it.pagopa.atmlayer.wf.process.client.bean.TaskCompleteDto;
import it.pagopa.atmlayer.wf.process.client.bean.TaskDto;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@RegisterRestClient(configKey = "camunda-proxy")
public interface CamundaProxy {

    @POST
    @Path("/deployment/create")
    RestResponse<Object> deploy(@RestForm File data);

    @POST
    @Path("/process-definition/key/{key}/start")
    RestResponse<StartProcessInstanceDto> startInstance(@PathParam("key") String key, CamundaBodyRequestDto body);

    @POST
    @Path("/task")
    RestResponse<List<TaskDto>> getList(CamundaBodyRequestDto body);

    @POST
    @Path("/task/{id}/complete")
    RestResponse<TaskCompleteDto> complete(@PathParam("id") String id, CamundaBodyRequestDto body);
}
