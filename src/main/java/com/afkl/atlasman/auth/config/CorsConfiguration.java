package com.afkl.atlasman.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@ConfigurationProperties(prefix = "cors")
@Data
public class CorsConfiguration {

    private int maxAge;
    private List<String> allowedMethods = new ArrayList<>();
    private List<String> allowedOrigins = new ArrayList<>();

}
