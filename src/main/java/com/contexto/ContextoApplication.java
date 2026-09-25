package com.contexto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ContextoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContextoApplication.class, args);
    }
}
