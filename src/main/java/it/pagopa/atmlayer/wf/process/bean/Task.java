package it.pagopa.atmlayer.wf.process.bean;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.Schema.False;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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

    @Schema(description = "ID del task", example = "ec0723fb-e6a4-11ee-a2d3-def94d507fe5", format = "String", maxLength = 36)
    @Size(max = 36)
    private String id;

    @Schema(description = "variabili del task", format = "String", additionalProperties = False.class)
    private Map<String, Object> variables;

    @Schema(description = "formKey del task", format = "String", maxLength = 36)
    private String form;

    @Schema(description = "priority associata al task", format = "int", minimum = "0", maximum = "2147483647")
    @PositiveOrZero
    private int priority; 

}
