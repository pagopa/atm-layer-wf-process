package it.pagopa.atmlayer.wf.process.bean;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
@Schema(description = "Richiesta di variabili")
@JsonInclude(Include.NON_NULL)
public class VariableRequest {
    
    @NotNull(message = "taskId non può essere null")
	@Schema(required = true, description = "Identificativo del Task")
    private String taskId;

    @Schema(description = "Lista di button richiesti da recuperare tra la variabili per il task corrente")
    private List<String> buttons;

    @Schema(description = "Lista di variabili da filtrare per il task corrente")
    private List<String> variables;
}
