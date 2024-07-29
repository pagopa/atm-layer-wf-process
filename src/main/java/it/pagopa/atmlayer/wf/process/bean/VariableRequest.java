package it.pagopa.atmlayer.wf.process.bean;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

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
@Schema(description = "Richiesta di variabili")
@JsonInclude(Include.NON_NULL)
public class VariableRequest {
    
    @NotNull(message = "taskId non può essere null")
	@Schema(required = true, description = "Identificativo del Task", maxLength = 36, format = "string")
    @Size(max = 36)
    private String taskId;

    @Schema(description = "Lista di button richiesti da recuperare tra la variabili per il task corrente", type = SchemaType.ARRAY, maxItems = 10000)
    private List<String> buttons;

    @Schema(description = "Lista di variabili da filtrare per il task corrente", type = SchemaType.ARRAY, implementation = String.class, maxItems = 10000)
    private List<String> variables;
}
