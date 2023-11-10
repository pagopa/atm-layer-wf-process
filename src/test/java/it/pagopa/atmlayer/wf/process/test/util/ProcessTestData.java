package it.pagopa.atmlayer.wf.process.test.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;

import it.pagopa.atmlayer.wf.process.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.process.bean.DeviceType;
import it.pagopa.atmlayer.wf.process.bean.TaskRequest;
import it.pagopa.atmlayer.wf.process.bean.VariableRequest;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaBodyRequestDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaStartProcessInstanceDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaTaskDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaVariablesDto;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.InstanceDto;
import it.pagopa.atmlayer.wf.process.client.model.bean.ModelBpmnDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessTestData {

    public static final String TRANSACTION_ID = "TEST_TRANSACTION_ID";

    public static final String FUNCTION_ID = "TEST_FUNCTION_ID";

    public static final String TASK_ID = "TEST_TASK_ID";

    public static final String BPMN_ID = "TEST_BPMN_ID";

    private static Random random = new Random();

    public static DeviceInfo getDeviceInfo() {
        return DeviceInfo.builder().bankId(RandomStringUtils.random(5, false, true))
                .branchId(RandomStringUtils.random(5, false, true))
                .channel(DeviceType.ATM)
                .code(RandomStringUtils.random(5, false, true))
                .terminalId(RandomStringUtils.random(10, true, true))
                .opTimestamp(Date.from(Instant.now())).build();
    }

    public static Map<String, Object> getVariables() {
        Map<String, Object> variables = new HashMap<>();

        variables.put(RandomStringUtils.random(10, true, true), RandomStringUtils.random(10, true, true));
        variables.put(RandomStringUtils.random(10, true, true), Math.random());
        return variables;
    }

    public static CamundaBodyRequestDto createRandomCamundaBodyRequestDto() {
        Map<String, Map<String, Object>> variables = new HashMap<>();
        variables.put("PRINTER", createRandomVariable());
        variables.put("SCANNER", createRandomVariable());

        return CamundaBodyRequestDto.builder()
                .businessKey(TRANSACTION_ID)
                .processInstanceBusinessKey(FUNCTION_ID)
                .variables(variables)
                .build();
    }

    private static Map<String, Object> createRandomVariable() {
        Map<String, Object> variable = new HashMap<>();
        variable.put("value", random.nextBoolean() ? "OK" : "KO");
        return variable;
    }

    public static CamundaStartProcessInstanceDto createCamundaStartProcessInstanceDto() {
        return CamundaStartProcessInstanceDto.builder().businessKey(ProcessTestData.TRANSACTION_ID).build();
    }

    public static CamundaVariablesDto createCamundaVariablesDto() {
        Map<String, Map<String, Object>> variables = new HashMap<>();

        // Creazione di una mappa casuale nidificata
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("chiave1", random.nextInt(100));
        innerMap.put("chiave2", random.nextDouble());
        innerMap.put("chiave3", random.nextBoolean());
        innerMap.put("chiave4", RandomStringUtils.random(5));

        // Inserimento della mappa nidificata nella mappa principale
        variables.put("variabile1", innerMap);

        return new CamundaVariablesDto(variables);
    }

    public static TaskRequest createTaskRequestStart() {
        return TaskRequest.builder()
                .transactionId(ProcessTestData.TRANSACTION_ID)
                .functionId(ProcessTestData.FUNCTION_ID)
                .variables(ProcessTestData.getVariables())
                .deviceInfo(ProcessTestData.getDeviceInfo())
                .build();
    }

    public static TaskRequest createTaskRequestNext() {
        return TaskRequest.builder()
                .transactionId(ProcessTestData.TRANSACTION_ID)
                .taskId(ProcessTestData.TASK_ID)
                .variables(ProcessTestData.getVariables())
                .deviceInfo(ProcessTestData.getDeviceInfo())
                .build();
    }

    public static VariableRequest createVariableRequest() {
        List<String> buttons = IntStream.range(0, random.nextInt(6))
                .mapToObj(i -> "Button_" + i)
                .collect(Collectors.toList());

        List<String> variables = IntStream.range(0, random.nextInt(6))
                .mapToObj(i -> "Variable_" + i)
                .collect(Collectors.toList());

        return VariableRequest.builder()
                .taskId(TRANSACTION_ID)
                .buttons(buttons)
                .variables(variables)
                .build();
    }

    public static VariableRequest createVariableRequestWithoutVars() {
        VariableRequest variableRequest = createVariableRequest();
        variableRequest.setVariables(null);

        return variableRequest;
    }

    public static VariableRequest createVariableRequestWithoutButtons() {
        VariableRequest variableRequest = createVariableRequest();
        variableRequest.setButtons(null);

        return variableRequest;
    }

    public static TaskRequest createTaskRequestNextMissingTaskId() {
        return TaskRequest.builder()
                .transactionId(ProcessTestData.TRANSACTION_ID)
                .variables(ProcessTestData.getVariables())
                .deviceInfo(ProcessTestData.getDeviceInfo())
                .build();
    }

    public static List<CamundaTaskDto> createListCamundaTaskDto() {
        List<CamundaTaskDto> tasks = new ArrayList<>();
        tasks.add(CamundaTaskDto.builder().id(TASK_ID + "1").build());
        tasks.add(CamundaTaskDto.builder().id(TASK_ID + "2").build());
        return tasks;
    }

    public static ModelBpmnDto createModelBpmnDto() {
        return ModelBpmnDto.builder().camundaDefinitionId(BPMN_ID).build();
    }

    public static List<InstanceDto> createResponseInstance() {
        return new ArrayList<InstanceDto>();
    }
}
