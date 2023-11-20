package it.pagopa.atmlayer.wf.process.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public enum TaskVarsEnum {

    ON_ERROR("onError"),
    OUTCOME_VAR_NAME("outcome_var_name"),
    ON_TIMEOUT("onTimeout"),
    COMMAND("command"),
    RECEIPT_TEMPLATE("receipt_template"),
    TIMEOUT("timeout"),
    DATA("data");

    private final String value;

    TaskVarsEnum(String value) {
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
