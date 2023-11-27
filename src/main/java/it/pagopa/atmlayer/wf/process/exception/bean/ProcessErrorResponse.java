package it.pagopa.atmlayer.wf.process.exception.bean;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Pasquale Sansonna
 * 
 * <p>The {@code ProcessErrorResponse} class represents a structured error response for workflow process exceptions
 * in the ATMLayer application. It encapsulates information such as error type, error code, HTTP status code,
 * and an error message. </p>
 * 
 * <p>This class is designed to be used as part of the exception handling mechanism in conjunction with the
 * {@link it.pagopa.atmlayer.wf.process.exception.ProcessException} class. </p>
 * 
 * Example Usage:
 * <pre>
 * {@code
 * // Create a ProcessErrorResponse instance using the builder pattern
 * ProcessErrorResponse errorResponse = ProcessErrorResponse.builder()
 *         .type("Validation Error")
 *         .errorCode("VALIDATION_ERROR")
 *         .statusCode(400)
 *         .message("Invalid input provided for the workflow process.")
 *         .build();
 * }
 * </pre>
 *
 * @see it.pagopa.atmlayer.wf.process.exception.ProcessException
 */
@Getter
@Builder
@JsonPropertyOrder({"type", "errorCode", "status", "message"})
@RegisterForReflection
@Schema(description = "Messaggio di errore.")
public class ProcessErrorResponse {

    /**
     * The error code associated with the exception.
     */
    private String errorCode;

    /**
     * The type or category of the error.
     */
    private String type;

    /**
     * The HTTP status code representing the error.
     */
    private int statusCode;

    /**
     * A human-readable error message providing details about the exception.
     */
    private String message;

}
