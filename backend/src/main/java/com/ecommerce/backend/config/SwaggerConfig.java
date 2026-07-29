package com.ecommerce.backend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI ecommerceOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Multi-Tenant E-Commerce API")
                        .description("REST APIs for Multi-Tenant E-Commerce Application")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Piya")
                                .email("piya07203@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation"));
    }
}