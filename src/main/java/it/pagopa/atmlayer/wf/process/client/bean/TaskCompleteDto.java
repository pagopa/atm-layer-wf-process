package it.pagopa.atmlayer.wf.process.client.bean;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskCompleteDto{

    @JsonProperty("variables")
    private  Map<String, Map<String, Object>> variables;

}