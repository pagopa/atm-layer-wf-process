package it.pagopa.atmlayer.wf.process.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.resteasy.reactive.RestResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.pagopa.atmlayer.wf.process.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse.VariableResponseBuilder;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaVariablesDto;
import it.pagopa.atmlayer.wf.process.enums.DeviceInfoEnum;
import it.pagopa.atmlayer.wf.process.enums.TaskButtonsEnum;
import it.pagopa.atmlayer.wf.process.enums.TaskVarsEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The {@code Utility} class provides utility methods for various operations within workflow process microservice.
 *
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utility {

    /**
     * Generates a map structure suitable for a REST request body based on the provided variables.
     * Each variable is mapped to a key in the outer map, and an inner map contains the variable's value.
     *
     * @param variables The input variables to be included in the map structure.
     * @return A map structure suitable for a REST request body.
     */
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

    /**
     * Converts an object to its JSON representation using Jackson ObjectMapper.
     *
     * @param object The object to be converted to JSON.
     * @return The JSON representation of the object.
     */
    public static String getJson(Object object) {
        String result = null;
        ObjectMapper om = new ObjectMapper();

        try {
            result = om.writeValueAsString(object);
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
     * Downloads file from the specified URL and returns it.
     *
     * @param url The URL from which to download the file.
     * @param fileName The name of the file to be created. 
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
     * Filters variables retrieved from camunda {@link CamundaVariablesDto} with the specified variable names.
     *
     * @param camundaVariablesDto The variables retrieved from Camunda.
     * @param vars The list of variable names to include in the filtered result.
     * @return A filtered {@link CamundaVariablesDto} containing only the specified variables.
     */
    public static CamundaVariablesDto filterCamundaVariables(CamundaVariablesDto camundaVariablesDto, List<String> vars) {
        Map<String, Map<String, Object>> variablesDto = camundaVariablesDto.getVariables();
        Map<String, Map<String, Object>> filteredFields = variablesDto.entrySet().stream()
                .filter(entry -> vars.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                
        return CamundaVariablesDto.builder().variables(filteredFields).build();
    }

    /**
     * Maps variables from a {@link CamundaVariablesDto} to a simpler map structure.
     *
     * @param camundaVariablesDto The variables retrieved from Camunda.
     * @return A map structure containing the variable names and their corresponding values.
     */
    public static Map<String, Object> mapVariablesResponse(CamundaVariablesDto camundaVariablesDto) {
        Map<String, Map<String, Object>> variables = camundaVariablesDto.getVariables();

        return variables.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Object value = entry.getValue().get("value");
                            if (value instanceof String && isBase64((String) value)) {
                                // if is Base64, deserialize in java object
                                return deserializeBase64(String.valueOf(value));
                            } else {
                                // else return the value as is
                                return value;
                            }
                        }));
    }

    private static boolean isBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static Object deserializeBase64(String base64String) {
        Object deserializedObject = null;
        
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(decodedBytes));
            deserializedObject = objectInputStream.readObject();           
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return deserializedObject;
    }
    /**
     * Populates device information variables in the provided map.
     *
     * @param transactionId The transaction ID.
     * @param deviceInfo     The device information.
     * @param variables      The map to be populated with device information variables.
     */
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

    /**
     * Builds a {@link VariableResponse} from Camunda variables, filtering by specified variable and button names.
     *
     * @param taskVariables The variables retrieved from Camunda.
     * @param variables     The list of variable names to include in the response.
     * @param buttons       The list of button names to include in the response.
     * @return A {@link RestResponse} containing the constructed {@link VariableResponse}.
     */
    public static RestResponse<VariableResponse> buildVariableResponse(CamundaVariablesDto taskVariables, List<String> variables, List<String> buttons) {
        VariableResponseBuilder variableResponseBuilder = VariableResponse.builder();
    
        // Filter variables
        if (variables != null) {
            variables.addAll(TaskVarsEnum.getValues());
        } else {
            variables = TaskVarsEnum.getValues();
        }
        
        CamundaVariablesDto variablesFilteredList = Utility.filterCamundaVariables(taskVariables, variables);
        variableResponseBuilder.variables(Utility.mapVariablesResponse(variablesFilteredList));
    
        // Filter buttons
        if (buttons != null) {
            buttons.addAll(TaskButtonsEnum.getValues());
        } else {
            buttons = TaskButtonsEnum.getValues();
        } 

        CamundaVariablesDto buttonsFilteredList = Utility.filterCamundaVariables(taskVariables, buttons);
        variableResponseBuilder.buttons(Utility.mapVariablesResponse(buttonsFilteredList));
    
        return RestResponse.ok(variableResponseBuilder.build());
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

    /**
     * Construct terminal ID by appending code to the bankId.
     * 
     * @param bankId
     * @param code
     * @return bankId+code
     */
    private static String constructTerminalId(String bankId, String code){
        return bankId.concat(code);
    }
}
