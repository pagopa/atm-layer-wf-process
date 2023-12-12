package it.pagopa.atmlayer.wf.process.enums;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Enum which contains the device info passed to Camunda.
 */
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
