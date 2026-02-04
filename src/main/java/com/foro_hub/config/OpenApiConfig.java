package com.foro_hub.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Foro Hub API",
                version = "1.0",
                description = """
                        API REST para la gestión de un foro educativo desarrollado como parte del Challenge Backend de Alura Latam.
                        
                        Esta API permite:
                        - Crear, leer, actualizar y eliminar tópicos
                        - Gestionar cursos
                        - Autenticación y autorización con JWT
                        - Paginación y ordenamiento de resultados
                        
                        **Nota:** La mayoría de los endpoints requieren autenticación.
                        Primero registra un usuario en `/auth/register` y luego inicia sesión en `/auth/login` para obtener tu token JWT.
                        """
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Authentication. Obtén tu token en el endpoint /auth/login",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
