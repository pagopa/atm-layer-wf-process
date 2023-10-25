package it.pagopa.atmlayer.wf.process.util;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigMapping(prefix = "wf-process")
public interface Properties {
    
    @WithName("camunda.auth-username")
    public String camundaAuthUsername();

    @WithName("camunda.auth-password")
    public String camundaAuthPassword();

}
