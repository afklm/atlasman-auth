package com.afkl.atlasman.auth.config;

import com.afkl.atlasman.auth.crowd.Crowd;
import com.afkl.atlasman.auth.crowd.CrowdAuthenticationProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Import(CorsConfiguration.class)
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private Crowd crowd;
    @Autowired
    private CorsConfiguration corsConfiguration;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new CrowdAuthenticationProvider(crowd));
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(corsConfiguration.getAllowedOrigins().toArray(new String[corsConfiguration.getAllowedOrigins().size()]))
                        .allowCredentials(false)
                        .allowedMethods(corsConfiguration.getAllowedMethods().toArray(new String[corsConfiguration.getAllowedMethods().size()]))
                        .maxAge(corsConfiguration.getMaxAge());
            }
        };
    }

}
