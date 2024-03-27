package it.pagopa.atmlayer.wf.process.client.transactions;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import it.pagopa.atmlayer.wf.process.client.transactions.bean.TransactionServiceRequest;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

@RegisterRestClient(configKey = "transactions-service-rest-client")
public interface TransactionsServiceRestClient {
    
    @POST
    @Path("/insert")
    void inset(TransactionServiceRequest request);

    @PUT
    @Path("/update")
    void update(TransactionServiceRequest request);

}