package it.pagopa.atmlayer.wf.process.client.bean;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class CamundaBodyRequestDto {

    @JsonProperty("businessKey")
    private String businessKey;

    @JsonProperty("processInstanceBusinessKey")
    private String processInstanceBusinessKey;

    @JsonProperty("taskId")
    private String taskId;
    
    @JsonProperty("variables")
    private  Map<String, Map<String, Object>> variables;

    @JsonProperty("withVariablesInReturn")
    private boolean withVariablesInReturn;

}
