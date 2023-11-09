package it.pagopa.atmlayer.wf.process.client.model;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.process.client.model.bean.ModelBpmnDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@RegisterRestClient(configKey = "model-rest-client")
public interface ModelRestClient {
    
    @GET
    @Path("bpmn/function/{functionType}/bank/{acquirerId}/branch/{branchId}/terminal/{terminalId}")
    RestResponse<ModelBpmnDto> findBPMNByTriad(@PathParam("functionType") String functionType, @PathParam("acquirerId") String acquirerId, @PathParam("branchId") String branchId, @PathParam("terminalId") String terminalId);
}
