package it.pagopa.atmlayer.wf.process;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(
		info = @Info(title = "Workflow Process API", description = "Interagisce con Camunda per l'esecuzione del flusso BPMN.", version = "1.0.0")
)
@ApplicationPath("/")
public class ProcessMicroservice extends Application {
}
