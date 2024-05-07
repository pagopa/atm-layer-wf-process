package it.pagopa.atmlayer.wf.process.service.impl;

import java.util.concurrent.CompletableFuture;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import it.pagopa.atmlayer.wf.process.bean.Task;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class PubSubService {
    private final PubSubCommands<Task> pubSubCommands;
    private final ValueCommands<String, Task> valueCommands;
    private PubSubCommands.RedisSubscriber subscriber;
    
    public PubSubService(RedisDataSource ds) {
        pubSubCommands = ds.pubsub(Task.class);
        valueCommands = ds.value(Task.class);
    }
    
    public SubscriptionPayload subscribe(String channel, String key) {
        CompletableFuture<Task> future = new CompletableFuture<>();   
        String cacheKey = key != null ? key : channel;       
        Task t = valueCommands.get(cacheKey);
        if (t != null ) {
            log.info("task in cache key :"+ key);
            future.complete(t);
            return SubscriptionPayload.builder()
                    .future(future)
                    .subscriber(subscriber).build();
            }
        log.info("task not in cache key :"+ key);    
       subscriber = pubSubCommands.subscribe(channel, task ->setTask(channel, future, task, cacheKey ));        
        return SubscriptionPayload.builder()
                .future(future)
                .subscriber(subscriber).build();
    }
    
    private boolean setTask(String channel, CompletableFuture<Task> future, Task task, String key ) {
        log.info("Task event received " + task.toString());
        CompletableFuture.runAsync(() ->{
            valueCommands.setex(key, 60L, task);
            log.info("Saved task in cache key :"+ key);
            if (subscriber != null)
                subscriber.unsubscribe();           
        });        
        return future.complete(task);
    }
    
    
    
    
}
