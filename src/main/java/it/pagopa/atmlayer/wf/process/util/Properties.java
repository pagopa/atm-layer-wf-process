package it.pagopa.atmlayer.wf.process.util;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * <p>The {@code Properties} interface represents a configuration mapping for the workflow process module.</p>
 *
 * <p>This interface is annotated with {@code @ConfigMapping} to provide a configuration mapping for the module.
 * It defines methods that map to specific properties in the configuration, allowing for convenient access to
 * configuration values.</p>
 *
 */
@ApplicationScoped
@ConfigMapping(prefix = "wf-process")
public interface Properties {
    
    /**
     * Get the Camunda authentication username from the configuration.
     *
     * @return The Camunda authentication username.
     */
    @WithName("camunda.auth-username")
    public String camundaAuthUsername();

    /**
     * Get the Camunda authentication password from the configuration.
     *
     * @return The Camunda authentication password.
     */
    @WithName("camunda.auth-password")
    public String camundaAuthPassword();

    /**
     * Get the number of attempts for retrieving the task list from the configuration.
     *
     * @return The number of attempts for retrieving the task list.
     */
    @WithName("get-task-list.attempts")
    public int getTaskListAttempts();

    /**
     * Get the time to attempt (in milliseconds) for retrieving the task list from the configuration.
     *
     * @return The time to attempt for retrieving the task list.
     */
    @WithName("get-task-list.time-to-attempt")
    public long getTaskListTimeToAttempt();

}
