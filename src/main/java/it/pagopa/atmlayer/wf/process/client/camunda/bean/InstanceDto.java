package it.pagopa.atmlayer.wf.process.client.camunda.bean;

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
public class InstanceDto {
    String id;

    String definitionId;

    String businessKey;

    String caseInstanceId;

    boolean suspended;

    String tenantId;
}
