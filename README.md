# Booking Demo System

Este es un proyecto de práctica de un sistema de reservas (tipo Booking/Airbnb) desarrollado con **Spring Boot** y **H2 Database**.

## Características del Proyecto

### 1. Base de Datos
- **Motor**: H2 Database (en memoria).
- **Consola H2**: Disponible en `http://localhost:8080/h2-console`.
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **User**: `sa` / **Password**: (vacío).

### 2. Entidades Principales
- **User**: Maneja usuarios con roles (`ADMIN`, `ARRENDATARIO`, `USUARIO`).
- **Spot**: Representa los alojamientos (casa, apartamento, etc.).
- **Booking**: Gestiona las reservas conectando usuarios con spots y fechas.

### 3. Funcionalidades de Diseño
- Gestión de roles mediante Enums.
- Relación `@ManyToOne` entre reservaciones y usuarios/alojamientos.
- Persistencia automática mediante Spring Data JPA.

## Tecnologías Utilizadas
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Maven**
- **Lombok** (para reducción de código repetitivo)
- **H2 Database**

## Cómo Ejecutar
1. Clona el repositorio.
2. Ejecuta `./mvnw spring-boot:run`.
3. Accede a la consola H2 para verificar la creación de las tablas.
