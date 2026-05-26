# Booking Demo System

Sistema de reservas tipo Booking/Airbnb desarrollado con Spring Boot y seguridad de grado empresarial.

## 🚀 Tecnologías Utilizadas

* **Backend**: Java 17, Spring Boot 3.5.x
* **Seguridad**: **Keycloak (OAuth2 / OpenID Connect)**, Spring Security Resource Server
* **Base de Datos**: H2 Database (Persistencia en archivo)
* **Infraestructura**: Docker / Docker Compose
* **Observabilidad**: Prometheus, Grafana, Spring Actuator
* **Herramientas**: Maven, Lombok, MapStruct, SpringDoc (Swagger)

---

## 🏗️ Arquitectura de Seguridad (Migración a Keycloak)

El sistema utiliza **Keycloak** como Servidor Central de Identidades. 

### Características Pro Implementadas:
* **Just-In-Time Provisioning**: El backend crea automáticamente el perfil del usuario en la base de datos local la primera vez que inicia sesión con éxito en Keycloak. No se requiere registro manual en dos pasos.
* **Limpieza de Código (Registro Delegado)**: Se eliminaron por completo los endpoints obsoletos de registro de usuarios del backend de Spring Boot (`POST /api/v1/user`), delegando el 100% de la gestión de identidades y credenciales de forma segura a Keycloak.
* **Role Mapping**: Los roles definidos en Keycloak se mapean automáticamente a `ROLE_XXX` en Spring Security.
* **Red Unificada**: Todos los servicios utilizan el host `auth-server` para garantizar la consistencia de los tokens JWT.

---

## 🔗 URLs de Interés (Entorno Desarrollo)

| Servicio | URL | Descripción |
| :--- | :--- | :--- |
| **App Root** | [http://localhost:8080](http://localhost:8080) | Página de bienvenida pública. |
| **Keycloak Admin** | [http://auth-server:8180](http://auth-server:8180) | Consola de administración (admin/admin). |
| **Formulario de Registro (Usuario)** | [http://auth-server:8180/realms/booking-realm/protocol/openid-connect/registrations?client_id=booking-app&response_type=code&scope=openid&redirect_uri=http://localhost:8080](http://auth-server:8180/realms/booking-realm/protocol/openid-connect/registrations?client_id=booking-app&response_type=code&scope=openid&redirect_uri=http://localhost:8080) | Enlace público para registrar nuevos usuarios sin tokens de administrador. (Abrir en **incógnito**). |
| **Swagger UI** | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) | Documentación interactiva de la API. |
| **Grafana** | [http://localhost:3000](http://localhost:3000) | Dashboards de monitoreo (admin/admin). |

---

## 🛠️ Configuración de Red (IMPORTANTE)

Para que el sistema funcione, debes añadir la siguiente línea a tu archivo `/etc/hosts`:
```text
127.0.0.1 auth-server
```

---

## 📝 Estado Actual del Proyecto

- [x] Migración de JWT Manual a OAuth2 (Keycloak).
- [x] Construcción multi-etapa en Docker (Multi-stage build) optimizada.
- [x] Eliminación de dependencias obsoletas (Redis para seguridad, JJWT).
- [x] **Just-In-Time Provisioning**: Sincronización automática Keycloak -> DB Local.
- [x] **Eliminación de Registro Obsoleto**: Endpoints de usuario en Spring Boot eliminados para delegar en Keycloak.
- [x] **Corrección de Auditoría JPA**: Integrado `@PrePersist` en `Spot` para evitar errores con el campo `createdAt`.
- [x] Host unificado `auth-server` para validación de tokens.
- [ ] Implementación de RBAC (Role Based Access Control) avanzado en controladores.

---

## 🛑 Troubleshooting / Problemas Conocidos

**Mensaje: "You are already authenticated as different user"**
* **Causa**: Tienes una sesión activa en el navegador con otro usuario en Keycloak.
* **Solución**: Cierra sesión o usa una ventana de **incógnito** para probar nuevos registros.

**Error: 401 Unauthorized**
* **Causa**: El token ha expirado o no se ha enviado en la cabecera `Authorization`.
* **Solución**: Renueva el token en Postman usando el `refresh_token` o haciendo login de nuevo.
