package it.pagopa.atmlayer.wf.process.service.impl;

import java.util.concurrent.CompletableFuture;

import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import it.pagopa.atmlayer.wf.process.bean.Task;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionPayload {
    private PubSubCommands.RedisSubscriber subscriber;
    private CompletableFuture<Task> future;
}