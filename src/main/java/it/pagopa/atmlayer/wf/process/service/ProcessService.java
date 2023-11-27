package it.pagopa.atmlayer.wf.process.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.process.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.process.bean.TaskResponse;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse;

/**
 * @author Pasquale Sansonna
 * 
 * <p>The {@code ProcessService} class provides services for managing operations related to BPM processes
 * through Camunda. It encapsulates the communication with the Camunda workflow engine, including
 * deploying BPMN process definitions, starting process instances, completing tasks, and retrieving
 * information about active tasks and variables. </p>
 * 
 * @see it.pagopa.atmlayer.wf.process.client.camunda.CamundaRestClient
 * @see it.pagopa.atmlayer.wf.process.client.model.ModelRestClient
 * 
 */
public interface ProcessService {

    /**
     * Deploys a BPMN process definition in Camunda.
     *
     * @param bpmnFilePath The path to the BPMN file to deploy.
     * @return A `RestResponse` containing the deployment outcome.
     * @throws IOException
     */
    RestResponse<Object> deploy(String requestUrl, String fileName) throws IOException ;

    /**
     * 
     * Retrieves the BPMN binary resource for the associated id of the deployment.
     * 
     * @param id - the id of deployment
     * @return A `RestResponse` containing the resource file in xml format.
     */
    RestResponse<String> getResource(String deploymentId);

    /**
     * Starts a new process instance.
     * 
     * @param transactionId
     * @param functionId
     * @param deviceInfo
     * @param variables
     */
    void start(String transactionId, String functionId, DeviceInfo deviceInfo, Map<String, Object> variables);

    /**
     * This method retrieves the active tasks for the specified camunda process
     * identified by the business key.
     * 
     * @param businessKey
     * @return A `RestResponse` containing the active tasks.
     */
    RestResponse<TaskResponse> retrieveActiveTasks(String businessKey);

    
    /**
     * Completes a task in Camunda.
     *
     * @param taskId    The ID of the task to complete.
     * @param variables The variables to associate with the completion.
     */
    void complete(String taskId, Map<String, Object> variables);

    
    /**
     * Gets the task instance variables.
     * 
     * @param taskId    The id of the task from which I will retrieve the variables.
     * @param variables The additional variables to retrieve
     * @param buttons   The buttons to retrieve
     * @return A `RestResponse` containing the variables and buttons needed and the default variables and buttons.
     */
    RestResponse<VariableResponse> getTaskVariables(String taskId, List<String> variables, List<String> buttons);

}