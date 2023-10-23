package it.pagopa.atmlayer.wf.process.bean;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

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
@Schema(description = "Il body della richiesta di elaborazione del task")
public class TaskRequest {

	@Schema(required = true, description = "Identificativo del Task")
    private String taskId;

	@Schema(required = true, description = "ID della transazione. Può essere generato dal Device alla richiesta della prima scena oppure generato dal server alla risposta della prima scena. Resta invariato fino al termine della funzione.", example = "b197bbd0-0459-4d0f-9d4a-45cdd369c018")
    private String transactionId;

	@Schema(required = true, description = "ID della funzione")
    private String functionId;

	@Schema(description = "La mappa delle variabili in input al task da eseguire")
    private Map<String, Object> variables;
    
    private DeviceInfo deviceInfo;

}