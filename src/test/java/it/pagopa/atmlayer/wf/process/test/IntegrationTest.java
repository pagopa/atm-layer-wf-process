package it.pagopa.atmlayer.wf.process.test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.Assert.assertTrue;

@QuarkusTest
@Testcontainers
@QuarkusTestResource(value = EnvironmentTestServicesResource.DockerCompose.class, restrictToAnnotatedClass = true)
@Slf4j
public class IntegrationTest {


    @Container
    public static GenericContainer<?> newman;


    @BeforeAll
    static void exposeTestPort() {
        newman = new GenericContainer<>(new ImageFromDockerfile()
                .withDockerfile(Paths.get("src/test/resources/integration-test/Dockerfile-postman")))
                .withFileSystemBind("src/test/resources/integration-test/output", "/output", BindMode.READ_WRITE)
                .withAccessToHost(true)
                .withStartupCheckStrategy(new OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(120)))
                .withExposedPorts(8086);
    }

    @Test
    void executePostmanCollectionWithNewmann() {

        newman.start();
        log.info(newman.getLogs());
        assertTrue(newman.getCurrentContainerInfo().getState().getExitCodeLong() == 0);
    }

}
