package it.pagopa.atmlayer.wf.process.client.camunda.bean;

import java.util.List;
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
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
public class CamundaStartProcessInstanceDto {

    private Map<String, Object> variables;

    private String businessKey;

    private String caseInstanceId;

    private List<Object> startInstructions;

    private boolean skipCustomListeners;

    private boolean skipIoMappings;

}
