package it.pagopa.atmlayer.wf.process.test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaVariablesDto;
import it.pagopa.atmlayer.wf.process.util.Utility;

@QuarkusTest
class UtilityTest {
    
    @Test
    void testDeleteFileIfExists() throws IOException {

        String testFilePath = "test.txt";

        Files.createFile(Paths.get(testFilePath));

        Utility.deleteFileIfExists(testFilePath);

        // Assert that the file does not exist after deletion
        assert !Files.exists(Paths.get(testFilePath));
    }
    
    @Test
    void testDeserialize() throws JsonMappingException, JsonProcessingException {
        String camundaVariablesDto = "{\r\n"
                + "    \"template\": {\r\n"
                + "        \"type\": \"String\",\r\n"
                + "        \"value\": \"MENU\",\r\n"
                + "        \"valueInfo\": {}\r\n"
                + "    },\r\n"
                + "    \"branchId\": {\r\n"
                + "        \"type\": \"String\",\r\n"
                + "        \"value\": \"12345\",\r\n"
                + "        \"valueInfo\": {}\r\n"
                + "    },\r\n"
                + "    \"onError\": {\r\n"
                + "        \"type\": \"Object\",\r\n"
                + "        \"value\": \"rO0ABXNyABFqYXZhLnV0aWwuVHJlZU1hcAzB9j4tJWrmAwABTAAKY29tcGFyYXRvcnQAFkxqYXZhL3V0aWwvQ29tcGFyYXRvcjt4cHB3BAAAAAJ0AAllcnJvckNvZGV0AAIzMXQAEGVycm9yRGVzY3JpcHRpb250ABJlcnJvciBvbiBtZW51Lmh0bWx4\",\r\n"
                + "        \"valueInfo\": {\r\n"
                + "            \"objectTypeName\": \"java.util.TreeMap\",\r\n"
                + "            \"serializationDataFormat\": \"application/x-java-serialized-object\"\r\n"
                + "        }\r\n"
                + "    }   \r\n"
                + "}";
        ObjectMapper mapper = new ObjectMapper();
        CamundaVariablesDto cvd = mapper.readValue(camundaVariablesDto, CamundaVariablesDto.class);
        assertNotEquals(null, Utility.mapVariablesResponse(cvd)); 
     
    }

}
