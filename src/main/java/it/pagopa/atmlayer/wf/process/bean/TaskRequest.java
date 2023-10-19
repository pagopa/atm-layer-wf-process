package it.pagopa.atmlayer.wf.process.bean;

import java.util.Map;

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
public class TaskRequest {

    private String taskId;

    private String transactionId;

    private String functionId;

    private Map<String, Object> variables;
    
    private DeviceInfo deviceInfo;

}