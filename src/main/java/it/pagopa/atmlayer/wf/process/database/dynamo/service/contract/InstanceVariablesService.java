package it.pagopa.atmlayer.wf.process.database.dynamo.service.contract;

import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

/**
 * Base service for instance variables management on Dynamo DB.
 */
public abstract class InstanceVariablesService {

    public static final String INSTANCE_VARIABLES_NAME_COL = "name";
    public static final String INSTANCE_VARIABLES_VALUE_COL = "value";
    public static final String INSTANCE_VARIABLES_TABLE_NAME = "pagopa-dev-atm-layer-wf-process-instance-variables";

    /**
     * Creates a ScanRequest for pagopa-dev-atm-layer-wf-process-instance-variables table on DynamoDB.
     * 
     * @return the scan request
     */
    protected ScanRequest scanRequest() {
        return ScanRequest.builder().tableName(INSTANCE_VARIABLES_TABLE_NAME)
                .attributesToGet(INSTANCE_VARIABLES_NAME_COL, INSTANCE_VARIABLES_VALUE_COL).build();
    }

}