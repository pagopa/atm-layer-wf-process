package it.pagopa.atmlayer.wf.process.exception;

import it.pagopa.atmlayer.wf.process.enums.ProcessErrorEnum;
import it.pagopa.atmlayer.wf.process.exception.bean.ProcessErrorResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Getter;

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
