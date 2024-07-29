package it.pagopa.atmlayer.wf.process;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(info = @Info(title = "Workflow Process API", description = "Interagisce con Camunda per l'esecuzione del flusso BPMN.", version = "1.0.0", contact = @Contact(name = "Supporto API", url = "https://www.pagopa.gov.it/", email = "info@pagopa.it")), tags = @Tag(name = "Process", description = "Gestione nel task attraverso l'engine Camunda"))
@ApplicationPath("/")
public class ProcessMicroservice extends Application {
}
