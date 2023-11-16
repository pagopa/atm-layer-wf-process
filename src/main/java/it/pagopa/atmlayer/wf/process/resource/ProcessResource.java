package it.pagopa.atmlayer.wf.process.resource;

import java.io.IOException;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.process.bean.TaskRequest;
import it.pagopa.atmlayer.wf.process.bean.TaskResponse;
import it.pagopa.atmlayer.wf.process.bean.VariableRequest;
import it.pagopa.atmlayer.wf.process.bean.VariableResponse;
import it.pagopa.atmlayer.wf.process.enums.ProcessErrorEnum;
import it.pagopa.atmlayer.wf.process.exception.ProcessException;
import it.pagopa.atmlayer.wf.process.service.ProcessService;
import it.pagopa.atmlayer.wf.process.util.Constants;
import it.pagopa.atmlayer.wf.process.util.Utility;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Pasquale Sansonna
 * 
 *         <p>
 *         This class defines REST endpoints for managing BPM processes through
 *         Camunda.
 *         </p>
 */
@Slf4j
@Path("/api/v1/processes")
public class ProcessResource {

    @Inject
    ProcessService processService;

    /**
     * Endpoint to deploy a BPMN process definition to Camunda.
     *
     * @param request The task request.
     * @return A `RestResponse` containing the deployment outcome.
     */
    @Operation(summary = "Esegue il 'deploy' di un flusso BPMN", description = "Esegue il deploy di un flusso BPMN nel motore di workflow (es. Camunda)")
    @APIResponse(responseCode = "200", description = "OK. Operazione eseguita con successo. Restituisce l'ID della risorsa creata nel motore di workflow.", content = @Content(schema = @Schema(implementation = RestResponse.class)))
    @APIResponse(responseCode = "400", description = "BAD_REQUEST. Nel caso di richiesta errata.", content = @Content(schema = @Schema(implementation = RestResponse.Status.class)))
    @APIResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR. Nel caso di errore durante il deploy del flusso BPMN.", content = @Content(schema = @Schema(implementation = RestResponse.Status.class)))
    @POST
    @Path("/deploy/{resourceType:.*}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public RestResponse<Object> deploy(
            @Parameter(description = "L'url da cui recuperare il file bpmn.") @RestForm("url") String requestUrl,
            @Parameter(description = "Tipo di File (BPMN, DMN...)") @PathParam("resourceType") String resourceType) {
        log.info("Executing DEPLOY. . .");
        RestResponse<Object> response;
        String fileName = new StringBuilder().append(UUID.randomUUID().toString()).append(Constants.DOT).append(resourceType.toLowerCase()).toString();

        try {
            response = processService.deploy(requestUrl, fileName);
        } catch (RuntimeException | IOException e) {
            log.error("Generic exception occured while deploying: ", e);
            throw new ProcessException(ProcessErrorEnum.GENERIC);
        } finally {
            // Delete of temp bpmn used for deploy on Camunda platform
            Utility.deleteFileIfExists(fileName);
        }

        return response;
    }

    /**
     * Endpoint to start a BPMN process and to retireve active tasks.
     *
     * @param request The task request.
     * @return A `RestResponse` containing information about the active tasks.
     */
    @Operation(summary = "Esegue la 'start' dell'istanza di processo del flusso BPMN", description = "Esegue la 'start' dell'istanza di processo del flusso BPMN nel motore di workflow (es. Camunda) e restituisce la lista dei task attivi.")
    @APIResponse(responseCode = "200", description = "OK. Operazione eseguita con successo. Restituisce la lista dei task attivi del workflow.", content = @Content(schema = @Schema(implementation = TaskResponse.class)))
    @APIResponse(responseCode = "400", description = "BAD_REQUEST. Nel caso di 'businessKey' errata.", content = @Content(schema = @Schema(implementation = RestResponse.Status.class)))
    @APIResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR. Nel caso di errore durante la start dell'istanza di processo del flusso BPMN.", content = @Content(schema = @Schema(implementation = RestResponse.Status.class)))
    @POST
    @Path("/start")
    public RestResponse<TaskResponse> startProcess(
            @Parameter(description = "Il body della richiesta con le info del dispositivo, le info del task corrente e la mappa delle variabili di input") TaskRequest request) {
        log.info("Executing START. . .");
        RestResponse<TaskResponse> response = null;

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
        }

        return response;
    }

    /**
     * Endpoint to complete a Camunda task and to retireve active tasks.
     *
     * @param request The task request.
     * @return A `RestResponse` containing information about active tasks.
     */
    @Operation(summary = "Esegue il 'next' task dell'istanza di processo del flusso BPMN", description = "Esegue il 'next' task dell'istanza di processo del flusso BPMN nel motore di workflow (es. Camunda) e restituisce la lista dei task attivi.")
    @APIResponse(responseCode = "200", description = "OK. Operazione eseguita con successo. Restituisce la lista dei task attivi del workflow, dopo il completamento del task corrente.", content = @Content(schema = @Schema(implementation = TaskResponse.class)))
    @APIResponse(responseCode = "400", description = "BAD_REQUEST. Nel caso di 'taskId' nullo.", content = @Content(schema = @Schema(implementation = RestResponse.Status.class)))
    @APIResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR. Nel caso di errore durante l'elaborazione dell'istanza di processo del flusso BPMN.", content = @Content(schema = @Schema(implementation = RestResponse.Status.class)))
    @POST
    @Path("/next")
    public RestResponse<TaskResponse> next(
            @Parameter(description = "Il body della richiesta con le info del dispositivo, le info del task corrente e la mappa delle variabili di input") TaskRequest request) {
        log.info("Executing NEXT. . .");
        RestResponse<TaskResponse> response;

        try {
            /*
             * Checking presence of taskId for complete
             */
            if (request.getTaskId() == null) {
                log.error("Next failed! taskId is missing.");
                throw new ProcessException(ProcessErrorEnum.TASK_ID_NOT_PRESENT);
            } else {
                /*
                 * Complete camunda task
                 */
                processService.complete(request.getTaskId(), request.getVariables());
                /*
                *  Retrieve active tasks
                */
                response = processService.retrieveActiveTasks(request.getTransactionId());
            }
        } catch (ProcessException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("Generic exception occured while executing next: ", e);
            throw new ProcessException(ProcessErrorEnum.GENERIC);
        }

        return response;
    }

    @Operation(summary = "Recupera le variabili dell'istanza di processo e filtra le stesse in base a quelle richieste dal task aggiungendovi le TaskVars")
    @APIResponse(responseCode = "200", description = "OK. Operazione eseguita con successo. Restituisce la mappa delle variabili filtrate e le Taskvars del task corrente del workflow.", content = @Content(schema = @Schema(implementation = VariableResponse.class)))
    @APIResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR. Nel caso di errore durante l'elaborazione.", content = @Content(schema = @Schema(implementation = RestResponse.Status.class)))
    @POST
    @Path("/variables")
    public RestResponse<VariableResponse> variables(VariableRequest request) {
        log.info("Executing VARIABLES. . .");
        RestResponse<VariableResponse> response;

        try {
            response = RestResponse.ok(
                    processService.getTaskVariables(request.getTaskId(), request.getVariables(), request.getButtons()));
        } catch (ProcessException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("Generic exception occured while executing variables: ", e);
            throw new ProcessException(ProcessErrorEnum.GENERIC);
        }

        return response;
    }

}
