package com.booking_1.demo.core.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
            "message", "¡Bienvenido a Booking Demo API!",
            "status", "Running",
            "security", "OAuth2 / Keycloak Enabled",
            "documentation", "/swagger-ui/index.html"
        );
    }
}
