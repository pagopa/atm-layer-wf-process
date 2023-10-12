package it.pagopa.atmlayer.wf.process;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(
		info = @Info(title = "Workflow Process API", description = "Run the workflow based on BPMN model", version = "1.0.0")
)
@ApplicationPath("/")
public class ProcessMicroservice extends Application {
}
