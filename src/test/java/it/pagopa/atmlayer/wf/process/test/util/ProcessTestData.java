package it.pagopa.atmlayer.wf.process.test.util;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

import it.pagopa.atmlayer.wf.process.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.process.bean.DeviceType;
import it.pagopa.atmlayer.wf.process.client.bean.CamundaBodyRequestDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessTestData {

    public static final String TRANSACTION_ID = "TEST_TRANSACTION_ID";

    public static final String FUNCTION_ID = "TEST_FUNCTION_ID";

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
}
