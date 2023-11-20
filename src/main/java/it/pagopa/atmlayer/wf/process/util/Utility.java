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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.pagopa.atmlayer.wf.process.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse.VariableResponseBuilder;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaVariablesDto;
import it.pagopa.atmlayer.wf.process.enums.DeviceInfoEnum;
import it.pagopa.atmlayer.wf.process.enums.ProcessErrorEnum;
import it.pagopa.atmlayer.wf.process.enums.TaskVarsEnum;
import it.pagopa.atmlayer.wf.process.exception.ProcessException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    public static void deleteFileIfExists(String filePath) {
        Path path = Paths.get(filePath);

        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                log.error("Error during delete file: ", e);
            }
            log.debug("File deleted successfully!");
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
    public static File downloadBpmnFile(URL url, String fileName) throws IOException {
        File file = new File(fileName);

        try (InputStream in = url.openStream(); OutputStream out = new FileOutputStream(fileName)) {
            in.transferTo(out);
        }

        return file;
    }

    /**
     * Filters variables retrieved from camunda {@link CamundaVariablesDto} with the
     * 
     * @param camundaVariablesDto
     * @param vars
     * @return
     */
    public static CamundaVariablesDto filterCamundaVariables(CamundaVariablesDto camundaVariablesDto, List<String> vars) {
        Map<String, Map<String, Object>> variablesDto = camundaVariablesDto.getVariables();
        Map<String, Map<String, Object>> filteredFields = variablesDto.entrySet().stream()
                .filter(entry -> vars.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return CamundaVariablesDto.builder().variables(filteredFields).build();
    }

    public static Map<String, Object> mapVariablesResponse(CamundaVariablesDto camundaVariablesDto) {
        Map<String, Map<String, Object>> variables = camundaVariablesDto.getVariables();

        return variables.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get("value")));
    }

    public static void populateDeviceInfoVariables(String transactionId, DeviceInfo deviceInfo,
            Map<String, Object> variables) {
        
        if (variables == null){
            variables = new HashMap<>();
        }

        variables.put(DeviceInfoEnum.TRANSACTION_ID.getValue(), transactionId);
        variables.put(DeviceInfoEnum.BANK_ID.getValue(), deviceInfo.getBankId());
        variables.put(DeviceInfoEnum.BRANCH_ID.getValue(), deviceInfo.getBranchId());
        variables.put(DeviceInfoEnum.TERMINAL_ID.getValue(), deviceInfo.getTerminalId());
        variables.put(DeviceInfoEnum.CODE.getValue(), deviceInfo.getCode());
        variables.put(DeviceInfoEnum.OP_TIMESTAMP.getValue(), deviceInfo.getOpTimestamp());
        variables.put(DeviceInfoEnum.DEVICE_TYPE.getValue(), deviceInfo.getChannel());
    }

    public static VariableResponse buildVariableResponse(CamundaVariablesDto taskVariables, List<String> variables, List<String> buttons){
        CamundaVariablesDto variablesFilteredList;
        CamundaVariablesDto buttonsFilteredList;
        VariableResponseBuilder variableResponseBuilder = VariableResponse.builder();

        try {
            // Filter variables
            if (variables != null && !variables.isEmpty()) {
                variables.addAll(TaskVarsEnum.getValues());
            } else {
                variables = TaskVarsEnum.getValues();
            }
            variablesFilteredList = Utility.filterCamundaVariables(taskVariables, variables);
            variableResponseBuilder.variables(Utility.mapVariablesResponse(variablesFilteredList));

            // Filter buttons
            if (buttons != null && !buttons.isEmpty()) {
                buttonsFilteredList = Utility.filterCamundaVariables(taskVariables, buttons);
                variableResponseBuilder.buttons(Utility.mapVariablesResponse(buttonsFilteredList));
            }
        } catch (RuntimeException e) {
            log.error("Generic exception occured while building variable response:", e);
            throw new ProcessException(ProcessErrorEnum.GENERIC);
        }

        return variableResponseBuilder.build();
    }

    /**
     * <p>Checks if branch ID and the terminal ID are present or if are not empty and eventually 
     * populate them in this way:</p>
     * 
     * <p><b>Branch ID</b> : "_"</p>
     * <p><b>Terminal ID</b> : "bankId+code"</p>
     * 
     * @param deviceInfo
     * @return deviceInfo updated
     */
    public static DeviceInfo constructModelDeviceInfo(DeviceInfo deviceInfo){
        if (deviceInfo.getBranchId() == null || deviceInfo.getBranchId().isEmpty()){
            deviceInfo.setBranchId(Constants.UNDERSCORE);
        }

        if (deviceInfo.getTerminalId() == null || deviceInfo.getTerminalId().isEmpty()){
            deviceInfo.setTerminalId(constructTerminalId(deviceInfo.getBankId(), deviceInfo.getCode()));
        }

        return deviceInfo;
    }

    private static String constructTerminalId(String bankId, String code){
        return bankId.concat(code);
    }
}
