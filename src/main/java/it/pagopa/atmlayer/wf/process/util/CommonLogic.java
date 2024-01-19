package it.pagopa.atmlayer.wf.process.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Logging utility which contains standard applicative logs.
 */
@Slf4j
public class CommonLogic{
    
    protected static final String UNKNOWN_STATUS = "Unknown response status code: {}";

    private static final String PROCESS_RESOURCE_CLASS_ID = "ProcessResource.";
    protected static final String PROCESS_DEPLOY_LOG_ID = PROCESS_RESOURCE_CLASS_ID + "deploy";
    protected static final String PROCESS_RESOURCE_LOG_ID = PROCESS_RESOURCE_CLASS_ID + "resource";
    protected static final String PROCESS_START_PROCESS_LOG_ID = PROCESS_RESOURCE_CLASS_ID + "startProcess";
    protected static final String PROCESS_NEXT_LOG_ID = PROCESS_RESOURCE_CLASS_ID + "next";
    protected static final String PROCESS_VARIABLES_LOG_ID = PROCESS_RESOURCE_CLASS_ID + "variables";

    private static final String MODEL_REST_CLIENT_CLASS_ID = "ModelRestClient.";
    protected static final String MODEL_FIND_BPMN_BY_TRIAD = MODEL_REST_CLIENT_CLASS_ID + "findBPMNByTriad";

    private static final String CAMUNDA_REST_CLIENT_CLASS_ID = "CamundaRestClient.";
    protected static final String CAMUNDA_DEPLOY_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "deploy";
    protected static final String CAMUNDA_GET_RESOURCES_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "getResources";
    protected static final String CAMUNDA_GET_RESOURCE_BINARY_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "getResourceBinary";
    protected static final String CAMUNDA_START_INSTANCE_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "startInstance";
    protected static final String CAMUNDA_GET_LIST_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "getList";
    protected static final String CAMUNDA_COMPLETE_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "complete";
    protected static final String CAMUNDA_GET_TASK_VARIABLES_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "getTaskVariables";
    protected static final String CAMUNDA_GET_INSTANCE_ACTIVITY_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "getInstanceActivity";


    /**
     * Logs the elapsed time occurred for the processing.
     * 
     * @param label - LOG_ID of the function to display in the log
     * @param start - the start time, when the execution is started
     * @param stop  - the stop time, when the execution is finished
     */
    protected static void logElapsedTime(String label, long start) {
        log.info(" - {} - Elapsed time [ms] = {}", label, System.currentTimeMillis() - start);
    }
    
}
