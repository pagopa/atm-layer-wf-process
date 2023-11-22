package it.pagopa.atmlayer.wf.process.service;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;

import it.pagopa.atmlayer.wf.process.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.process.bean.Task;
import it.pagopa.atmlayer.wf.process.bean.TaskResponse;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse;
import it.pagopa.atmlayer.wf.process.client.camunda.CamundaRestClient;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaBodyRequestDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaStartProcessInstanceDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaTaskDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaVariablesDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.InstanceDto;
import it.pagopa.atmlayer.wf.process.client.model.ModelRestClient;
import it.pagopa.atmlayer.wf.process.client.model.bean.ModelBpmnDto;
import it.pagopa.atmlayer.wf.process.enums.ProcessErrorEnum;
import it.pagopa.atmlayer.wf.process.exception.ProcessException;
import it.pagopa.atmlayer.wf.process.util.Properties;
import it.pagopa.atmlayer.wf.process.util.Utility;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
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

    @RestClient
    ModelRestClient modelRestClient;

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

        try {
            camundaDeployResponse = camundaRestClient.deploy(Utility.downloadBpmnFile(new URL(requestUrl), fileName));
            log.info("BPMN deployed!");
        } catch (WebApplicationException e) {
            log.error("Deploy bpmn failed! The service may be unreachable service or an error occured:", e);
            throw new ProcessException(ProcessErrorEnum.GENERIC);
        }

        return camundaDeployResponse;
    }

    /**
     * Start a new Bpmn process instance.
     * 
     * @param transactionId
     * @param functionId
     * @param deviceInfo
     * @param variables
     */
    public void start(String transactionId, String functionId, DeviceInfo deviceInfo, Map<String, Object> variables) {
        Utility.populateDeviceInfoVariables(transactionId, deviceInfo, variables);

        String bpmnId = findBpmnId(functionId, deviceInfo);

        startInstance(transactionId, bpmnId, variables);
    }

    /**
     * <p>
     * <b>MODEL COMMUNICATION</b>
     * </p>
     * 
     * Find the Bpmn Id for the specified device with the given funtion id.
     * 
     * @param functionId
     * @param deviceInfo
     * @return bpmnId
     */
    private String findBpmnId(String functionId, DeviceInfo deviceInfo) {
        String bpmnId = functionId;
        RestResponse<ModelBpmnDto> modelFindBpmnIdResponse = null;

        deviceInfo = Utility.constructModelDeviceInfo(deviceInfo);
        try {
            modelFindBpmnIdResponse = modelRestClient.findBPMNByTriad(functionId, deviceInfo.getBankId(),
                    deviceInfo.getBranchId(), deviceInfo.getTerminalId());
        } catch (WebApplicationException e) {
            switch (e.getResponse().getStatus()) {
                case RestResponse.StatusCode.BAD_REQUEST -> {
                    log.error("Find Bpmn id failed! No runnable BPMN found for selection.");
                }
                case RestResponse.StatusCode.INTERNAL_SERVER_ERROR -> {
                    log.error("Find Bpmn id failed! A model generic error occured.");
                }
                default -> {
                    log.error("Unknown response status code: {}", e.getResponse().getStatus());
                }
            }
        } catch (ProcessingException e) {
            log.warn("Connection refused with model service");
        }
        /*
         * Model call is ok
         */
        if (modelFindBpmnIdResponse != null) {
            bpmnId = modelFindBpmnIdResponse.getEntity().getCamundaDefinitionId();
            log.info("BpmnId retrieved by model: [{}]", bpmnId);
        } else {
            log.info("Using functionId as bpmnId: [{}]", bpmnId);
        }

        return bpmnId;
    }

    /**
     * Start a new Bpmn process instance for the specified terminal with the
     * transaction id as business key and the given variables.
     * 
     * @param transactionId
     * @param bpmnId
     * @param variables
     */
    private void startInstance(String transactionId, String bpmnId, Map<String, Object> variables) {
        try {
            camundaStartProcess(transactionId, bpmnId, variables);
            log.info("Process started! Business key: {}", transactionId);
        } catch (WebApplicationException e) {
            switch (e.getResponse().getStatus()) {
                case RestResponse.StatusCode.BAD_REQUEST -> {
                    log.error("Start process instance failed! Invalid variable.");
                    throw new ProcessException(ProcessErrorEnum.START_C01);
                }
                case RestResponse.StatusCode.INTERNAL_SERVER_ERROR -> {
                    log.error("Start process instance failed! The instance could not be created successfully.");
                    throw new ProcessException(ProcessErrorEnum.START_C02);
                }
                default -> {
                    log.error("Unknown response status code: {}", e.getResponse().getStatus());
                    throw new ProcessException(ProcessErrorEnum.GENERIC);
                }
            }
        }
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

        if (businessKey != null) {
            response = getActiveTasks(businessKey);
        } else {
            throw new ProcessException(ProcessErrorEnum.BUSINESS_KEY_NOT_PRESENT);
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
    public RestResponse<TaskResponse> getActiveTasks(String businessKey) {
        RestResponse<List<CamundaTaskDto>> camundaTaskList = getList(businessKey);

        log.info("Retrieving active tasks. . .");
        List<Task> activeTasks = camundaTaskList.getEntity().stream()
                .map(taskDto -> Task.builder()
                        .form(taskDto.getFormKey())
                        .id(taskDto.getId())
                        .priority(taskDto.getPriority())
                        .build())
                .collect(Collectors.toList());

        return RestResponse.status(Status.fromStatusCode(camundaTaskList.getStatus()),
                TaskResponse.builder().transactionId(businessKey).tasks(activeTasks).build());
    }

    /**
     * <p>
     * Retrieve the list of task associated to the business key.
     * </p>
     * 
     * @param businessKey
     * @return the list of camunda task associated to the businessKey
     */
    private RestResponse<List<CamundaTaskDto>> getList(String businessKey) {
        RestResponse<List<CamundaTaskDto>> camundaGetListResponse;

        try {
            camundaGetListResponse = camundaGetTaskList(businessKey);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == RestResponse.StatusCode.INTERNAL_SERVER_ERROR) {
                log.error("Get list of tasks failed!");
                throw new ProcessException(ProcessErrorEnum.GET_LIST_C03);
            } else {
                log.error("Unknown response status code: {}", e.getResponse().getStatus());
                throw new ProcessException(ProcessErrorEnum.GENERIC);
            }
        }

        if (camundaGetListResponse.getEntity().isEmpty()) {
            RestResponse<List<InstanceDto>> instanceResponse = camundaRestClient
                    .getInstanceActivity(businessKey);
            if (!instanceResponse.getEntity().isEmpty()) {
                log.debug("Instance still running...");
                /*
                 * It is possibile that after start of a process or the complete of a specified
                 * task there is a service task in execution which takes
                 * long time to finish its work. We iterate till a predefined number of attempts
                 * and after a specified time.
                 */
                camundaGetListResponse = retryActiveTasks(camundaGetListResponse, businessKey);
                if (camundaGetListResponse.getEntity().isEmpty()) {
                    camundaGetListResponse = RestResponse.status(Status.ACCEPTED, Collections.emptyList());
                }
            }
        }

        return camundaGetListResponse;
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
                camundaGetListResponse = camundaGetTaskList(businessKey);
            } catch (InterruptedException e) {
                log.error("Error during getActiveTask:", e);
                Thread.currentThread().interrupt();
                throw new ProcessException(ProcessErrorEnum.GENERIC);
            } catch (WebApplicationException e) {
                if (e.getResponse().getStatus() == RestResponse.StatusCode.INTERNAL_SERVER_ERROR) {
                    log.error("Get list of tasks failed!");
                    throw new ProcessException(ProcessErrorEnum.GET_LIST_C03);
                } else {
                    log.error("Unknown response status code: {}", e.getResponse().getStatus());
                    throw new ProcessException(ProcessErrorEnum.GENERIC);
                }
            }
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
    public void complete(String taskId, Map<String, Object> variables) {
        try {
            camundaTaskComplete(taskId, variables);
            log.info("Task completed! taskId: {}", taskId);
        } catch (WebApplicationException e) {
            switch (e.getResponse().getStatus()) {
                case RestResponse.StatusCode.BAD_REQUEST -> {
                    log.warn("Complete task failed! Invalid variable.");
                }
                case RestResponse.StatusCode.INTERNAL_SERVER_ERROR -> {
                    log.warn("Complete task failed! Task not exists or not corresponding to the specified instance.");
                }
                default -> {
                    log.warn("Unknown response status code: {}", e.getResponse().getStatus());
                }
            }
        } catch (ProcessingException e) {
            log.warn("Connection refused on Camunda service...");
        }
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
        RestResponse<CamundaVariablesDto> taskVariables;

        try {
            taskVariables = camundaGetTaskVariables(taskId);
            log.info("Retrieve variables completed!");
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == RestResponse.StatusCode.INTERNAL_SERVER_ERROR) {
                log.error("Retrieve variables failed! Task id is null or does ont exist.");
                throw new ProcessException(ProcessErrorEnum.VARIABLES_C06);
            } else {
                log.error("Unknown response status code: {}", e.getResponse().getStatus());
                throw new ProcessException(ProcessErrorEnum.GENERIC);
            }
        }

        return Utility.buildVariableResponse(taskVariables.getEntity(), variables, buttons);
    }

    /**
     * <p>
     * <b>CAMUNDA COMMUNICATION</b>
     * </p>
     * 
     * Starts a new BPMN process instance in Camunda.
     *
     * @param transactionId The transaction ID associated with the process.
     * @param functionId    The ID of the bpmn.
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

}
