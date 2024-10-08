package it.pagopa.atmlayer.wf.process.resource;

import it.pagopa.atmlayer.wf.process.bean.TaskRequest;
import it.pagopa.atmlayer.wf.process.bean.TaskResponse;
import it.pagopa.atmlayer.wf.process.bean.VariableRequest;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse;
import it.pagopa.atmlayer.wf.process.enums.ProcessErrorEnum;
import it.pagopa.atmlayer.wf.process.exception.ProcessException;
import it.pagopa.atmlayer.wf.process.exception.bean.ProcessErrorResponse;
import it.pagopa.atmlayer.wf.process.service.ProcessService;
import it.pagopa.atmlayer.wf.process.util.CommonLogic;
import it.pagopa.atmlayer.wf.process.util.Constants;
import it.pagopa.atmlayer.wf.process.util.Utility;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestResponse;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Pasquale Sansonna
 *
 * <p>The {@code ProcessResource} class defines REST endpoints for managing BPM processes through Camunda.
 * It provides operations for deploying BPMN process definitions, retrieving BPMN resources, starting process instances,
 * completing tasks, and handling variables within the workflow. </p>
 *
 * <p>The class is designed to handle BPM process-related operations and interacts with the Camunda workflow engine
 * through the injected {@link it.pagopa.atmlayer.wf.process.service.impl.ProcessServiceImpl} instance. </p>
 * @see it.pagopa.atmlayer.wf.process.service.impl.ProcessServiceImpl
 */
@Slf4j
@Path("/api/v1/processes")
@Tag(name = "Workflow", description = "Gestione del task affidata all'engine Camunda")
public class ProcessResource extends CommonLogic {

    @Inject
    ProcessService processService;

    /**
     * Endpoint to deploy a BPMN process definition to Camunda.
     *
     * @param requestUrl The task request.
     * @return A `RestResponse` containing the deployment outcome.
     */
    @Operation(operationId = "deploy", summary = "Esegue il 'deploy' di una risorsa.", description = "Esegue il deploy di una risorsa nel motore di workflow (es. Camunda)")
    @APIResponse(responseCode = "200", description = "OK. Operazione eseguita con successo. Restituisce l'ID della risorsa creata nel motore di workflow.", content = @Content(schema = @Schema(implementation = RestResponse.Status.class)))
    @APIResponse(responseCode = "400", description = "BAD_REQUEST. Nel caso di richiesta errata.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR. Nel caso di errore generico.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @APIResponse(responseCode = "503", description = "SERVICE_UNAVAILABLE. Errore durante il deploy della risorsa su Camunda.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @POST
    @Path("/deploy/{resourceType:.*}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public RestResponse<Object> deploy(
            @Parameter(description = "L'url da cui recuperare il file bpmn.") @RestForm("url") @Schema(format = "String", maxLength = 300) @Size(max = 300) String requestUrl,
            @Parameter(description = "Tipo di File (BPMN, DMN...)") @Schema(format = "String", maxLength = 4) @Size(max = 300) @PathParam("resourceType") String resourceType) {
        long start = System.currentTimeMillis();
        log.info("Executing DEPLOY. . .");

        RestResponse<Object> response;

        String fileName = new StringBuilder().append(UUID.randomUUID().toString()).append(Constants.DOT)
                .append(resourceType != null ? resourceType.toLowerCase() : Constants.BPMN).toString();

        try {
            response = processService.deploy(requestUrl, fileName);
        } catch (RuntimeException | IOException e) {
            log.error("Generic exception occured while deploying: ", e);
            throw new ProcessException(ProcessErrorEnum.GENERIC);
        } finally {
            // Delete of temp bpmn used for deploy on Camunda platform
            Utility.deleteFileIfExists(fileName);
            logElapsedTime(PROCESS_DEPLOY_LOG_ID, start);
        }

        return response;
    }

    /**
     * Endpoint to get a BPMN resource from Camunda.
     *
     * @param id The deploymentId.
     * @return A `RestResponse` containing the resource BPMN.
     */
    @Operation(operationId = "resource", summary = "Recupera risorsa BPMN/FORM/DMN.", description = "Recupera file BPMN per il dato deploymentId.")
    @APIResponse(responseCode = "200", description = "OK. Operazione eseguita con successo. Restituisce il file in formato Xml.", content = @Content(mediaType = "application/xml", schema = @Schema(implementation = String.class, format = "String", maxLength = 36)))
    @APIResponse(responseCode = "400", description = "BAD_REQUEST. Risorsa non trovata.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @APIResponse(responseCode = "404", description = "NOT_FOUND. Deployments non trovati.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR. Nel caso di errore generico.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @GET
    @Path("/deploy/{id}/data")
    @Produces(MediaType.APPLICATION_XML)
    public RestResponse<String> resource(@Schema(format = "String", maxLength = 36) @PathParam("id") @Size(max = 36) @Parameter(description = "Il deploymentId del bpmn da deployare") String id) {
        log.info("Executing RESOURCE. . .");
        long start = System.currentTimeMillis();

        RestResponse<String> response;

        try {
            response = processService.getResource(id);
        } catch (ProcessException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("Generic exception occured while processing get resource bpmn: ", e);
            throw new ProcessException(ProcessErrorEnum.GENERIC);
        } finally {
            logElapsedTime(PROCESS_RESOURCE_LOG_ID, start);
        }

        return response;
    }

    /**
     * Endpoint to start a BPMN process and to retireve active tasks.
     *
     * @param request The task request.
     * @return A `RestResponse` containing information about the active tasks.
     */
    @Operation(operationId = "startProcess", summary = "Esegue la 'start' dell'istanza di processo del flusso BPMN", description = "Esegue la 'start' dell'istanza di processo del flusso BPMN nel motore di workflow (es. Camunda) e restituisce la lista dei task attivi.")
    @APIResponse(responseCode = "200", description = "OK. Operazione eseguita con successo. Restituisce la lista dei task attivi del workflow. Task attivi non presenti se il bpmn risulta completato.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class)))
    @APIResponse(responseCode = "202", description = "ACCEPTED. Service task in esecuzione ma non ancora completato al momento della risposta.", content = @Content(schema = @Schema(implementation = RestResponse.Status.class)))
    @APIResponse(responseCode = "400", description = "BAD_REQUEST. Nel caso di 'businessKey' errata.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR. Nel caso di errore generico.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @APIResponse(responseCode = "503", description = "SERVICE_UNAVAILABLE. Nel caso di errore durante la start dell'istanza di processo del flusso BPMN ritornato da Camunda.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @POST
    @Path("/start")
    public RestResponse<TaskResponse> startProcess(@Parameter(description = "Il body della richiesta con le info del dispositivo, le info del task corrente e la mappa delle variabili di input") TaskRequest request) {
        log.info("Executing START. . .");
        long start = System.currentTimeMillis();

        RestResponse<TaskResponse> response;

        try {
            /*
             * Starting camunda process
             */
            processService.start(request.getTransactionId(), request.getFunctionId(), request.getDeviceInfo(),
                    request.getVariables());
            /*
             * Retrieve active tasks
             */
            response = processService.retrieveActiveTasks(request.getTransactionId());
        } catch (ProcessException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("Generic exception occured while starting process: ", e);
            throw new ProcessException(ProcessErrorEnum.GENERIC);
        } finally {
            logElapsedTime(PROCESS_START_PROCESS_LOG_ID, start);
        }

        return response;
    }

    /**
     * Endpoint to complete a Camunda task and to retrieve active tasks.
     *
     * @param request The task request.
     * @return A `RestResponse` containing information about active tasks.
     */
    @Operation(operationId = "next", summary = "Esegue il 'next' task dell'istanza di processo del flusso BPMN", description = "Esegue il 'next' task dell'istanza di processo del flusso BPMN nel motore di workflow (es. Camunda) e restituisce la lista dei task attivi.")
    @APIResponse(responseCode = "200", description = "OK. Operazione eseguita con successo. Restituisce la lista dei task attivi del workflow, dopo il completamento del task corrente. Task attivi non presenti se il bpmn risulta completato.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class)))
    @APIResponse(responseCode = "202", description = "ACCEPTED. Service task in esecuzione ma non ancora completato al momento della risposta.", content = @Content(schema = @Schema(implementation = RestResponse.Status.class)))
    @APIResponse(responseCode = "400", description = "BAD_REQUEST. Nel caso di 'taskId' nullo.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR. Nel caso di errore generico.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @APIResponse(responseCode = "503", description = "SERVICE_UNAVAILABLE. Nel caso di errore durante il completamento del task ritornato da Camunda.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @POST
    @Path("/next")
    public RestResponse<TaskResponse> next(
            @Parameter(description = "Il body della richiesta con le info del dispositivo, le info del task corrente e la mappa delle variabili di input") TaskRequest request) {

        log.info("Executing NEXT. . .");
        long start = System.currentTimeMillis();

        RestResponse<TaskResponse> response;

        try {
            /*
             * Checking presence of taskId for complete
             */
            if (request.getTaskId() != null && !request.getTaskId().isEmpty()) {
                /*
                 * Complete camunda task
                 */
                if (request.getVariables() != null && request.getVariables().get(Constants.FUNCTION_ID) != null) {
                    processService.complete(request.getTaskId(), request.getVariables(), request.getVariables().get(Constants.FUNCTION_ID).toString(), request.getDeviceInfo());
                } else {
                    processService.complete(request.getTaskId(), request.getVariables());
                }
            }
            /*
             * Retrieve active tasks
             */
            response = processService.retrieveActiveTasks(request.getTransactionId());
        } catch (ProcessException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("Generic exception occured while executing next: ", e);
            throw new ProcessException(ProcessErrorEnum.GENERIC);
        } finally {
            logElapsedTime(PROCESS_NEXT_LOG_ID, start);
        }

        return response;
    }

    /**
     * Endpoint to retrieve the variables of a task.
     *
     * @param request
     * @return A `RestResponse` containing variables of the task.
     */
    @Operation(operationId = "variables", description = "Recupero delle variabili del task presente nella richiesta.", summary = "Recupera le variabili dell'istanza di processo e filtra le stesse in base a quelle richieste dal task aggiungendovi le variabili e i bottoni di default.")
    @APIResponse(responseCode = "200", description = "OK. Operazione eseguita con successo. Restituisce la mappa delle variabili filtrate e le Taskvars del task corrente del workflow.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VariableResponse.class)))
    @APIResponse(responseCode = "400", description = "BAD REQUEST. Nel caso di errori di validazione o di una richiesta malformata.")
    @APIResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR. Nel caso di errore durante l'elaborazione.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @APIResponse(responseCode = "503", description = "SERVICE_UNAVAILABLE. Nel caso di errore durante il recupero delle variabili ritornato da Camunda.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @POST
    @Path("/variables")
    public RestResponse<VariableResponse> variables(VariableRequest request) {
        log.info("Executing VARIABLES. . .");
        long start = System.currentTimeMillis();

        RestResponse<VariableResponse> response;

        try {
            response = processService.getTaskVariables(request.getTaskId(), request.getVariables(), request.getButtons());
        } catch (ProcessException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("Generic exception occured while executing variables: ", e);
            throw new ProcessException(ProcessErrorEnum.GENERIC);
        } finally {
            logElapsedTime(PROCESS_VARIABLES_LOG_ID, start);
        }

        return response;
    }

    /**
     * Endpoint to retrieve the variables of a task.
     *
     * @param id
     * @return A `RestResponse` containing variables of the task.
     */
    @Operation(operationId = "undeploy", description = "Esegue l'undeploy del bpmn.", summary = "Effettua l'undeploy del bpmn.")
    @APIResponse(responseCode = "204", description = "OK. Operazione eseguita con successo.")
    @APIResponse(responseCode = "400", description = "BAD REQUEST. Nel caso di errori di validazione o di una richiesta malformata.")
    @APIResponse(responseCode = "404", description = "NOT_FOUND. Nel caso il deployment corrispondente al id non esiste.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "ERROR. Nel caso di errori durante l'undeploy.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessErrorResponse.class)))
    @POST
    @Path("/undeploy/{id}")
    public RestResponse<Object> undeploy(@Schema(format = "String", maxLength = 36) @PathParam("id") @Size(max = 36) @Parameter(description = "Il deploymentId del bpmn su cui effettuare l'undeploy") String id) {
        log.info("Executing UNDEPLOY. . .");
        long start = System.currentTimeMillis();

        RestResponse<Object> response;

        try {
            response = processService.undeploy(id);
        } catch (ProcessException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("Generic exception occured while executing variables: ", e);
            throw new ProcessException(ProcessErrorEnum.GENERIC);
        } finally {
            logElapsedTime(PROCESS_VARIABLES_LOG_ID, start);
        }

        return response;
    }
}
