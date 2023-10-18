package it.pagopa.atmlayer.wf.process.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.rest.dto.runtime.StartProcessInstanceDto;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import io.quarkus.logging.Log;
import it.pagopa.atmlayer.wf.process.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.process.bean.Task;
import it.pagopa.atmlayer.wf.process.bean.TaskResponse;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse.VariableResponseBuilder;
import it.pagopa.atmlayer.wf.process.client.CamundaRestClient;
import it.pagopa.atmlayer.wf.process.client.bean.CamundaBodyRequestDto;
import it.pagopa.atmlayer.wf.process.client.bean.CamundaVariablesDto;
import it.pagopa.atmlayer.wf.process.client.bean.CamundaTaskDto;
import it.pagopa.atmlayer.wf.process.util.Constants;
import it.pagopa.atmlayer.wf.process.util.DeviceInfoEnum;
import it.pagopa.atmlayer.wf.process.util.TaskVarsEnum;
import it.pagopa.atmlayer.wf.process.util.Utility;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Pasquale Sansonna
 * 
 *         <p>
 *         This class provides services for managing operations related to BPM
 *         processes
 *         through Camunda.
 *         </p>
 */
@ApplicationScoped
public class ProcessService {

    @RestClient
    CamundaRestClient camundaRestClient;

    /**
     * Deploys a BPMN process definition in Camunda.
     *
     * @param bpmnFilePath The path to the BPMN file to deploy.
     * @return A `RestResponse` containing the deployment outcome.
     */
    public RestResponse<Object> deploy() {
        RestResponse<Object> response;

        try {
            final File bpmn = new File("/variabili.bpmn");
            // Camunda communication
            response = camundaRestClient.deploy(bpmn);

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
     * @return The business key (transactionId) of the started process or an empty string in case of
     *         an error.
     */
    public String start(String transactionId, String functionId, DeviceInfo deviceInfo, Map<String, Object> variables) {

        populateDeviceInfoVariables(transactionId, deviceInfo, variables);

        RestResponse<StartProcessInstanceDto> camundaStartInstanceResponse = camundaStartProcess(transactionId, functionId,
                variables);

        if (camundaStartInstanceResponse.getStatus() != RestResponse.Status.OK.getStatusCode()) {
            transactionId = Constants.EMPTY;
            Log.error("START - Start process instance failed!");
        } else {
            Log.info("START - Process started! Business key: " + transactionId);
        }

        return transactionId;
    }

    
    private void populateDeviceInfoVariables(String transactionId, DeviceInfo deviceInfo, Map<String, Object> variables) {
        variables.put(DeviceInfoEnum.TRANSACTION_ID.getValue(), transactionId);
        variables.put(DeviceInfoEnum.BANK_ID.getValue(), deviceInfo.getBankId());
        variables.put(DeviceInfoEnum.BRANCH_ID.getValue(), deviceInfo.getBranchId());
        variables.put(DeviceInfoEnum.TERMINAL_ID.getValue(), deviceInfo.getTerminalId());
        variables.put(DeviceInfoEnum.CODE.getValue(), deviceInfo.getCode());
        variables.put(DeviceInfoEnum.OP_TIMESTAMP.getValue(), deviceInfo.getOpTimestamp());
        variables.put(DeviceInfoEnum.DEVICE_TYPE.getValue(), deviceInfo.getDeviceType());
    }

    /**
     * Retrieves the active tasks associated with a BPM process.
     *
     * @param businessKey The business key of the process.
     * @return A `RestResponse` containing the retrieved tasks.
     */
    public RestResponse<TaskResponse> getActiveTasks(String businessKey) {
        RestResponse<TaskResponse> taskResponse;
        RestResponse<List<CamundaTaskDto>> camundaGetListResponse = camundaGetTaskList(businessKey);

        if (camundaGetListResponse.getStatus() == RestResponse.Status.OK.getStatusCode()) {
            Log.info("NEXT - Retrieving active tasks. . .");
            List<Task> activeTasks = camundaGetListResponse.getEntity()
                    .stream()
                    .map(taskDto -> Task.builder()
                            .form(taskDto.getFormKey())
                            .id(taskDto.getId())
                            .priority(taskDto.getPriority())
                            .build())
                    .collect(Collectors.toList());

            taskResponse = RestResponse
                    .ok(TaskResponse.builder().transactionId(businessKey).tasks(activeTasks).build());
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
        RestResponse<Object> camundaCompleteResponse = camundaTaskComplete(taskId, variables);

        boolean isCompleted = false;
        if (camundaCompleteResponse.getStatus() == RestResponse.Status.OK.getStatusCode()
                || camundaCompleteResponse.getStatus() == RestResponse.Status.NO_CONTENT.getStatusCode()) {
            Log.info("NEXT - Task completed! taskId: " + taskId);
            isCompleted = true;
        }

        return isCompleted;
    }

    public RestResponse<VariableResponse> getTaskVariables(String taskId, List<String> variables,
            List<String> buttons) {
        // Retrieve task variables from camunda
        
        CamundaVariablesDto taskVariables = camundaGetTaskVariables(taskId).getEntity();
        CamundaVariablesDto variablesFilteredList;
        CamundaVariablesDto buttonsFilteredList;
        VariableResponseBuilder variableResponseBuilder = VariableResponse.builder();

        //Filter variables
        if (variables != null && !variables.isEmpty()) {
            variables.addAll(TaskVarsEnum.getValues());
        } else {
            variables = TaskVarsEnum.getValues();
        }
        variablesFilteredList = filterCamundaVariables(taskVariables, variables);
        variableResponseBuilder.variables(mapVariablesResponse(variablesFilteredList));

        //Filter buttons
        if (buttons != null && !buttons.isEmpty()){
            buttonsFilteredList = filterCamundaVariables(taskVariables, buttons);
            variableResponseBuilder.buttons(mapVariablesResponse(buttonsFilteredList));
        }
        
        return RestResponse.ok(variableResponseBuilder.build());
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
    public RestResponse<StartProcessInstanceDto> camundaStartProcess(String transactionId, String functionId,
            Map<String, Object> variables) {
        CamundaBodyRequestDto body = CamundaBodyRequestDto.builder()
                .businessKey(transactionId)
                .variables(Utility.generateBodyRequestVariables(variables))
                .build();

        // TODO retrieve processDefinitionKey from model
        return camundaRestClient.startInstance(functionId, body);
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
    public RestResponse<List<CamundaTaskDto>> camundaGetTaskList(String businessKey) {
        CamundaBodyRequestDto body = CamundaBodyRequestDto.builder().processInstanceBusinessKey(businessKey).build();

        return camundaRestClient.getList(body);
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
    public RestResponse<Object> camundaTaskComplete(String taskId, Map<String, Object> variables) {
        CamundaBodyRequestDto body = CamundaBodyRequestDto.builder()
                .variables(Utility.generateBodyRequestVariables(variables))
                .build();

        return camundaRestClient.complete(taskId, body);
    }

    public RestResponse<CamundaVariablesDto> camundaGetTaskVariables(String taskId) {
        return camundaRestClient.getTaskVariables(taskId);
    }

    private CamundaVariablesDto filterCamundaVariables(CamundaVariablesDto camundaVariablesDto, List<String> vars) {
        Map<String, Map<String, Object>> variablesDto = camundaVariablesDto.getVariables();

        // Utilizza uno stream per filtrare i campi in base ai nomi forniti
        Map<String, Map<String, Object>> filteredFields = variablesDto.entrySet().stream()
                .filter(entry -> vars.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return CamundaVariablesDto.builder().variables(filteredFields).build();
    }

    private Map<String, Object> mapVariablesResponse(CamundaVariablesDto camundaVariablesDto) {
        Map<String, Map<String, Object>> variables = camundaVariablesDto.getVariables();

        return variables.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get("value")));
    }

}
