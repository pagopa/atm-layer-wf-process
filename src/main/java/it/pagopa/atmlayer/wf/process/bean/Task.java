package it.pagopa.atmlayer.wf.process.bean;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@Schema(description = "Info relative al task da elaborare")
public class Task {

    @Schema(description = "ID del task")
    private String id;

    @Schema(description = "variabili del task")
    private Map<String, Object> variables;

    @Schema(description = "formKey del task")
    private String form;

    @Schema(description = "priority associata al task")
    private int priority;

}
