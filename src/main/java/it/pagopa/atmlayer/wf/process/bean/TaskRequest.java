package it.pagopa.atmlayer.wf.process.bean;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.Schema.False;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Schema(description = "Il body della richiesta di elaborazione del task")
public class TaskRequest {

	@Schema(required = true, description = "Identificativo del Task", maxLength = 36, format = "string")
    @Size(max = 36)
    private String taskId;

    @NotNull(message = "transactionId non può essere null")
	@Schema(required = true, description = "ID della transazione. Può essere generato dal Device alla richiesta della prima scena oppure generato dal server alla risposta della prima scena. Resta invariato fino al termine della funzione.", example = "b197bbd0-0459-4d0f-9d4a-45cdd369c018", maxLength = 36, format = "string")
    @Size(max = 36)
    private String transactionId;

	@Schema(required = true, description = "ID della funzione", maxLength = 300, format = "string")
    private String functionId;

	@Schema(description = "La mappa delle variabili in input al task da eseguire", additionalProperties = False.class)
    private Map<String, Object> variables;
    
    @NotNull(message = "deviceInfo non può essere null")
    private DeviceInfo deviceInfo;

}