# Foro Hub API

> API REST para la gestión de un foro educativo - Challenge Backend de Alura Latam

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)

## Descripción

Foro Hub es una API REST que replica el funcionamiento de un foro educativo, permitiendo a los usuarios crear tópicos, asociarlos a cursos y gestionarlos de manera completa (CRUD). Desarrollada con Spring Boot y siguiendo las mejores prácticas de desarrollo backend.

## Características

- **CRUD completo de Tópicos** - Crear, leer, actualizar y eliminar tópicos
- **CRUD completo de Cursos** - Gestión de cursos del foro
- **Autenticación JWT** - Seguridad con JSON Web Tokens
- **Validaciones de negocio** - Validación de datos y reglas de negocio
- **Paginación y ordenamiento** - Listados paginados y ordenables
- **Documentación interactiva** - Swagger/OpenAPI integrado
- **Base de datos relacional** - MySQL con Flyway para migraciones
- **Soft delete** - Eliminación lógica de registros
- **Testing** - Tests unitarios y de integración

## Tecnologías

- **Java 17**
- **Spring Boot 3.5.10**
  - Spring Data JPA
  - Spring Security
  - Spring Validation
- **MySQL 8.0**
- **Flyway** - Migraciones de base de datos
- **JWT (jjwt 0.12.5)** - Autenticación
- **Lombok** - Reducción de boilerplate
- **Swagger/OpenAPI** - Documentación de API
- **Maven** - Gestión de dependencias

## Inicio Rápido

### Prerequisitos

- Java 17 o superior
- MySQL 8.0 o superior
- Maven 3.6+ (o usar el wrapper incluido `./mvnw`)

### Configuración

1. **Clonar el repositorio**
   ```bash
   git clone <url-del-repositorio>
   cd foro-challenge
   ```

2. **Configurar la base de datos**
   
   Crear una base de datos MySQL:
   ```sql
   CREATE DATABASE forohub;
   ```

3. **Configurar variables de entorno** (recomendado)
   
   Copiar el archivo de ejemplo y ajustar los valores:
   ```bash
   cp .env.example .env
   ```
   
   Editar `.env` con tus credenciales:
   ```env
   DB_NAME=forohub
   DB_USERNAME=forohub_user
   DB_PASSWORD=forohub_pass
   API_SECURITY_TOKEN_SECRET=tu_secret_seguro_aqui
   API_SECURITY_TOKEN_EXPIRATION=3600000
   ```
   
   **Nota:** Genera un secret seguro con: `openssl rand -base64 64`

4. **Compilar el proyecto**
   ```bash
   ./mvnw clean install
   ```

5. **Ejecutar la aplicación**
   ```bash
   ./mvnw spring-boot:run
   ```

La aplicación estará disponible en `http://localhost:8080`

## Documentación API (Swagger)

Una vez iniciada la aplicación, accede a la documentación interactiva:

**Swagger UI:** http://localhost:8080/swagger-ui.html

### Cómo usar Swagger

1. **Registrar un usuario**: `POST /auth/register`
2. **Iniciar sesión**: `POST /auth/login` (obtendrás un token JWT)
3. **Autorizar**: Click en el botón "Authorize" 🔓 e ingresar: `Bearer {tu-token}`
4. **Probar endpoints**: Todos los endpoints están listos para probar

## Endpoints Principales

### Autenticación (públicos)
- `POST /auth/register` - Registrar nuevo usuario
- `POST /auth/login` - Iniciar sesión

### Tópicos (requieren autenticación)
- `POST /topicos` - Crear tópico
- `GET /topicos` - Listar tópicos (paginado)
- `GET /topicos/{id}` - Obtener tópico por ID
- `PUT /topicos/{id}` - Actualizar tópico
- `DELETE /topicos/{id}` - Eliminar tópico

### Cursos (requieren autenticación)
- `POST /cursos` - Crear curso
- `GET /cursos` - Listar cursos (paginado)
- `GET /cursos/{id}` - Obtener curso por ID
- `PUT /cursos/{id}` - Actualizar curso
- `DELETE /cursos/{id}` - Eliminar curso

## Estructura del Proyecto

```
src/main/java/com/foro_hub/
├── config/          # Configuraciones (Security, OpenAPI)
├── controller/      # Controladores REST
├── domain/          # Entidades JPA
├── dto/             # Data Transfer Objects
├── exception/       # Manejo de excepciones
├── mapper/          # Mappers entre entidades y DTOs
├── repository/      # Repositorios JPA
├── security/        # Configuración de seguridad JWT
├── service/         # Lógica de negocio
└── util/            # Utilidades
```

## Testing

Ejecutar los tests:
```bash
./mvnw test
```

## Seguridad

- **Autenticación**: JWT (JSON Web Tokens)
- **Autorización**: Todos los endpoints (excepto `/auth/**`) requieren token válido
- **Contraseñas**: Encriptadas con BCrypt
- **Tokens**: Expiran en 1 hora (configurable)

## Docker (Opcional)

Si prefieres usar Docker para MySQL:

1. **Asegúrate de tener el archivo `.env` configurado**
   ```bash
   cp .env.example .env
   # Edita el .env con tus valores
   ```

2. **Levantar MySQL con Docker**
   ```bash
   docker-compose up -d
   ```

3. **Verificar que MySQL esté corriendo**
   ```bash
   docker-compose ps
   ```

4. **Detener MySQL**
   ```bash
   docker-compose down
   ```

Esto levantará MySQL automáticamente con la configuración del `.env`

## Validaciones de Negocio

- No se permiten tópicos duplicados (mismo título y mensaje)
- No se permiten emails duplicados al registrarse
- Todos los campos requeridos son validados
- Soft delete: Los registros no se eliminan físicamente

## Paginación

Los endpoints de listado soportan paginación:

```
GET /topicos?page=0&size=10&sort=fechaCreacion,desc
```

Parámetros:
- `page`: Número de página (comienza en 0)
- `size`: Cantidad de elementos por página
- `sort`: Campo y dirección de ordenamiento

## Troubleshooting

### Error de conexión a MySQL
- Verificar que MySQL esté corriendo
- Verificar credenciales en `application.yaml` o variables de entorno

### Error 401 Unauthorized
- Verificar que el token JWT sea válido y no haya expirado
- Verificar que el header `Authorization` tenga el formato: `Bearer {token}`

### Error de compilación
```bash
./mvnw clean install -U
```

## Licencia

Este proyecto es parte del Challenge Backend de Alura Latam.

## Autor

Nicolas Bon - Desarrollado como parte del programa ONE - Oracle Next Education de Alura Latam
