package it.pagopa.atmlayer.wf.process.database.dynamo.service;

import java.util.List;
import java.util.stream.Collectors;

import it.pagopa.atmlayer.wf.process.database.dynamo.entity.InstanceVariables;
import it.pagopa.atmlayer.wf.process.database.dynamo.service.contract.InstanceVariablesService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@ApplicationScoped
public class InstanceVariablesServiceImpl extends InstanceVariablesService{

    @Inject
    DynamoDbClient dynamoDB;

    public List<InstanceVariables> findAll() {
        return dynamoDB.scanPaginator(scanRequest()).items().stream()
                .map(InstanceVariables::from)
                .collect(Collectors.toList());
    }
    
}