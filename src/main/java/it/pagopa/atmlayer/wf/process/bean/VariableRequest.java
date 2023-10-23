package it.pagopa.atmlayer.wf.process.bean;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
@Schema(description = "Richiesta di variabili")
public class VariableRequest {
    
	@Schema(required = true, description = "Identificativo del Task")
    private String taskId;

    private List<String> buttons;

    @Schema(description = "Lista di variabili da filtrare per il task corrente")
    private List<String> variables;
}
