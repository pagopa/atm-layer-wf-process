package it.pagopa.atmlayer.wf.process.test.util;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;

import org.apache.commons.lang3.RandomStringUtils;

import it.pagopa.atmlayer.wf.process.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.process.bean.DeviceType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessTestData {
    
    public static final String TRANSACTION_ID = "TEST_TRANSACTION_ID";

    public static final String FUNCTION_ID = "TEST_FUNCTION_ID";
    
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
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put(RandomStringUtils.random(10, true, true), RandomStringUtils.random(10, true, true));

        variables.put(RandomStringUtils.random(10, true, true), RandomStringUtils.random(10, true, true));
        variables.put(RandomStringUtils.random(10, true, true), Math.random());
        variables.put(RandomStringUtils.random(10, true, true), nestedMap);
        return variables;
    }
}
