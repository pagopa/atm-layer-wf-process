package it.pagopa.atmlayer.wf.process.enums;

import lombok.Getter;

import org.jboss.resteasy.reactive.RestResponse;

/**
 * Enum which maps the Process error state.
 */
@Getter
public enum ProcessErrorEnum {

    GENERIC(RestResponse.Status.INTERNAL_SERVER_ERROR, "G01", "An unexpected error has occurred, see logs for more info.", "GENERIC_ERROR"),
    TASK_ID_NOT_PRESENT(RestResponse.Status.BAD_REQUEST, "G02", "Task id is required, not an optional value.", "MISSING_TASK_ID"),
    BUSINESS_KEY_NOT_PRESENT(RestResponse.Status.BAD_REQUEST, "G03", "Business key is required, not an optional value.", "MISSING_BUSINESS_KEY"),
    RESOURCE_R01(RestResponse.Status.NOT_FOUND, "R01", "Get resources failed! No deployment resources found for the given id deployment.", "RESOURCES_NOT_FOUND"),
    RESOURCE_R02(RestResponse.Status.BAD_REQUEST, "R02", "Deployment Resource with given resource id or deployment id does not exist.", "RESOURCE_BINARY_NOT_FOUND"),
    START_C01(RestResponse.Status.BAD_REQUEST, "C01", "The instance could not be created due to an invalid variable value.", "START_INVALID_VARIABLE"),
    START_C02(RestResponse.Status.SERVICE_UNAVAILABLE, "C02", "The instance could not be created successfully.", "START_INSTANCE_FAILURE"),
    GET_LIST_C03(RestResponse.Status.SERVICE_UNAVAILABLE, "C03", "Some query parameter are invalid.", "GET_LIST_INVALID_QUERY_PARAM"),
    COMPLETE_C04(RestResponse.Status.BAD_REQUEST,"C04", "The variable value or type is invalid.", "COMPLETE_INVALID_VARIABLE"),
    COMPLETE_C05(RestResponse.Status.SERVICE_UNAVAILABLE, "C05", "The task does not exists or the corresponding instance could not be resumed successfully.", "COMPLETE_TASK_NOT_EXIST"),
    VARIABLES_C06(RestResponse.Status.SERVICE_UNAVAILABLE, "C06", "Task id is null or does not exists", "VARIABLES_TASK_NOT_EXIST"),
    BPMN_ID_NOT_FOUND_M01(RestResponse.Status.BAD_REQUEST, "M01", "No runnable BPMN found for selection.", "NOT_VALID_REFERENCED_ENTITY"),
    MODEL_GENERIC_ERROR_M02(RestResponse.Status.SERVICE_UNAVAILABLE, "M02", "An unexpected error has occurred, see model logs.", "GENERIC_ERROR");
    
    
    private final RestResponse.Status status;
    private final String errorCode;
    private final String errorMessage;
    private final String type;

    ProcessErrorEnum(RestResponse.Status status, String errorCode, String errorMessage, String type) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.type = type;
    }

}
