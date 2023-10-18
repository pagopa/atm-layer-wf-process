package it.pagopa.atmlayer.wf.process.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum DeviceInfoEnum {
    
    TRANSACTION_ID("transactionId"),
    BANK_ID("bankId"),
    BRANCH_ID("branchId"),
    TERMINAL_ID("terminalId"),
    CODE("code"),
    OP_TIMESTAMP("opTimestamp"),
    DEVICE_TYPE("deviceType");

    private final String value;

    DeviceInfoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<String> getValues() {
        return Arrays.stream(TaskVarsEnum.values())
                .map(TaskVarsEnum::getValue)
                .collect(Collectors.toList());
    }

}
