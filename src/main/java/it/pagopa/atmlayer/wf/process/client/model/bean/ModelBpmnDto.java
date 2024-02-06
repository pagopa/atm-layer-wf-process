package it.pagopa.atmlayer.wf.process.client.model.bean;

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
public class ModelBpmnDto {
    
    String camundaDefinitionId;

    String definitionKey;

    String definitionVersionCamunda;

}
