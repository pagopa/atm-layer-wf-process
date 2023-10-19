package it.pagopa.atmlayer.wf.process.resource;

import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.process.bean.TaskRequest;
import it.pagopa.atmlayer.wf.process.bean.TaskResponse;
import it.pagopa.atmlayer.wf.process.bean.VariableRequest;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse;
import it.pagopa.atmlayer.wf.process.service.ProcessService;
import it.pagopa.atmlayer.wf.process.util.Constants;
import it.pagopa.atmlayer.wf.process.util.Utility;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Pasquale Sansonna
 * 
 *         <p>
 *         This class defines REST endpoints for managing BPM processes through
 *         Camunda.
 *         </p>
 */
@Slf4j
@Path("/api/v1/processes")
public class ProcessResource {

    @Inject
    ProcessService processService;

    /**
     * Endpoint to deploy a BPMN process definition to Camunda.
     *
     * @param request The task request.
     * @return A `RestResponse` containing the deployment outcome.
     */
    @POST
    @Path("/deploy")
    public RestResponse<Object> deploy() {
        log.info("DEPLOY - Request received. . .");
        return processService.deploy();
    }

    /**
     * Endpoint to start a BPMN process and to retireve active tasks.
     *
     * @param request The task request.
     * @return A `RestResponse` containing information about the active tasks.
     */
    @POST
    @Path("/start")
    public RestResponse<TaskResponse> startProcess(TaskRequest request) {
        log.info("START - TaskRequest received. . .");
        log.info("START - Request body\n{}", Utility.getJson(request));

        RestResponse<TaskResponse> taskResponse;
        try {
            /*
             * Starting camunda process
             */
            String businessKey = processService.start(request.getTransactionId(), request.getFunctionId(),
                    request.getDeviceInfo(), request.getVariables());
            if (!businessKey.equals(Constants.EMPTY)) {
                /*
                 * Retrieve active tasks
                 */
                taskResponse = processService.getActiveTasks(businessKey);
            } else {
                taskResponse = RestResponse.status(RestResponse.Status.BAD_REQUEST);
            }
        } catch (RuntimeException e) {
            log.error("START - Exception during start process: ", e);
            taskResponse = RestResponse.serverError();
        }

        log.info("START - Response body\n{}", Utility.getJson(taskResponse.getEntity()));
        return taskResponse;
    }

    /**
     * Endpoint to complete a Camunda task and to retireve active tasks.
     *
     * @param request The task request.
     * @return A `RestResponse` containing information about active tasks.
     */
    @POST
    @Path("/next")
    public RestResponse<TaskResponse> next(TaskRequest request) {
        log.info("NEXT - TaskRequest received. . .");
        log.info("NEXT - Request body\n:{}", Utility.getJson(request));

        RestResponse<TaskResponse> taskResponse;
        try {
            /*
             * Checking presence of taskId for complete
             */
            if (request.getTaskId() == null) {
                log.error("NEXT - Next failed! taskId is missing.");
                taskResponse = RestResponse.status(RestResponse.Status.BAD_REQUEST);
            } else {
                /*
                 * Complete camunda task
                 */
                if (processService.complete(request.getTaskId(), request.getVariables())) {
                    /*
                     * Retrieve active tasks
                     */
                    String businessKey = request.getTransactionId();
                    taskResponse = processService.getActiveTasks(businessKey);
                } else {
                    taskResponse = RestResponse.status(RestResponse.Status.BAD_REQUEST);
                }
            }
        } catch (RuntimeException e) {
            log.error("NEXT - Exception during start process: ", e);
            taskResponse = RestResponse.serverError();
        }

        log.info("NEXT - Response body\n:{}" , Utility.getJson(taskResponse.getEntity()));
        return taskResponse;
    }

    @POST
    @Path("/variables")
    public RestResponse<VariableResponse> variables(VariableRequest request) {
        log.info("VARIABLES - VariableRequest received. . .");
        log.info("VARIABLES - Request body\n:{}", Utility.getJson(request));

        RestResponse<VariableResponse> variableResponse;

        try {
            variableResponse = processService.getTaskVariables(request.getTaskId(), request.getVariables(),
                    request.getButtons());
        } catch (RuntimeException e) {
            log.error("NEXT - Exception during start process: ", e);
            variableResponse = RestResponse.serverError();
        }

        log.info("VARIABLES - Response body\n:{}", Utility.getJson(variableResponse.getEntity()));
        return variableResponse;
    }

}
