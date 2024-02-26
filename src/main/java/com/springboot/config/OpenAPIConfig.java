package com.springboot.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@io.swagger.v3.oas.annotations.security.SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "Swagger", version = "1.0.0"))
@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {

}
