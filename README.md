# Booking Demo System

Sistema de reservas tipo Booking/Airbnb desarrollado con Spring Boot.

Incluye:

* API REST
* Seguridad JWT
* Redis
* H2 Database
* Observabilidad con Prometheus + Grafana
* Docker Compose

---

# Tecnologías Utilizadas

* Java 17
* Spring Boot 3.5.x
* Spring Data JPA
* Spring Security
* Spring Security
* Redis
* H2 Database
* Prometheus
* Grafana
* Docker / Docker Compose
* Maven
* Lombok
* MapStruct

---

# Arquitectura

El sistema utiliza:

* Spring Boot como backend principal
* H2 como base de datos persistente local
* Redis para blacklist/cache de tokens
* Prometheus para scraping de métricas
* Grafana para dashboards y monitoreo

---

# Servicios Docker

## App

Aplicación Spring Boot expuesta en:

```text
http://localhost:8080
```

---

## H2 Console

```text
http://localhost:8080/h2-console
```

### Configuración H2

```text
JDBC URL: jdbc:h2:file:/app/data/bookingdb
User: sa
Password:
```

---

## Prometheus

```text
http://localhost:9090
```

Verificar targets:

```text
Status -> Targets
```

Debe aparecer:

```text
booking-system UP
```

---

## Grafana

```text
http://localhost:3000
```

### Credenciales

```text
user: admin
password: admin
```

---

# Entidades Principales

## User

Manejo de usuarios y roles:

* ADMIN
* ARRENDATARIO
* USUARIO

---

## Spot

Representa alojamientos:

* casas
* apartamentos
* habitaciones

---

## Booking

Gestiona reservas:

* usuario
* spot
* fechas

---

# Observabilidad

La aplicación expone métricas mediante Spring Actuator:

```text
http://localhost:8080/actuator/prometheus
```

Métricas disponibles:

* JVM
* memoria
* CPU
* requests HTTP
* tiempos de respuesta
* threads
* garbage collector

---

# Dependencias de Observabilidad

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

---

# application.properties

```properties
spring.application.name=booking-app

# H2
spring.datasource.url=jdbc:h2:file:/app/data/bookingdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA
spring.jpa.hibernate.ddl-auto=update

# Redis
spring.data.redis.host=redis
spring.data.redis.port=6379

# Actuator
management.endpoints.web.exposure.include=health,info,prometheus
management.endpoint.prometheus.access=unrestricted
management.prometheus.metrics.export.enabled=true
```

---

# Dockerfile

```dockerfile
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
```

---

# docker-compose.yml

```yaml
services:

  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - redis
    environment:
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATASOURCE_URL: jdbc:h2:file:/app/data/bookingdb
    volumes:
      - ./data:/app/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    depends_on:
      - app

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      GF_SECURITY_ADMIN_PASSWORD: admin
    depends_on:
      - prometheus
```

---

# prometheus.yml

```yaml
global:
  scrape_interval: 5s
  evaluation_interval: 5s

scrape_configs:
  - job_name: 'booking-system'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['app:8080']
```

---

# Cómo Ejecutar (Guía paso a paso)

Para arrancar el sistema completo con Docker, sigue estos pasos en orden:

## Paso 1: Preparar el motor de Docker (Solo si usas Linux/Ubuntu)
Si tienes instalado Docker Desktop, asegúrate de estar usando el contexto nativo para evitar errores de conexión:

```bash
# Cambiar al contexto por defecto
docker context use default

# Verificar que el contexto es el correcto (debe tener un *)
docker context ls
```

## Paso 2: Compilar el proyecto y generar el JAR
Docker necesita el archivo ejecutable de Java. Genéralo con Maven:

```bash
./mvnw clean package -DskipTests
```

## Paso 3: Levantar los contenedores
Este comando descargará las imágenes (Redis, Prometheus, Grafana) y construirá la imagen de tu App:

```bash
docker --context default compose up --build
```

---

## Paso 4: Verificación de servicios
Una vez que veas logs en la terminal, puedes acceder a:

*   **App API:** [http://localhost:8080](http://localhost:8080)
*   **Frontend Angular:** [http://localhost:4200](http://localhost:4200)
*   **Grafana:** [http://localhost:3000](http://localhost:3000) (User: `admin` / Pass: `admin`)
*   **Prometheus:** [http://localhost:9090](http://localhost:9090)
*   **H2 Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

### Credenciales de prueba
Para probar el sistema rápidamente sin registrarte:
*   **Email:** `ricardo@email.com`
*   **Contraseña:** `ricardo123`

---

# Solución de problemas comunes

Si `docker compose up` se queda congelado o:

```bash
docker info
```

se queda en:

```text
Server:
```

el problema normalmente es el Docker context.

Verificar:

```bash
docker context ls
```

Si aparece:

```text
desktop-linux *
```

cambiar al daemon nativo:

```bash
docker context use default
```

Luego reiniciar Docker:

```bash
sudo systemctl restart docker
```

Verificar:

```bash
docker info
```

---

# Logs útiles

## Ver logs de la app

```bash
docker compose logs -f app
```

---

## Ver todos los logs

```bash
docker compose logs -f
```

---

# Métricas útiles para Grafana

## Requests HTTP

```text
rate(http_server_requests_seconds_count[1m])
```

## Memoria JVM

```text
jvm_memory_used_bytes
```

## CPU

```text
system_cpu_usage
```

## Threads

```text
jvm_threads_live_threads
```

---

# Estado Actual del Proyecto

* JWT Authentication
* Redis Token Blacklist
* Swagger/OpenAPI
* Observabilidad completa
* Dockerización
* Persistencia H2
* Dashboards Grafana listos para integrar

---

# Estrategia de Calidad y Pruebas (Roadmap)

Para asegurar la estabilidad del sistema, se ha definido el siguiente plan de pruebas y control de calidad:

## 1. Pruebas Unitarias (Lógica de Negocio)
**Objetivo:** Validar reglas de negocio aisladas en la capa de servicios.
*   **Estado:** [En progreso]
*   **Checks:**
    *   [x] Validación de capacidad máxima en reservas.
    *   [x] Validación de duración máxima de estancia (30 días).
    *   [x] Validación de fechas (check-in no pasado).
    *   [x] Validación de fechas (check-out posterior a check-in).
    *   [x] Validación de flag `isAvailable` del Spot.
    *   [x] Cálculo exacto del precio total (Noches * Precio).
    *   [x] Flujo exitoso de creación de reserva (Happy Path).

## 2. Pruebas de Integración (Persistencia)
**Objetivo:** Validar que las consultas SQL y la interacción con la BD son correctas.
*   **Estado:** [En progreso]
*   **Checks:**
    *   [x] Lógica de solapamiento (`existsOverlappingBooking`) - Casos base.
    *   [x] Lógica de solapamiento - Casos borde (envolvente, interno, solape final).
    *   [x] Persistencia correcta de relaciones entre Usuario, Spot y Booking.
    *   [x] Integración con H2 en modo persistente (Archivo).

## 3. Pruebas de API y Seguridad
**Objetivo:** Validar endpoints REST/GraphQL y control de acceso.
*   **Estado:** [En progreso]
*   **Checks:**
    *   [x] Protección de rutas por Roles (RBAC).
    *   [x] Validación de propiedad del Spot (Ownership) para edición.
    *   [ ] Validación de tokens JWT y revocación en Redis (Logout/Blacklist).
    *   [ ] Formato de respuestas de error global (`ControllerAdvice`).

## 4. Pruebas de Carga y Stress
**Objetivo:** Asegurar que el sistema aguanta tráfico concurrente (evitar overbooking accidental).
*   **Estado:** [Completado]
*   **Checks:**
    *   [x] Stress test con K6 para verificar `PESSIMISTIC_WRITE` (evitar overbooking).
    *   [x] Stress test de Login y generación de JWT.

---

# Debilidades Detectadas y Mejoras Pendientes

Durante la auditoría de código se han identificado los siguientes puntos de mejora:

1.  **Seguridad en Reservas:** Validar que el `guestId` enviado en el DTO coincida con el usuario autenticado para evitar que un usuario cree reservas a nombre de otro.
2.  **Robustez de Fechas:** Implementar validaciones más estrictas en la capa de entidad o mediante anotaciones personalizadas de Bean Validation.
3.  **Entorno de Test:** Migrar de H2 a **Testcontainers** con PostgreSQL para asegurar que los bloqueos pesimistas se comportan exactamente igual que en producción.
4.  **Documentación API:** Publicar la URL de Swagger UI en este README.
5.  **Centralización de Logs:** Mejorar el formato de logs para que sean fácilmente parseables por herramientas de observabilidad.
