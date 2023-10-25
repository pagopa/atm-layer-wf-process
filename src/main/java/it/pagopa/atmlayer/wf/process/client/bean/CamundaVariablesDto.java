package it.pagopa.atmlayer.wf.process.client.bean;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@RegisterForReflection
@NoArgsConstructor
@AllArgsConstructor
public class CamundaVariablesDto {
    
    @Builder.Default
    private Map<String, Map<String, Object>> variables = new HashMap<>();

    @JsonAnySetter
    public void setVariable(String variableName, Map<String, Object> variableData) {
        variables.put(variableName, variableData);
    }
    
    @JsonAnyGetter
    public Map<String, Map<String, Object>> getVariables() {
        return variables;
    }
}
