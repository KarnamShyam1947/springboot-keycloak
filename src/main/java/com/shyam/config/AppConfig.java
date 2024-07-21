package com.shyam.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    
    @Value("${keycloak.tokenEndpoint}")
    private String tokenEndpoint;

    @Bean
    RestTemplate getKeyCloakRestTemplate() {
        return new RestTemplateBuilder()
                    .rootUri(tokenEndpoint)
                    .build();
    }

}
