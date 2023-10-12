package it.pagopa.atmlayer.wf.process.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.rest.dto.runtime.StartProcessInstanceDto;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import io.quarkus.logging.Log;
import it.pagopa.atmlayer.wf.process.bean.TaskResponse;
import it.pagopa.atmlayer.wf.process.client.CamundaProxy;
import it.pagopa.atmlayer.wf.process.client.bean.CamundaBodyRequestDto;
import it.pagopa.atmlayer.wf.process.client.bean.TaskCompleteDto;
import it.pagopa.atmlayer.wf.process.client.bean.TaskDto;
import it.pagopa.atmlayer.wf.process.util.Constants;
import it.pagopa.atmlayer.wf.process.util.Utility;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Pasquale Sansonna
 * 
 *         <p>This class provides services for managing operations related to BPM
 *         processes
 *         through Camunda.
 *         It can be injected into other parts of the application.</p>
 */
@ApplicationScoped
public class ProcessService {

    @RestClient
    CamundaProxy camundaProxy;

    /**
     * Deploys a BPMN process definition in Camunda.
     *
     * @param bpmnFilePath The path to the BPMN file to deploy.
     * @return A `RestResponse` containing the deployment outcome.
     */
    public RestResponse<Object> deploy(String bpmnFilePath) {
        RestResponse<Object> response;

        try {
            final File bpmn = new File(bpmnFilePath);
            // Camunda communication
            response = camundaProxy.deploy(bpmn);

            if (response.getStatus() == RestResponse.Status.OK.getStatusCode()) {
                response = RestResponse.ok(response.getEntity());
                Log.info("DEPLOY - BPMN deployed!");
            } else {
                response = RestResponse.status(RestResponse.Status.BAD_REQUEST);
            }
        } catch (RuntimeException e) {
            Log.error("Error during bpmn deployment: ", e);
            response = RestResponse.serverError();
        }

        return response;
    }

    /**
     * Starts a new BPMN process in Camunda.
     *
     * @param transactionId The transaction ID associated with the process.
     * @param variables     The variables to associate with the process.
     * @return The business key of the started process or an empty string in case of
     *         an error.
     */
    public String start(String transactionId, Map<String, Object> variables) {
        RestResponse<StartProcessInstanceDto> camundaStartInstanceResponse = camundaStartProcess(transactionId,
                variables);

        String businessKey;
        if (camundaStartInstanceResponse.getStatus() != RestResponse.Status.OK.getStatusCode()) {
            businessKey = Constants.EMPTY;
            Log.error("START - Start process instance failed!");
        } else {
            businessKey = camundaStartInstanceResponse.getEntity().getBusinessKey();
            Log.info("START - Process started! Business key: " + businessKey);
        }

        return businessKey;
    }

    /**
     * Retrieves the active tasks associated with a BPM process.
     *
     * @param businessKey The business key of the process.
     * @return A `RestResponse` containing the retrieved tasks.
     */
    public RestResponse<TaskResponse> getActiveTasks(String businessKey) {
        RestResponse<TaskResponse> taskResponse;
        RestResponse<List<TaskDto>> camundaGetListResponse = getCamundaTaskList(businessKey);

        // TODO retrieve variables for each task from camunda and return them to the
        // task microservice
        if (camundaGetListResponse.getStatus() == RestResponse.Status.OK.getStatusCode()) {
            Log.info("NEXT - Retrieving active tasks. . .");
            List<String> taskIds = camundaGetListResponse.getEntity()
                    .stream()
                    .map(TaskDto::getId)
                    .collect(Collectors.toCollection(ArrayList::new));

            taskResponse = RestResponse.ok(TaskResponse.builder().transactionId(businessKey).tasks(taskIds).build());
            Log.info("NEXT - Tasks retrieved!");
        } else {
            Log.error("NEXT - Get list of tasks failed!");
            taskResponse = RestResponse.status(RestResponse.Status.BAD_REQUEST);
        }

        return taskResponse;
    }

    /**
     * Completes a task in Camunda.
     *
     * @param taskId    The ID of the task to complete.
     * @param variables The variables to associate with the completion.
     * @return `true` if the task is completed successfully, `false` otherwise.
     */
    public boolean complete(String taskId, Map<String, Object> variables) {
        RestResponse<TaskCompleteDto> camundaCompleteResponse = camundaTaskComplete(taskId, variables);

        boolean isCompleted = false;
        if (camundaCompleteResponse.getStatus() == RestResponse.Status.OK.getStatusCode()
                || camundaCompleteResponse.getStatus() == RestResponse.Status.NO_CONTENT.getStatusCode()) {
            Log.error("NEXT - Task completed! taskId: " + taskId);
            isCompleted = true;
        }

        return isCompleted;
    }

    /**
     * <p>
     * <b>CAMUNDA COMMUNICATION</b>
     * </p>
     * 
     * Starts a new BPMN process instance in Camunda.
     *
     * @param transactionId The transaction ID associated with the process.
     * @param variables     The variables to associate with the process.
     * @return A `RestResponse` containing information about the started process
     *         instance.
     */
    public RestResponse<StartProcessInstanceDto> camundaStartProcess(String transactionId,
            Map<String, Object> variables) {
        CamundaBodyRequestDto body = CamundaBodyRequestDto.builder()
                .businessKey(transactionId)
                .variables(Utility.generateBodyRequestVariables(variables))
                .build();

        // TODO retrieve processDefinitionKey from model
        return camundaProxy.startInstance("pagamentoAvvisiPagoPA", body);
    }

    /**
     * <p>
     * <b>CAMUNDA COMMUNICATION</b>
     * </p>
     * 
     * Retrieves a list of Camunda tasks associated with a given business key.
     *
     * @param businessKey The business key of the process instance.
     * @return A `RestResponse` containing a list of Camunda tasks.
     */
    public RestResponse<List<TaskDto>> getCamundaTaskList(String businessKey) {
        CamundaBodyRequestDto body = CamundaBodyRequestDto.builder().processInstanceBusinessKey(businessKey).build();

        return camundaProxy.getList(body);
    }

    /**
     * <p>
     * <b>CAMUNDA COMMUNICATION</b>
     * </p>
     * 
     * Completes a Camunda task.
     *
     * @param taskId    The ID of the task to complete.
     * @param variables The variables to associate with the completion.
     * @return A `RestResponse` indicating the completion status.
     */
    public RestResponse<TaskCompleteDto> camundaTaskComplete(String taskId, Map<String, Object> variables) {
        // TODO add withVariablesInReturn TRUE in body request
        CamundaBodyRequestDto body = CamundaBodyRequestDto.builder()
                .variables(Utility.generateBodyRequestVariables(variables))
                .build();

        return camundaProxy.complete(taskId, body);
    }

}
