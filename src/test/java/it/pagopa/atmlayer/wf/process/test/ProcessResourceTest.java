package it.pagopa.atmlayer.wf.process.test;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.MockitoConfig;
import it.pagopa.atmlayer.wf.process.client.CamundaRestClient;
import it.pagopa.atmlayer.wf.process.client.bean.CamundaBodyRequestDto;
import it.pagopa.atmlayer.wf.process.resource.ProcessResource;
import it.pagopa.atmlayer.wf.process.test.util.ProcessTestData;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@TestHTTPEndpoint(ProcessResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProcessResourceTest {

    @InjectMock
    @MockitoConfig(convertScopes = true)
    @RestClient
    CamundaRestClient camundaRestClient;

    @Test
    public void testStartOk() {
        Mockito.when(camundaRestClient.startInstance(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                .thenReturn(RestResponse.ok(ProcessTestData.createCamundaStartProcessInstanceDto()));
        Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class))).thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

        given()
                .body(ProcessTestData.createTaskRequestStart())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/start")
                .then()
                .statusCode(StatusCode.OK);
    }

    @Test
    public void testStartInstanceKo() {
        Mockito.when(camundaRestClient.startInstance(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                .thenReturn(RestResponse.serverError());

        given()
                .body(ProcessTestData.createTaskRequestStart())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/start")
                .then()
                .statusCode(StatusCode.BAD_REQUEST);
    }

    @Test
    public void testStartKo() {
        Mockito.when(camundaRestClient.startInstance(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                .thenThrow(new RuntimeException());
        
        given()
                .body(ProcessTestData.createTaskRequestStart())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/start")
                .then()
                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testNextOk() {
        Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                .thenReturn(RestResponse.ok());
        Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class))).thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

        given()
                .body(ProcessTestData.createTaskRequestNext())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/next")
                .then()
                .statusCode(StatusCode.OK);
    }

    @Test
    public void testNextOkNoContent() {
        Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                .thenReturn(RestResponse.noContent());
        Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class))).thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

        given()
                .body(ProcessTestData.createTaskRequestNext())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/next")
                .then()
                .statusCode(StatusCode.OK);
    }

    @Test
    public void testNextOkWithNoTasksRetrieved() {
        Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                .thenReturn(RestResponse.ok());
        Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class))).thenReturn(RestResponse.ok(Collections.emptyList()));

        given()
                .body(ProcessTestData.createTaskRequestNext())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/next")
                .then()
                .statusCode(StatusCode.OK);
    }

    @Test
    public void testNextMissingTaskId() {
        given()
                .body(ProcessTestData.createTaskRequestNextMissingTaskId())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/next")
                .then()
                .statusCode(StatusCode.BAD_REQUEST);
    }

    @Test
    public void testNextKo() {
        Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                .thenThrow(new RuntimeException());

        given()
                .body(ProcessTestData.createTaskRequestNext())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/next")
                .then()
                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testNextCompleteKo() {
        Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                .thenReturn(RestResponse.serverError());

        given()
                .body(ProcessTestData.createTaskRequestNext())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/next")
                .then()
                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testNextRetrieveActiveTasksKo() {
        Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                .thenReturn(RestResponse.ok());
        Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class))).thenReturn(RestResponse.status(Status.BAD_REQUEST));

        given()
                .body(ProcessTestData.createTaskRequestNext())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/next")
                .then()
                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDeployKo() {
        Mockito.when(camundaRestClient.deploy(Mockito.any(File.class))).thenReturn(RestResponse.ok());
        
        given()
                .formParam("url", "http://aaaaddddeeeffff.com")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .when()
                .post("/deploy")
                .then()
                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testVariablesOk() {
        Mockito.when(camundaRestClient.getTaskVariables(Mockito.anyString())).thenReturn(RestResponse.ok(ProcessTestData.createCamundaVariablesDto()));
        
        given()
                .body(ProcessTestData.createVariableRequest())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/variables")
                .then()
                .statusCode(StatusCode.OK);
    }

    @Test
    public void testVariablesOkNoVariables() {
        Mockito.when(camundaRestClient.getTaskVariables(Mockito.anyString())).thenReturn(RestResponse.ok(ProcessTestData.createCamundaVariablesDto()));
        
        given()
                .body(ProcessTestData.createVariableRequestWithoutVars())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/variables")
                .then()
                .statusCode(StatusCode.OK);
    }

    @Test
    public void testVariablesOkNoButtons() {
        Mockito.when(camundaRestClient.getTaskVariables(Mockito.anyString())).thenReturn(RestResponse.ok(ProcessTestData.createCamundaVariablesDto()));
        
        given()
                .body(ProcessTestData.createVariableRequestWithoutButtons())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/variables")
                .then()
                .statusCode(StatusCode.OK);
    }

    @Test
    public void testVariablesTaskNotFound() {
        Mockito.when(camundaRestClient.getTaskVariables(Mockito.anyString())).thenReturn(RestResponse.serverError());
        
        given()
                .body(ProcessTestData.createVariableRequest())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/variables")
                .then()
                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testVariablesKo() {
        Mockito.when(camundaRestClient.getTaskVariables(Mockito.anyString())).thenThrow(new RuntimeException());
        
        given()
                .body(ProcessTestData.createVariableRequest())
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/variables")
                .then()
                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
    }
}
