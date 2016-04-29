package com.afkl.atlasman.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "cors")
@Data
public class AtlasmanCorsConfiguration {

    private int maxAge;
    private List<String> allowedMethods = new ArrayList<>();
    private List<String> allowedOrigins = new ArrayList<>();

}
