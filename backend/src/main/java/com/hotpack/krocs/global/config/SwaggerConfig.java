package com.hotpack.krocs.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OperationCustomizer;
import com.hotpack.krocs.global.security.annotation.Login;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnClass(OpenAPI.class)
public class SwaggerConfig {

    @Value("${SPRINGDOC_SWAGGER_UI_SERVERS_0_URL:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Krocs API")
                        .version("v1")
                        .description("Krocs API 명세서"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addServersItem(new Server()
                        .url(serverUrl)
                        .description("API Server"));
    }

    @Bean
    public OperationCustomizer hideLoginArguments() {
        return (operation, handlerMethod) -> {
            Set<String> loginParamNames = Arrays.stream(handlerMethod.getMethodParameters())
                    .filter(p -> p.hasParameterAnnotation(Login.class))
                    .map(p -> p.getParameterName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            if (operation.getParameters() != null && !loginParamNames.isEmpty()) {
                operation.setParameters(
                        operation.getParameters().stream()
                                .filter(p -> !loginParamNames.contains(p.getName()))
                                .collect(Collectors.toList())
                );
            }
            return operation;
        };
    }
}