package it.pagopa.atmlayer.wf.process.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The {@code Constants} class provides a set of constant values used across the workflow process microservice.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String UNDERSCORE = "_";

    public static final String DOT = ".";

    public static final Object BPMN = "bpmn";

    public static final String DEFINITION_KEY = "definitionKey";

    public static final String DEFINITION_VERSION_CAMUNDA = "definitionVersionCamunda";

    public static final String FUNCTION_ID = "functionId";
}
