package it.pagopa.atmlayer.wf.process.client.bean;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
