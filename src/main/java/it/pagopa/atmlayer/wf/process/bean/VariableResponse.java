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
@Schema(description = "Risposta di variabili")
public class VariableResponse {
    
    Map<String, Object> buttons;

    Map<String, Object> variables;
}
