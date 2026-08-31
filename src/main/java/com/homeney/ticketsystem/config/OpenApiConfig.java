package com.homeney.ticketsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Homeney Cyber Ticket API")
                        .version("2.0.0")
                        .description("API REST moderna para gerenciamento centralizado de chamados e suporte técnico com estética Dark Tech HUD.")
                        .contact(new Contact()
                                .name("Equipe Homeney")
                                .email("dev@homeney.local"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
