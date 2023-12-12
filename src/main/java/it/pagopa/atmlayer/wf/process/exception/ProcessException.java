package it.pagopa.atmlayer.wf.process.exception;

import it.pagopa.atmlayer.wf.process.enums.ProcessErrorEnum;
import it.pagopa.atmlayer.wf.process.exception.bean.ProcessErrorResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Getter;

/**
 * @author Pasquale Sansonna
 * 
 * <p>The {@code ProcessException} class represents an exception specific to the workflow process in the ATMLayer application.
 * It extends the {@link jakarta.ws.rs.WebApplicationException} and is used to handle errors related to the workflow process. </p>
 * 
 * <p>This exception is typically thrown when there is an issue in the workflow process, and it encapsulates information about
 * the error through the {@link it.pagopa.atmlayer.wf.process.enums.ProcessErrorEnum} enumeration. It generates a response
 * with details such as error type, status code, error message, and error code using the {@link ProcessErrorResponse} bean. </p>
 *
 * Example Usage:
 * <pre>
 * {@code
 * try {
 *     // Workflow process code
 * } catch (ProcessException e) {
 *     // Handle the specific workflow process exception
 *     logger.error("Workflow process error: {}", e.getMessage());
 *     // Additional handling based on the error details if needed
 * }
 * }
 * </pre>
 *
 * @see it.pagopa.atmlayer.wf.process.enums.ProcessErrorEnum
 * @see it.pagopa.atmlayer.wf.process.exception.bean.ProcessErrorResponse
 */
@Getter
public class ProcessException extends WebApplicationException {

    public ProcessException(ProcessErrorEnum processErrorEnum) {
        super(Response.status(processErrorEnum.getStatus().getStatusCode()).entity(ProcessErrorResponse.builder()
                .type(processErrorEnum.getType())
                .statusCode(processErrorEnum.getStatus().getStatusCode())
                .message(processErrorEnum.getErrorMessage())
                .errorCode(processErrorEnum.getErrorCode())
                .build()).build());
    }

}
