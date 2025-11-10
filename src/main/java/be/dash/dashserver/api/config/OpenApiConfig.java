package be.dash.dashserver.api.config;

import java.util.Arrays;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(Arrays.asList(
                        new Server().url("https://dev.da-sh.kr").description("Development Server URL"),
                        new Server().url("http://localhost:8080").description("Local Server URL")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Auth"))
                .components(attachBearerAuthScheme());
    }

    private Components attachBearerAuthScheme() {
        return new Components().addSecuritySchemes("Auth",
                new SecurityScheme()
                        .name("Authorization")
                        .type(Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Use 'Authorization: Bearer <token>' header"));
    }

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("V1 API")
                .pathsToExclude("/api/v2/**", "/api/v3/**", "/api/v4/**")
                .build();
    }

    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
                .group("V2 API")
                .pathsToMatch("/api/v2/**")
                .build();
    }

    @Bean
    public GroupedOpenApi v3Api() {
        return GroupedOpenApi.builder()
                .group("V3 API")
                .pathsToMatch("/api/v3/**")
                .build();
    }

    @Bean
    public GroupedOpenApi v4Api() {
        return GroupedOpenApi.builder()
                .group("V4 API")
                .pathsToMatch("/api/v4/**")
                .build();
    }
}


