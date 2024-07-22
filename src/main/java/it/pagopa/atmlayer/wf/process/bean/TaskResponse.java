package it.pagopa.atmlayer.wf.process.bean;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
@Schema(description = "La risposta all'elaborazione del task")
public class TaskResponse {

    @Schema(description = "lista dei task recuperati", maxItems = 10000, implementation = Task.class)
    private List<Task> tasks;

    @Schema(description = "business key associata al processo camunda", maxLength = 36, format = "string")
    private String transactionId;

}