package it.pagopa.atmlayer.wf.process.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;

/**
 * Enum which represents the info of buttons.
 */
@ApplicationScoped
@AllArgsConstructor
public enum TaskButtonsEnum {

    EPP_ENTER("eppEnter"),
    EPP_CANCEL("eppCancel");

    private final String value;

    public String getValue() {
        return value;
    }

    public static List<String> getValues() {
        return Arrays.stream(TaskButtonsEnum.values())
                .map(TaskButtonsEnum::getValue)
                .collect(Collectors.toList());
    }
}
