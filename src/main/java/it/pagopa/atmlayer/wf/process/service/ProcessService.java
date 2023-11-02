package it.pagopa.atmlayer.wf.process.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.process.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.process.bean.Task;
import it.pagopa.atmlayer.wf.process.bean.TaskResponse;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse.VariableResponseBuilder;
import it.pagopa.atmlayer.wf.process.client.CamundaRestClient;
import it.pagopa.atmlayer.wf.process.client.bean.CamundaBodyRequestDto;
import it.pagopa.atmlayer.wf.process.client.bean.CamundaStartProcessInstanceDto;
import it.pagopa.atmlayer.wf.process.client.bean.CamundaVariablesDto;
import it.pagopa.atmlayer.wf.process.enums.DeviceInfoEnum;
import it.pagopa.atmlayer.wf.process.enums.TaskVarsEnum;
import it.pagopa.atmlayer.wf.process.client.bean.CamundaTaskDto;
import it.pagopa.atmlayer.wf.process.util.Constants;
import it.pagopa.atmlayer.wf.process.util.Properties;
import it.pagopa.atmlayer.wf.process.util.Utility;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Pasquale Sansonna
 * 
 *         <p>
 *         This class provides services for managing operations related to BPM
 *         processes
 *         through Camunda.
 *         </p>
 */
@Slf4j
@ApplicationScoped
public class ProcessService {

    @RestClient
    CamundaRestClient camundaRestClient;

    @Inject
    Properties properties;

    /**
     * Deploys a BPMN process definition in Camunda.
     *
     * @param bpmnFilePath The path to the BPMN file to deploy.
     * @return A `RestResponse` containing the deployment outcome.
     * @throws IOException
     */
    public RestResponse<Object> deploy(String requestUrl, String fileName) throws IOException {
        RestResponse<Object> camundaDeployResponse;
        
        camundaDeployResponse = camundaRestClient.deploy(Utility.downloadBpmnFile(new URL(requestUrl), fileName));

        if (camundaDeployResponse.getStatus() == RestResponse.Status.OK.getStatusCode()) {
            log.info("DEPLOY - BPMN deployed!");
        } else {
            log.error("DEPLOY - Error during deployment!");
        }

        return camundaDeployResponse;
    }

    /**
     * Starts a new BPMN process in Camunda.
     *
     * @param transactionId The transaction ID associated with the process.
     * @param variables     The variables to associate with the process.
     * @return The business key (transactionId) of the started process or an empty
     *         string in case of
     *         an error.
     */
    public String start(String transactionId, String functionId, DeviceInfo deviceInfo, Map<String, Object> variables) {
        populateDeviceInfoVariables(transactionId, deviceInfo, variables);

        RestResponse<CamundaStartProcessInstanceDto> camundaStartInstanceResponse = camundaStartProcess(transactionId, functionId, variables);

        if (camundaStartInstanceResponse.getStatus() != RestResponse.Status.OK.getStatusCode()) {
            transactionId = null;
            log.error("Start process instance failed!");
        } else {
            log.info("Process started! Business key: {}", transactionId);
        }

        return transactionId;
    }

    /**
     * This method retrieves the active tasks for the specified camunda process
     * identified by the business key.
     * 
     * @param businessKey
     * @return RestResponse<TaskResponse>
     */
    public RestResponse<TaskResponse> retrieveActiveTasks(String businessKey) {
        RestResponse<TaskResponse> response;
        TaskResponse taskResponse;

        if (businessKey != null) {
            taskResponse = getActiveTasks(businessKey);
            response = taskResponse != null ? RestResponse.ok(taskResponse) : RestResponse.serverError();
        } else {
            response = RestResponse.status(RestResponse.Status.BAD_REQUEST);
        }

        return response;
    }

    /**
     * Retrieves the active tasks associated with a BPM process.
     * 
     * @param businessKey The business key of the process.
     * @return A `RestResponse` containing the retrieved tasks.
     * @throws InterruptedException
     */
    public TaskResponse getActiveTasks(String businessKey) {
        TaskResponse taskResponse = null;
        RestResponse<List<CamundaTaskDto>> camundaGetListResponse = camundaGetTaskList(businessKey);

        if (camundaGetListResponse.getStatus() == RestResponse.Status.OK.getStatusCode()) {

            /*
             * It is possibile that after start of a process or the complete of a specified
             * task there is a service task in execution which takes
             * long time to finish its work. We iterate till a predefined number of attempts
             * and after a specified time.
             */
            camundaGetListResponse = retryActiveTasks(camundaGetListResponse, businessKey);

            log.info("Retrieving active tasks. . .");
            List<Task> activeTasks = camundaGetListResponse.getEntity()
                    .stream()
                    .map(taskDto -> Task.builder()
                            .form(taskDto.getFormKey())
                            .id(taskDto.getId())
                            .priority(taskDto.getPriority())
                            .build())
                    .collect(Collectors.toList());

            taskResponse = TaskResponse.builder().transactionId(businessKey).tasks(activeTasks).build();
            log.info("Tasks retrieved!");
        } else {
            log.error("Get list of tasks failed!");
        }

        return taskResponse;
    }

    /**
     * This method iterate the call to retrieve active tasks in camunda in case
     * camundaGetListResponse contains an empty list of tasks.
     * The attempts are predefined in application properties, also the delay between
     * the client calls.
     * 
     * @param camundaGetListResponse
     * @param businessKey
     * @return RestResponse<List<CamundaTaskDto>>
     */
    private RestResponse<List<CamundaTaskDto>> retryActiveTasks(
            RestResponse<List<CamundaTaskDto>> camundaGetListResponse, String businessKey) {
        int attempts = 0;

        while (camundaGetListResponse.getEntity().isEmpty() && attempts < properties.getTaskListAttempts()) {
            ++attempts;

            try {
                Thread.sleep(properties.getTaskListTimeToAttempt());
            } catch (InterruptedException e) {
                log.error("Error during getActiveTask", e);
                Thread.currentThread().interrupt();
            }

            camundaGetListResponse = camundaGetTaskList(businessKey);
        }

        return camundaGetListResponse;
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
            log.info("Task completed! taskId: {}", taskId);
            isCompleted = true;
        }

        return isCompleted;
    }

    /**
     * Gets the task instance variables.
     * 
     * @param taskId    The id of the task from which I will retrieve the variables.
     * @param variables The additional variables to retrieve
     * @param buttons   The buttons to retrieve
     * @return
     */
    public VariableResponse getTaskVariables(String taskId, List<String> variables,
            List<String> buttons) {

        CamundaVariablesDto taskVariables = camundaGetTaskVariables(taskId).getEntity();
        CamundaVariablesDto variablesFilteredList;
        CamundaVariablesDto buttonsFilteredList;
        VariableResponseBuilder variableResponseBuilder = VariableResponse.builder();

        // Filter variables
        if (variables != null && !variables.isEmpty()) {
            variables.addAll(TaskVarsEnum.getValues());
        } else {
            variables = TaskVarsEnum.getValues();
        }
        variablesFilteredList = filterCamundaVariables(taskVariables, variables);
        variableResponseBuilder.variables(mapVariablesResponse(variablesFilteredList));

        // Filter buttons
        if (buttons != null && !buttons.isEmpty()) {
            buttonsFilteredList = filterCamundaVariables(taskVariables, buttons);
            variableResponseBuilder.buttons(mapVariablesResponse(buttonsFilteredList));
        }

        return variableResponseBuilder.build();
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
    public RestResponse<CamundaStartProcessInstanceDto> camundaStartProcess(String transactionId, String functionId,
            Map<String, Object> variables) {
        CamundaBodyRequestDto body = CamundaBodyRequestDto.builder()
                .businessKey(transactionId)
                .variables(Utility.generateBodyRequestVariables(variables))
                .build();

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
    private RestResponse<List<CamundaTaskDto>> camundaGetTaskList(String businessKey) {
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
    private RestResponse<Object> camundaTaskComplete(String taskId, Map<String, Object> variables) {
        CamundaBodyRequestDto body = CamundaBodyRequestDto.builder()
                .variables(Utility.generateBodyRequestVariables(variables))
                .build();

        return camundaRestClient.complete(taskId, body);
    }

    /**
     * <p>
     * <b>CAMUNDA COMMUNICATION</b>
     * </p>
     * 
     * Get variables associated to the process instance.
     *
     * @param taskId The ID of the task to complete.
     * @return A `RestResponse` containing variables.
     */
    private RestResponse<CamundaVariablesDto> camundaGetTaskVariables(String taskId) {
        return camundaRestClient.getTaskVariables(taskId);
    }

    /**
     * Filters variables retrieved from camunda {@link CamundaVariablesDto} with the
     * 
     * @param camundaVariablesDto
     * @param vars
     * @return
     */
    private CamundaVariablesDto filterCamundaVariables(CamundaVariablesDto camundaVariablesDto, List<String> vars) {
        Map<String, Map<String, Object>> variablesDto = camundaVariablesDto.getVariables();
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

    private void populateDeviceInfoVariables(String transactionId, DeviceInfo deviceInfo,
            Map<String, Object> variables) {
        variables.put(DeviceInfoEnum.TRANSACTION_ID.getValue(), transactionId);
        variables.put(DeviceInfoEnum.BANK_ID.getValue(), deviceInfo.getBankId());
        variables.put(DeviceInfoEnum.BRANCH_ID.getValue(), deviceInfo.getBranchId());
        variables.put(DeviceInfoEnum.TERMINAL_ID.getValue(), deviceInfo.getTerminalId());
        variables.put(DeviceInfoEnum.CODE.getValue(), deviceInfo.getCode());
        variables.put(DeviceInfoEnum.OP_TIMESTAMP.getValue(), deviceInfo.getOpTimestamp());
        variables.put(DeviceInfoEnum.DEVICE_TYPE.getValue(), deviceInfo.getChannel());
    }
}
