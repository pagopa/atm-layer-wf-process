package it.pagopa.atmlayer.wf.process.exception.bean;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonPropertyOrder({"type", "errorCode", "status", "message"})
@RegisterForReflection
@Schema(description = "Messaggio di errore.")
public class ProcessErrorResponse {

    private String errorCode;

    private String type;

    private int statusCode;

    private String message;

}
