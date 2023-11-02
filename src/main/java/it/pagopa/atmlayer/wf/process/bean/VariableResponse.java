package it.pagopa.atmlayer.wf.process.bean;

import java.util.Map;

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
@Schema(description = "Risposta di variabili")
@JsonInclude(Include.NON_NULL)
public class VariableResponse {
    
    @Schema(description = "Buttons filtrati per il task richiesto")
    Map<String, Object> buttons;

    @Schema(description = "Variabili filtrate per il task richiesto")
    Map<String, Object> variables;
}
