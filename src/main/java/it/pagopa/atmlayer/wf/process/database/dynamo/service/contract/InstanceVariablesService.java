package it.pagopa.atmlayer.wf.process.database.dynamo.service.contract;

import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

public abstract class InstanceVariablesService {

    public final static String INSTANCE_VARIABLES_NAME_COL = "name";
    public final static String INSTANCE_VARIABLES_VALUE_COL = "value";
    public final static String INSTANCE_VARIABLES_TABLE_NAME = "pagopa-dev-atm-layer-wf-process-instance-variables";

    protected ScanRequest scanRequest() {
        return ScanRequest.builder().tableName(INSTANCE_VARIABLES_TABLE_NAME)
                .attributesToGet(INSTANCE_VARIABLES_NAME_COL, INSTANCE_VARIABLES_VALUE_COL).build();
    }

}