package it.pagopa.atmlayer.wf.process.util;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import it.pagopa.atmlayer.wf.process.bean.TaskRequest;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@ApplicationScoped
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utility {

    public static Map<String, Map<String, Object>> generateBodyRequestVariables(Map<String, Object> variables) {
        Map<String, Map<String, Object>> vars = new HashMap<>();

        if (variables != null && !variables.isEmpty()) {
            variables.forEach((key, value) -> {
                Map<String, Object> innerMap = new HashMap<>();
                innerMap.put("value", value);
                vars.put(key, innerMap);
            });
        }

        return vars;
    }

    public static boolean isTaskIdPresent(TaskRequest request) {
        return request.getTaskId() != null;
    }

    public static String getJson(Object object) {
        String result = null;
        ObjectMapper om = new ObjectMapper();

        try {
            result = om.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
           Log.error("Error during Json processing log!");
        }
        return result;
    }
}
