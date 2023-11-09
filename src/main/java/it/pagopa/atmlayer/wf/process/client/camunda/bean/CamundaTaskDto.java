package it.pagopa.atmlayer.wf.process.client.camunda.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
public class CamundaTaskDto {

  private String id;

  private String name;

  private String assignee;

  private Date created;

  private Date due;

  private Date followUp;

  private Date lastUpdated;

  private String delegationState;

  private String description;

  private String executionId;

  private String owner;

  private String parentTaskId;

  private int priority;

  private String processDefinitionId;

  private String processInstanceId;

  private String taskDefinitionKey;

  private String caseExecutionId;

  private String caseInstanceId;

  private String caseDefinitionId;

  private boolean suspended;

  private String formKey;

  private CamundaFormRef camundaFormRef;
  
  private String tenantId;

}