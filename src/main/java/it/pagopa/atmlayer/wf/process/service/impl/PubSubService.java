package it.pagopa.atmlayer.wf.process.service.impl;

import java.util.concurrent.CompletableFuture;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import it.pagopa.atmlayer.wf.process.bean.Task;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PubSubService {
    private final PubSubCommands<Task> pubSubCommands;
    
    public PubSubService(RedisDataSource ds) {
        pubSubCommands = ds.pubsub(Task.class);
    }
    
    public SubscriptionPayload subscribe(String channel) {
        CompletableFuture<Task> future = new CompletableFuture<>();       
        PubSubCommands.RedisSubscriber subscriber = pubSubCommands.subscribe(channel, future::complete);
        return SubscriptionPayload.builder()
                .future(future)
                .subscriber(subscriber).build();
    }
    
}
