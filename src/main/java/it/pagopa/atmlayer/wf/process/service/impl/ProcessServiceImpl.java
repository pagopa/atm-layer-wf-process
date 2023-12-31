package it.pagopa.atmlayer.wf.process.service.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;

import io.quarkus.logging.Log;
import it.pagopa.atmlayer.wf.process.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.process.bean.Task;
import it.pagopa.atmlayer.wf.process.bean.TaskResponse;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse;
import it.pagopa.atmlayer.wf.process.client.camunda.CamundaRestClient;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaBodyRequestDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaResourceDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaStartProcessInstanceDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaTaskDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaVariablesDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.InstanceDto;
import it.pagopa.atmlayer.wf.process.client.model.ModelRestClient;
import it.pagopa.atmlayer.wf.process.client.model.bean.ModelBpmnDto;
import it.pagopa.atmlayer.wf.process.enums.ProcessErrorEnum;
import it.pagopa.atmlayer.wf.process.exception.ProcessException;
import it.pagopa.atmlayer.wf.process.service.ProcessService;
import it.pagopa.atmlayer.wf.process.util.Logging;
import it.pagopa.atmlayer.wf.process.util.Properties;
import it.pagopa.atmlayer.wf.process.util.Utility;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

/**
 * ProcessService implementation
 */
@Slf4j
@ApplicationScoped
public class ProcessServiceImpl implements ProcessService {

    @RestClient
    CamundaRestClient camundaRestClient;

    @RestClient
    ModelRestClient modelRestClient;

    @Inject
    Properties properties;

    /**
     * {@inheritDoc}
     */
    public RestResponse<Object> deploy(String requestUrl, String fileName) throws IOException {
        log.info("CAMUNDA DEPLOY sending request with params: [ requestUrl: " + requestUrl + ", fileName: " + fileName + " ]");
        RestResponse<Object> camundaDeployResponse;

        try {
            camundaDeployResponse = camundaRestClient.deploy(Utility.downloadBpmnFile(new URL(requestUrl), fileName));
            log.info("Resource deployed!");
        } catch (WebApplicationException e) {
            log.error("Deploy bpmn failed! The service may be unreachable or an error occured:", e);
            throw new ProcessException(ProcessErrorEnum.DEPLOY_D01);
        }

        return camundaDeployResponse;
    }

    /**
     * {@inheritDoc}
     */
    public RestResponse<String> getResource(String deploymentId) {
        String resourceId = camundaGetResources(deploymentId);

        return RestResponse.ok(camundaGetResourceBinary(deploymentId, resourceId));
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
            log.info("MODEL FIND BPMN BY TRIAD REQUEST sending request with params: [ functionId: " + functionId + ", bankId: " + deviceInfo.getBankId()
                    + ", branchId: " + deviceInfo.getBranchId() + ", terminalId: " + deviceInfo.getTerminalId() + " ]");
            modelFindBpmnIdResponse = modelRestClient.findBPMNByTriad(functionId, deviceInfo.getBankId(),
                    deviceInfo.getBranchId(), deviceInfo.getTerminalId());
        } catch (WebApplicationException e) {
            switch (e.getResponse().getStatus()) {
                case RestResponse.StatusCode.BAD_REQUEST ->
                    log.warn("Find Bpmn id failed! No runnable BPMN found for selection.");

                case RestResponse.StatusCode.INTERNAL_SERVER_ERROR ->
                    log.warn("Find Bpmn id failed! A model generic error occured.");

                default -> log.warn(Logging.UNKNOWN_STATUS, e.getResponse().getStatus());
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
                    log.error(Logging.UNKNOWN_STATUS, e.getResponse().getStatus());
                    throw new ProcessException(ProcessErrorEnum.GENERIC);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
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
    private RestResponse<TaskResponse> getActiveTasks(String businessKey) {
        RestResponse<List<CamundaTaskDto>> camundaTaskList = getList(businessKey);

        log.info("Retrieving active tasks. . .");
        List<Task> activeTasks = camundaTaskList.getEntity().stream()
                .map(taskDto -> {
                    log.info("ID: {} ", taskDto.getId());

                    return Task.builder()
                            .form(taskDto.getFormKey())
                            .id(taskDto.getId())
                            .priority(taskDto.getPriority())
                            .build();
                })
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
            log.info("CAMUNDA GET LIST sending request with params: [ businessKey: " + businessKey + " ]");
            camundaGetListResponse = camundaGetTaskList(businessKey);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == RestResponse.StatusCode.INTERNAL_SERVER_ERROR) {
                log.error("Get list of tasks failed!");
                throw new ProcessException(ProcessErrorEnum.GET_LIST_C03);
            } else {
                log.error(Logging.UNKNOWN_STATUS, e.getResponse().getStatus());
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
                    log.info("Service task not completed yet!");
                    camundaGetListResponse = RestResponse.status(Status.ACCEPTED, Collections.emptyList());
                }
            } else {
                log.info("Process completed!");
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
                log.debug("Retry active task attempt {}", attempts);
            } catch (InterruptedException e) {
                log.error("Error during getActiveTask:", e);
                Thread.currentThread().interrupt();
                throw new ProcessException(ProcessErrorEnum.GENERIC);
            } catch (WebApplicationException e) {
                if (e.getResponse().getStatus() == RestResponse.StatusCode.INTERNAL_SERVER_ERROR) {
                    log.error("Get list of tasks failed!");
                    throw new ProcessException(ProcessErrorEnum.GET_LIST_C03);
                } else {
                    log.error(Logging.UNKNOWN_STATUS, e.getResponse().getStatus());
                    throw new ProcessException(ProcessErrorEnum.GENERIC);
                }
            }
        }

        return camundaGetListResponse;
    }

    /**
     * {@inheritDoc}
     */
    public void complete(String taskId, Map<String, Object> variables) {
        try {
            camundaTaskComplete(taskId, variables);
            log.info("Task completed! taskId: {}", taskId);
        } catch (WebApplicationException e) {
            switch (e.getResponse().getStatus()) {
                case RestResponse.StatusCode.BAD_REQUEST -> log.warn("Complete task failed! Invalid variable.");

                case RestResponse.StatusCode.INTERNAL_SERVER_ERROR ->
                    log.warn("Complete task failed! Task not exists or not corresponding to the specified instance.");

                default -> log.warn(Logging.UNKNOWN_STATUS, e.getResponse().getStatus());
            }
        } catch (ProcessingException e) {
            log.warn("Connection refused on Camunda service...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public RestResponse<VariableResponse> getTaskVariables(String taskId, List<String> variables,
            List<String> buttons) {
        RestResponse<CamundaVariablesDto> taskVariables;

        try {
            taskVariables = camundaGetTaskVariables(taskId);
            log.info("Variables: [{}]", taskVariables.getEntity());
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == RestResponse.StatusCode.INTERNAL_SERVER_ERROR) {
                log.error("Retrieve variables failed! Task id is null or does ont exist.");
                throw new ProcessException(ProcessErrorEnum.VARIABLES_C06);
            } else {
                log.error(Logging.UNKNOWN_STATUS, e.getResponse().getStatus());
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
        log.info("CAMUNDA START INSTANCE sending request with params: [functionId: {}, body: {}]", functionId, body);
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
        log.info("CAMUNDA COMPLETE sending request with params: [taskId: {}, body: {}]", taskId, body);
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
        log.info("CAMUNDA GET TASK VARIABLES sending request with params: [ taskId: {}", taskId, " ]");
        return camundaRestClient.getTaskVariables(taskId);
    }

    /**
     * <p>
     * <b>CAMUNDA COMMUNICATION</b>
     * </p>
     * 
     * Retrieves the BPMN for the associated id of the deployment on Camunda.
     * 
     * @param id
     * @return resourceId
     */
    private String camundaGetResources(String id) {
        RestResponse<List<CamundaResourceDto>> camundaGetResourcesResponse;

        try {
            log.info("CAMUNDA GET RESOURCES sending request with params: [ id: {} " + id + " ]");
            camundaGetResourcesResponse = camundaRestClient.getResources(id);
            log.info("Resources retrieved!");
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == RestResponse.StatusCode.NOT_FOUND) {
                log.error("Get resources failed! No deployment resources found for the given id deployment.");
                throw new ProcessException(ProcessErrorEnum.RESOURCE_R01);
            } else {
                log.error(Logging.UNKNOWN_STATUS, e.getResponse().getStatus());
                throw new ProcessException(ProcessErrorEnum.GENERIC);
            }
        }
        return camundaGetResourcesResponse.getEntity().stream().findFirst().get().getId();
    }

    /**
     * <p>
     * <b>CAMUNDA COMMUNICATION</b>
     * </p>
     * 
     * Retrieves the BPMN binary resource for the associated deploymentId and
     * resourceId on Camunda.
     * 
     * @param id
     * @return resourceId
     */
    private String camundaGetResourceBinary(String deploymentId, String resourceId) {
        RestResponse<String> camundaGetResourceBinaryResponse;

        try {
            log.info("CAMUNDA GET RESOURCE BINARY sending request with params: [ deploymentId: {}", deploymentId, ", resourceId: ", resourceId,
                    " ]");
            camundaGetResourceBinaryResponse = camundaRestClient.getResourceBinary(deploymentId, resourceId);
            log.info("Resource xml retrieved!");
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == RestResponse.StatusCode.BAD_REQUEST) {
                log.error("Get resources failed! No deployment resources found for the given id deployment.");
                throw new ProcessException(ProcessErrorEnum.RESOURCE_R02);
            } else {
                log.error(Logging.UNKNOWN_STATUS, e.getResponse().getStatus());
                throw new ProcessException(ProcessErrorEnum.GENERIC);
            }
        }

        return camundaGetResourceBinaryResponse.getEntity();
    }

}
