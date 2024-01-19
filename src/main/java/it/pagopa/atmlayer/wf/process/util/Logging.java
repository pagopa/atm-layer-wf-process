package it.pagopa.atmlayer.wf.process.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Logging utility which contains standard applicative logs.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Logging {
    
    public static final String UNKNOWN_STATUS = "Unknown response status code: {}";

    public static final String PROCESS_RESOURCE_CLASS_ID = "ProcessResource.";
    public static final String PROCESS_DEPLOY_LOG_ID = PROCESS_RESOURCE_CLASS_ID + "deploy";
    public static final String PROCESS_RESOURCE_LOG_ID = PROCESS_RESOURCE_CLASS_ID + "resource";
    public static final String PROCESS_START_PROCESS_LOG_ID = PROCESS_RESOURCE_CLASS_ID + "startProcess";
    public static final String PROCESS_NEXT_LOG_ID = PROCESS_RESOURCE_CLASS_ID + "next";
    public static final String PROCESS_VARIABLES_LOG_ID = PROCESS_RESOURCE_CLASS_ID + "variables";

    public static final String MODEL_REST_CLIENT_CLASS_ID = "ModelRestClient.";
    public static final String MODEL_FIND_BPMN_BY_TRIAD = MODEL_REST_CLIENT_CLASS_ID + "findBPMNByTriad";

    public static final String CAMUNDA_REST_CLIENT_CLASS_ID = "CamundaRestClient.";
    public static final String CAMUNDA_DEPLOY_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "deploy";
    public static final String CAMUNDA_GET_RESOURCES_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "getResources";
    public static final String CAMUNDA_GET_RESOURCE_BINARY_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "getResourceBinary";
    public static final String CAMUNDA_START_INSTANCE_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "startInstance";
    public static final String CAMUNDA_GET_LIST_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "getList";
    public static final String CAMUNDA_COMPLETE_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "complete";
    public static final String CAMUNDA_GET_TASK_VARIABLES_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "getTaskVariables";
    public static final String CAMUNDA_GET_INSTANCE_ACTIVITY_LOG_ID = CAMUNDA_REST_CLIENT_CLASS_ID + "getInstanceActivity";


    /**
     * Logs the elapsed time occurred for the processing.
     * 
     * @param label - LOG_ID of the function to display in the log [Pattern -> CLASS.FUNCTION]
     * @param start - the start time, when the execution is started
     * @param stop  - the stop time, when the execution is finished
     */
    public static void logElapsedTime(String label, long start) {
        log.info(" - {} - Elapsed time [ms] = {}", label, System.currentTimeMillis() - start);
    }
    
}
