package com.afkl.atlasman.auth;

import com.afkl.atlasman.auth.config.AuthorizationServerConfiguration;
import com.afkl.atlasman.auth.config.WebSecurityConfiguration;
import com.afkl.atlasman.auth.crowd.CrowdClientConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * TODO add docs and tests...
 */
@Import({CrowdClientConfiguration.class, WebSecurityConfiguration.class, AuthorizationServerConfiguration.class})
@Configuration
@EnableAutoConfiguration
public class Bootstrap {

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }

}
