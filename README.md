# Booking Demo System

Sistema de reservas tipo Booking/Airbnb desarrollado con Spring Boot.

Incluye:

* API REST + GraphQL
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
* Spring GraphQL
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

# Cómo Ejecutar

## 1. Compilar el proyecto

IMPORTANTE:

Antes de ejecutar Docker Compose debes generar el JAR.

```bash
mvn clean package
```

---

## 2. Levantar containers

```bash
docker compose up --build
```

---

# Problema Común en Ubuntu (Docker Context)

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
* GraphQL
* Swagger/OpenAPI
* Observabilidad completa
* Dockerización
* Persistencia H2
* Dashboards Grafana listos para integrar
