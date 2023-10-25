package it.pagopa.atmlayer.wf.process.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import it.pagopa.atmlayer.wf.process.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.process.bean.DeviceType;
import it.pagopa.atmlayer.wf.process.bean.TaskRequest;
import it.pagopa.atmlayer.wf.process.bean.TaskResponse;
import it.pagopa.atmlayer.wf.process.resource.ProcessResource;
import it.pagopa.atmlayer.wf.process.service.ProcessService;
import it.pagopa.atmlayer.wf.process.test.util.ProcessTestData;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@TestHTTPEndpoint(ProcessResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProcessResourceTest {

    @InjectMock
    private ProcessService processService;

    @Test
    public void testStartProcess() {
        
        TaskRequest request = TaskRequest.builder().transactionId(ProcessTestData.TRANSACTION_ID).deviceInfo(ProcessTestData.getDeviceInfo()).build();
        TaskResponse taskResponse = TaskResponse.builder().tasks(Collections.emptyList()).build();
        Mockito.when(processService.start(ProcessTestData.TRANSACTION_ID, ProcessTestData.FUNCTION_ID, ProcessTestData.getDeviceInfo(), null)).thenReturn(ProcessTestData.TRANSACTION_ID);
        Mockito.when(processService.getActiveTasks(ProcessTestData.TRANSACTION_ID)).thenReturn(taskResponse);

        given()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/start")
                .then()
                .statusCode(StatusCode.OK);
    }
/* 
    @Test
    public void testStartProcessNoBusinessKey() {
        DeviceInfo deviceInfo = DeviceInfo.builder().bankId(RandomStringUtils.random(5, false, true))
                .branchId(RandomStringUtils.random(5, false, true))
                .channel(DeviceType.ATM)
                .code(RandomStringUtils.random(5, false, true))
                .terminalId(RandomStringUtils.random(10, true, true))
                .opTimestamp(Date.from(Instant.now())).build();
        String transactionId = RandomStringUtils.random(10, true, true);

        TaskRequest request = TaskRequest.builder().transactionId(transactionId).deviceInfo(deviceInfo).build();
        TaskResponse taskResponse = TaskResponse.builder().tasks(Collections.emptyList()).build();
        Mockito.when(processService.start(transactionId, RandomStringUtils.random(5, false, true), deviceInfo, null)).thenReturn(Constants.EMPTY);
        Mockito.when(processService.getActiveTasks(transactionId)).thenReturn(taskResponse);

        given()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/start")
                .then()
                .statusCode(StatusCode.BAD_REQUEST);
    }

    
      @Test
      public void testNext() {
      // Simulare il comportamento del servizio di processo quando viene chiamato
      il
      // metodo 'complete'.
      Mockito.when(processService.complete(Mockito.anyString(),
      Mockito.any())).thenReturn(true);
      TaskResponse taskResponse = new TaskResponse(Collections.emptyList());
      Mockito.when(processService.getActiveTasks(Mockito.anyString())).thenReturn(
      taskResponse);
      
      TaskRequest request = new TaskRequest();
      // Inizializzare 'request' con i dati di input desiderati
      
      given()
      .body(request)
      .contentType("application/json")
      .when()
      .post("/api/v1/processes/next")
      .then()
      .statusCode(200);
      }
      
      @Test
      public void testVariables() {
      // Simulare il comportamento del servizio di processo quando viene chiamato
      il
      // metodo 'getTaskVariables'.
      VariableResponse variableResponse = new
      VariableResponse(Collections.emptyMap());
      Mockito.when(processService.getTaskVariables(Mockito.anyString(),
      Mockito.any(), Mockito.any()))
      .thenReturn(variableResponse);
      
      VariableRequest request = new VariableRequest();
      // Inizializzare 'request' con i dati di input desiderati
      
      given()
      .body(request)
      .contentType("application/json")
      .when()
      .post("/api/v1/processes/variables")
      .then()
      .statusCode(200);
      } */
    

}
