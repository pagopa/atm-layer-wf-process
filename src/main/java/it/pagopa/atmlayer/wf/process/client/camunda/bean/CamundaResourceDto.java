package it.pagopa.atmlayer.wf.process.client.camunda.bean;

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
public class CamundaResourceDto {
    
    String id;

    String name;

    String deploymentId;
    
}
