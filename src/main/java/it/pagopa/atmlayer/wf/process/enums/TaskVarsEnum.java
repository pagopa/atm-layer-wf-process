package it.pagopa.atmlayer.wf.process.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;

/**
 * Enum which represents the info of device peripherals.
 */
@ApplicationScoped
@AllArgsConstructor
public enum TaskVarsEnum {

    ON_ERROR("onError"),
    OUTCOME_VAR_NAME("outcomeVarName"),
    ON_TIMEOUT("onTimeout"),
    COMMAND("command"),
    RECEIPT_TEMPLATE("receiptTemplate"),
    TIMEOUT("timeout"),
    DATA("data"),
    TEMPLATE("template"),
    EPP_MODE("eppMode");

    private final String value;

    public String getValue() {
        return value;
    }

    public static List<String> getValues() {
        return Arrays.stream(TaskVarsEnum.values())
                .map(TaskVarsEnum::getValue)
                .collect(Collectors.toList());
    }
}
