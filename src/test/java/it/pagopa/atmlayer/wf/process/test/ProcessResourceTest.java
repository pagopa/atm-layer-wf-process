package it.pagopa.atmlayer.wf.process.test;

import static io.restassured.RestAssured.given;

import java.io.File;
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
import it.pagopa.atmlayer.wf.process.client.camunda.CamundaRestClient;
import it.pagopa.atmlayer.wf.process.client.camunda.bean.CamundaBodyRequestDto;
import it.pagopa.atmlayer.wf.process.client.model.ModelRestClient;
import it.pagopa.atmlayer.wf.process.resource.ProcessResource;
import it.pagopa.atmlayer.wf.process.test.util.ProcessTestData;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@QuarkusTest
@TestHTTPEndpoint(ProcessResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProcessResourceTest {

        @InjectMock
        @MockitoConfig(convertScopes = true)
        @RestClient
        CamundaRestClient camundaRestClient;

        @InjectMock
        @MockitoConfig(convertScopes = true)
        @RestClient
        ModelRestClient modelRestClient;

        @Test
        public void testStartOk() {
                Mockito.when(camundaRestClient.startInstance(Mockito.anyString(),
                                Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createCamundaStartProcessInstanceDto()));
                Mockito.when(modelRestClient.findBPMNByTriad(Mockito.anyString(), Mockito.anyString(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.ok(ProcessTestData.createModelBpmnDto()));
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

                given()
                                .body(ProcessTestData.createTaskRequestStart())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/start")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testStartOkWithoutVars() {
                Mockito.when(camundaRestClient.startInstance(Mockito.anyString(),
                                Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createCamundaStartProcessInstanceDto()));
                Mockito.when(modelRestClient.findBPMNByTriad(Mockito.anyString(), Mockito.anyString(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.ok(ProcessTestData.createModelBpmnDto()));
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

                given()
                                .body(ProcessTestData.createTaskRequestStartWithoutVariables())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/start")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testStartFindBpmnBadRequest() {
                Mockito.when(camundaRestClient.startInstance(Mockito.anyString(),
                                Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createCamundaStartProcessInstanceDto()));
                Mockito.when(modelRestClient.findBPMNByTriad(Mockito.anyString(), Mockito.anyString(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenThrow(new WebApplicationException(Response.status(Status.BAD_REQUEST).build()));
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

                given()
                                .body(ProcessTestData.createTaskRequestStart())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/start")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testStartFindBpmnInternalServerError() {
                Mockito.when(camundaRestClient.startInstance(Mockito.anyString(),
                                Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createCamundaStartProcessInstanceDto()));
                Mockito.when(modelRestClient.findBPMNByTriad(Mockito.anyString(), Mockito.anyString(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenThrow(new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).build()));
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));
                given()
                                .body(ProcessTestData.createTaskRequestStart())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/start")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testStartFindBpmnUnknownStatusCode() {
                Mockito.when(camundaRestClient.startInstance(Mockito.anyString(),
                                Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createCamundaStartProcessInstanceDto()));
                Mockito.when(modelRestClient.findBPMNByTriad(Mockito.anyString(), Mockito.anyString(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenThrow(new WebApplicationException(Response.status(Status.BAD_GATEWAY).build()));
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

                given()
                                .body(ProcessTestData.createTaskRequestStart())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/start")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testStartFindBpmnProcessingException() {
                Mockito.when(camundaRestClient.startInstance(Mockito.anyString(),
                                Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createCamundaStartProcessInstanceDto()));
                Mockito.when(modelRestClient.findBPMNByTriad(Mockito.anyString(), Mockito.anyString(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenThrow(new ProcessingException("Test"));
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));
                                
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
                Mockito.when(camundaRestClient.startInstance(Mockito.anyString(),
                                Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.serverError());

                given()
                                .body(ProcessTestData.createTaskRequestStart())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/start")
                                .then()
                                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
        }

        @Test
        public void testStartKo() {
                Mockito.when(camundaRestClient.startInstance(Mockito.anyString(),
                                Mockito.any(CamundaBodyRequestDto.class)))
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
        public void testStartInstanceBadRequest() {
                Mockito.when(camundaRestClient.startInstance(Mockito.anyString(),
                                Mockito.any(CamundaBodyRequestDto.class)))
                                .thenThrow(new WebApplicationException(Response.status(Status.BAD_REQUEST).build()));

                given()
                                .body(ProcessTestData.createTaskRequestStart())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/start")
                                .then()
                                .statusCode(StatusCode.BAD_REQUEST);
        }

        @Test
        public void testStartInstanceInternalServerError() {
                Mockito.when(camundaRestClient.startInstance(Mockito.anyString(),
                                Mockito.any(CamundaBodyRequestDto.class)))
                                .thenThrow(new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).build()));

                given()
                                .body(ProcessTestData.createTaskRequestStart())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/start")
                                .then()
                                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
        }

        @Test
        public void testStartInstanceUnknownError() {
                Mockito.when(camundaRestClient.startInstance(Mockito.anyString(),
                                Mockito.any(CamundaBodyRequestDto.class)))
                                .thenThrow(new WebApplicationException(Response.status(Status.BAD_GATEWAY).build()));

                given()
                                .body(ProcessTestData.createTaskRequestStart())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/start")
                                .then()
                                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
        }

        @Test
        public void testStartOkDeviceInfoEmpty() {
                Mockito.when(camundaRestClient.startInstance(Mockito.anyString(),
                                Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createCamundaStartProcessInstanceDto()));
                Mockito.when(modelRestClient.findBPMNByTriad(Mockito.anyString(), Mockito.anyString(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.ok(ProcessTestData.createModelBpmnDto()));
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

                given()
                                .body(ProcessTestData.createTaskRequestStartDeviceInfoEmpty())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/start")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testStartOkDeviceInfoNull() {
                Mockito.when(camundaRestClient.startInstance(Mockito.anyString(),
                                Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createCamundaStartProcessInstanceDto()));
                Mockito.when(modelRestClient.findBPMNByTriad(Mockito.anyString(), Mockito.anyString(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.ok(ProcessTestData.createModelBpmnDto()));
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

                given()
                                .body(ProcessTestData.createTaskRequestStartDeviceInfoNull())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/start")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testNextOk() {
                Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok());
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

                given()
                                .body(ProcessTestData.createTaskRequestNext())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/next")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testNextOkWithoutVars() {
                Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok());
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

                given()
                                .body(ProcessTestData.createTaskRequestNextWithoutVars())
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
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

                given()
                                .body(ProcessTestData.createTaskRequestNext())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/next")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testNextOkCompleteBadRequest() {
                Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                                .thenThrow(new WebApplicationException(Response.status(Status.BAD_REQUEST).build()));
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

                given()
                                .body(ProcessTestData.createTaskRequestNext())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/next")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testNextOkCompleteInternalServerError() {
                Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                                .thenThrow(new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).build()));
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

                given()
                                .body(ProcessTestData.createTaskRequestNext())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/next")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testNextOkUnknownError() {
                Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                                .thenThrow(new WebApplicationException(Response.status(Status.BAD_GATEWAY).build()));
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

                given()
                                .body(ProcessTestData.createTaskRequestNext())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/next")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testNextOkProcessingError() {
                Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                                .thenThrow(new ProcessingException("Test"));
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createListCamundaTaskDto()));

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
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(Collections.emptyList()));
                Mockito.when(camundaRestClient.getInstanceActivity(Mockito.any(String.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createResponseInstance()));
                given()
                                .body(ProcessTestData.createTaskRequestNext())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/next")
                                .then()
                                .statusCode(StatusCode.ACCEPTED);
        }

       /*  @Test
        public void testNextOkWithNoTasksRetrievedWhileProcessing() {
                Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok());
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok(Collections.emptyList()));
                Mockito.when(camundaRestClient.getInstanceActivity(Mockito.any(String.class)))
                                .thenReturn(RestResponse.ok(ProcessTestData.createResponseInstanceProcessRetrieved()));
                given()
                                .body(ProcessTestData.createTaskRequestNext())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/next")
                                .then()
                                .statusCode();
        } */

        @Test
        public void testNextKoNoBusinessKey() {
                Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok());
                given()
                                .body(ProcessTestData.createTaskRequestNextNoBusinessKey())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/next")
                                .then()
                                .statusCode(StatusCode.BAD_REQUEST);
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
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.status(Status.BAD_REQUEST));

                given()
                                .body(ProcessTestData.createTaskRequestNext())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/next")
                                .then()
                                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
        }

        @Test
        public void testNextRetrieveActiveTasksInternalServerError() {
                Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok());
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenThrow(new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).build()));

                given()
                                .body(ProcessTestData.createTaskRequestNext())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/next")
                                .then()
                                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
        }

        @Test
        public void testNextRetrieveActiveTasksUnkonwnResponseCode() {
                Mockito.when(camundaRestClient.complete(Mockito.anyString(), Mockito.any(CamundaBodyRequestDto.class)))
                                .thenReturn(RestResponse.ok());
                Mockito.when(camundaRestClient.getList(Mockito.any(CamundaBodyRequestDto.class)))
                                .thenThrow(new WebApplicationException(Response.status(Status.BAD_GATEWAY).build()));

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
                                .post("/deploy/{functionType}", "BPMN")
                                .then()
                                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
        }


        @Test
        public void testVariablesBadRequest() {
                Mockito.when(camundaRestClient.getTaskVariables(Mockito.anyString()))
                                .thenThrow(new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).build()));

                given()
                                .body(ProcessTestData.createVariableRequest())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/variables")
                                .then()
                                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
        }

        @Test
        public void testVariablesUnknownResponseCode() {
                Mockito.when(camundaRestClient.getTaskVariables(Mockito.anyString()))
                                .thenThrow(new WebApplicationException(Response.status(Status.BAD_GATEWAY).build()));

                given()
                                .body(ProcessTestData.createVariableRequest())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/variables")
                                .then()
                                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);
        }

        @Test
        public void testVariablesOkNoVariables() {
                Mockito.when(camundaRestClient.getTaskVariables(Mockito.anyString()))
                                .thenReturn(RestResponse.ok(ProcessTestData.createCamundaVariablesDto()));

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
                Mockito.when(camundaRestClient.getTaskVariables(Mockito.anyString()))
                                .thenReturn(RestResponse.ok(ProcessTestData.createCamundaVariablesDto()));

                given()
                                .body(ProcessTestData.createVariableRequestWithoutButtons())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/variables")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testVariablesOkEmptyVariables() {
                Mockito.when(camundaRestClient.getTaskVariables(Mockito.anyString()))
                                .thenReturn(RestResponse.ok(ProcessTestData.createCamundaVariablesDto()));

                given()
                                .body(ProcessTestData.createVariableRequestWithVarsEmpty())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/variables")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testVariablesOkEmptyButtons() {
                Mockito.when(camundaRestClient.getTaskVariables(Mockito.anyString()))
                                .thenReturn(RestResponse.ok(ProcessTestData.createCamundaVariablesDto()));

                given()
                                .body(ProcessTestData.createVariableRequestWithButtonsEmpty())
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/variables")
                                .then()
                                .statusCode(StatusCode.OK);
        }

        @Test
        public void testVariablesTaskNotFound() {
                Mockito.when(camundaRestClient.getTaskVariables(Mockito.anyString()))
                                .thenReturn(RestResponse.serverError());

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

        @Test
        public void testLogFilter(){
                given()
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post("/")
                                .then()
                                .statusCode(StatusCode.NOT_FOUND);
        }

        @Test
        public void testLogFilterPost(){
                given()
                                .contentType(MediaType.APPLICATION_JSON)
                                .when()
                                .post()
                                .then()
                                .statusCode(StatusCode.NOT_FOUND);
        }
}
