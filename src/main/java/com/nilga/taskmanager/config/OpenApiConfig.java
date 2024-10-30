package com.nilga.taskmanager.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up OpenAPI documentation for the Task Manager API.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates a custom {@link OpenAPI} bean for API documentation.
     * Sets up the API title, version, description, and license information.
     * Also adds an external documentation link.
     *
     * @return a configured {@link OpenAPI} instance with custom documentation settings
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .version("1.0.0")
                        .description("API for managing tasks")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Task Manager Documentation")
                        .url("https://example.com"));
    }
}
