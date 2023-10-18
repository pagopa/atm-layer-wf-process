package it.pagopa.atmlayer.wf.process.bean;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class VariableRequest {
    
    private String taskId;

    private List<String> buttons;

    private List<String> variables;
}
