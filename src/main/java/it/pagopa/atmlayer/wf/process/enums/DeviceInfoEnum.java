package it.pagopa.atmlayer.wf.process.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public enum DeviceInfoEnum {
    
    TRANSACTION_ID("transactionId"),
    BANK_ID("bankId"),
    BRANCH_ID("branchId"),
    TERMINAL_ID("terminalId"),
    CODE("code"),
    OP_TIMESTAMP("opTimestamp"),
    DEVICE_TYPE("channel");

    private final String value;

    DeviceInfoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
