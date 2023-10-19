package it.pagopa.atmlayer.wf.process.bean;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    private String id;

    private Map<String, Object> variables;

    private String form;

    private int priority;

}
