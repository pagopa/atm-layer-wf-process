package it.pagopa.atmlayer.wf.process.bean;

import java.util.List;
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
public class TaskResponse {

    @JsonProperty(value = "tasks")
    private List<String> tasks;

    @JsonProperty(value = "transactionId")
    private String transactionId;

    @JsonProperty(value = "variables")
    private Map<String,Object> variables;

}