package com.booking_1.demo.core.security.exceptions;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.booking_1.demo.core.exceptions.ErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("aplication/json");

        ErrorResponse errorResponse = new ErrorResponse(
                java.time.LocalDateTime.now(),
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden",
                "¡Alto ahí! No tienes los permisos necesarios (" + accessDeniedException.getMessage() + ")",
                request.getRequestURI()

        );
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()); // Necesario para parsear
                                                                                           // LocalDateTime

        // 5. Escribir el JSON en la respuesta final
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }

}
