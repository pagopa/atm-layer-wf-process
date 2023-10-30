package it.pagopa.atmlayer.wf.process.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.pagopa.atmlayer.wf.process.bean.TaskRequest;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
           log.error("Error during Json processing log!");
        }
        return result;
    }

    /**
     * Delete file if exists in the specified filePath.
     * 
     * @param filePath
     * @throws IOException
     */
    public static void deleteFileIfExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        if (Files.exists(path)) {
            Files.delete(path);
            log.debug("File deleted successfully!");
        } else {
            log.error("File does not exist.");
        }
    }

    /**
     * Downloads a BPMN file from the specified URL and returns it as a temporary
     * file.
     *
     * @param url The URL from which to download the BPMN file.
     * @return A temporary File object representing the downloaded BPMN file.
     * @throws IOException If an I/O error occurs during the download or file
     *                     creation.
     */
    public static File downloadBpmnFile(URL url) throws IOException {
        File file = new File(Constants.DOWNLOADED_BPMN);

        try (InputStream in = url.openStream();
                OutputStream out = new FileOutputStream(Constants.DOWNLOADED_BPMN)) {
            in.transferTo(out);
        }

        return file;
    }
}
