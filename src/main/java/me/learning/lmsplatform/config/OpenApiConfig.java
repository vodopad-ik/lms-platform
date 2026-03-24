package me.learning.lmsplatform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI lmsPlatformOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("LMS Platform API")
            .description("Learning Management System REST API with advanced caching")
            .version("1.0.0")
            .contact(new Contact()
                .name("LMS Team")
                .email("support@lms-platform.com")
                .url("https://lms-platform.com"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT")))
        .servers(List.of(
            new Server()
                .url("http://localhost:8080")
                .description("Development server"),
            new Server()
                .url("https://api.lms-platform.com")
                .description("Production server")
        ));
  }
}
