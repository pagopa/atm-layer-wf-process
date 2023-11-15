package it.pagopa.atmlayer.wf.process.test;

import static io.restassured.RestAssured.given;

import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LogFilterTest {
    
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
