package it.pagopa.atmlayer.wf.process.client.transactions.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
public class TransactionServiceRequest {

    private String functionType;

    private String transactionId;
    
    private String acquirerId;
    
    private String branchId;
    
    private String terminalId;
    
    private String transactionStatus;

    public TransactionServiceRequest(String functionType, String transactionId, String transactionStatus) {
        super();
        this.functionType = functionType;
        this.transactionId = transactionId;
        this.transactionStatus = transactionStatus;
    }
}