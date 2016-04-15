package com.afkl.atlasman.auth.crowd;

import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticatorImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelperImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractorImpl;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.CrowdClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.Properties;

import static com.atlassian.crowd.service.client.ClientPropertiesImpl.newInstanceFromProperties;

@Configuration
@ComponentScan("com.afkl.atlasman.auth.crowd")
public class CrowdClientConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public ClientPropertiesImpl clientProperties() {
        Properties properties = new Properties();
        properties.setProperty("application.name", environment.getRequiredProperty("atlassian.crowd.name"));
        properties.setProperty("application.password", environment.getRequiredProperty("atlassian.crowd.password"));
        properties.setProperty("crowd.server.url", environment.getRequiredProperty("atlassian.crowd.url"));
        properties.setProperty("session.validationInterval", environment.getRequiredProperty("atlassian.crowd.validationinterval"));
        return newInstanceFromProperties(properties);
    }

    @Bean
    public CrowdClient crowdClient() {
        return new RestCrowdClientFactory().newInstance(clientProperties());
    }

    @Bean
    public CrowdHttpAuthenticator crowdHttpAuthenticator() {
        return new CrowdHttpAuthenticatorImpl(crowdClient(), clientProperties(),
                CrowdHttpTokenHelperImpl
                        .getInstance(CrowdHttpValidationFactorExtractorImpl.getInstance()));
    }

}
