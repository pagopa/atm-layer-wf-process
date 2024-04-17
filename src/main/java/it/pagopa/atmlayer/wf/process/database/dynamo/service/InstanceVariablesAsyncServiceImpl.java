package it.pagopa.atmlayer.wf.process.database.dynamo.service;

import java.util.List;
import java.util.stream.Collectors;

import io.smallrye.mutiny.Uni;
import it.pagopa.atmlayer.wf.process.database.dynamo.entity.InstanceVariables;
import it.pagopa.atmlayer.wf.process.database.dynamo.service.contract.InstanceVariablesService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@ApplicationScoped
public class InstanceVariablesAsyncServiceImpl extends InstanceVariablesService{

    @Inject
    DynamoDbAsyncClient dynamoDB;

    public Uni<List<InstanceVariables>> findAll() {
        return Uni.createFrom().completionStage(() -> dynamoDB.scan(scanRequest()))
                .onItem().transform(res -> res.items().stream().map(InstanceVariables::from).collect(Collectors.toList()));
    }
    
}