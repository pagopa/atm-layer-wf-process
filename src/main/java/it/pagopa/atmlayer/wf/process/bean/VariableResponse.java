package it.pagopa.atmlayer.wf.process.bean;

import java.util.Map;

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
public class VariableResponse {
    
    Map<String, Object> buttons;

    Map<String, Object> variables;
}
