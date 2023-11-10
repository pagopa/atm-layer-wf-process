package it.pagopa.atmlayer.wf.process.client.camunda.bean;

import java.util.List;

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
public class CaumndaInstanceActivityDto {

    List<InstanceDto> instanceList;
}
