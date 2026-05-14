package com.booking_1.demo.core.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.booking_1.demo.booking.services.IBookingService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBookingService bookingService; // Mockeamos el servicio para que no interfiera

    @Test
    @DisplayName("Debe retornar 401 si se accede a un endpoint protegido sin autenticación")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER") // Simulamos un usuario con rol USER
    @DisplayName("Debe retornar 403 si un USER intenta acceder a un endpoint de ADMIN")
    void shouldReturn403WhenUserAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simulamos un usuario con rol ADMIN
    @DisplayName("Debe retornar 200 si un ADMIN accede a su endpoint")
    void shouldReturn200WhenAdminAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Debe permitir que un USER vea sus propias reservas")
    void shouldAllowUserToSeeOwnBookings() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/my"))
                .andExpect(status().isOk());
    }
}
