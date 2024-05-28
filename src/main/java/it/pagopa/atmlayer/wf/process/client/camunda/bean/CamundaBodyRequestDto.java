package it.pagopa.atmlayer.wf.process.client.camunda.bean;

import java.util.Map;

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
@JsonInclude(Include.NON_NULL)
public class CamundaBodyRequestDto {

    private String businessKey;

    private String processInstanceBusinessKey;

    private String taskId;
    
    private String processInstanceId;
    
    private  Map<String, Map<String, Object>> variables;

}
