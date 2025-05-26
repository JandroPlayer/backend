package com.hotelconnect.backend.reserves;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelconnect.backend.activitats.ActivitatRepository;
import com.hotelconnect.backend.activitats.ActivitatService;
import com.hotelconnect.backend.activitats.HotelActivitatRepository;
import com.hotelconnect.backend.booking.*;
import com.hotelconnect.backend.config.SecurityConfig;
import com.hotelconnect.backend.hotels.Hotel;
import com.hotelconnect.backend.hotels.HotelRepository;
import com.hotelconnect.backend.logica.Logica;
import com.hotelconnect.backend.setup.TestSetup;
import com.hotelconnect.backend.users.User;
import com.hotelconnect.backend.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("removal")
@WebMvcTest(ReservaController.class)
@Import(SecurityConfig.class)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservaService reservaService;

    @MockBean
    private HotelRepository hotelRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ReservaRepository reservaRepository;

    @MockBean
    private Logica logica;

    @MockBean
    private ActivitatService activitatService;

    @MockBean
    private ActivitatRepository activitatRepo;

    @MockBean
    private HotelActivitatRepository hotelActivitatRepo;

    private Reserva reserva;
    private Hotel hotel;
    private User user;

    @BeforeEach
    void init() {
        user = TestSetup.createTestUser();
        hotel = TestSetup.createTestHotel();
        reserva = TestSetup.createTestReserva(user, hotel);
    }

    @Test
    void testCrearReserva() throws Exception {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(reservaService.crearReserva(any(Reserva.class))).thenReturn(reserva);

        mockMvc.perform(post("/api/reservas")
                        .with(httpBasic("testuser", "testpass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reserva)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testObtenerReservas() throws Exception {
        List<Reserva> reservas = List.of(reserva);
        when(reservaService.obtenerReservas()).thenReturn(reservas);

        mockMvc.perform(get("/api/reservas")
                        .with(httpBasic("testuser", "testpass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testObtenerReservaPorId_Existe() throws Exception {
        when(reservaService.obtenerReservaPorId(1L)).thenReturn(reserva);

        mockMvc.perform(get("/api/reservas/1")
                .with(httpBasic("testuser", "testpass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testObtenerReservaPorId_NoExiste() throws Exception {
        when(reservaService.obtenerReservaPorId(1L)).thenReturn(null);

        mockMvc.perform(get("/api/reservas/1")
                .with(httpBasic("testuser", "testpass")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetReservasByHotel() throws Exception {
        when(reservaService.getReservasByHotel(1L)).thenReturn(List.of(reserva));

        mockMvc.perform(get("/api/reservas/hotel/1")
                .with(httpBasic("testuser", "testpass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testObtenerReservasPorUsuario() throws Exception {
        when(reservaService.getReservasByUser(1)).thenReturn(List.of(reserva));

        mockMvc.perform(get("/api/reservas/usuario/1")
                .with(httpBasic("testuser", "testpass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testPagarReserva_OK() throws Exception {
        reserva.setPagada(true);
        when(logica.pagarReserva(reservaRepository, 1L)).thenReturn(reserva);

        mockMvc.perform(put("/api/reservas/1/pagar")
                .with(httpBasic("testuser", "testpass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagada").value(true));
    }

    @Test
    void testPagarReserva_BadRequest() throws Exception {
        when(logica.pagarReserva(reservaRepository, 1L)).thenThrow(new IllegalStateException("Ja està pagada"));

        mockMvc.perform(put("/api/reservas/1/pagar")
                .with(httpBasic("testuser", "testpass")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ja està pagada"));
    }

    @Test
    void testDeleteReserva_OK() throws Exception {
        when(reservaService.deleteReservaHotel(1L)).thenReturn("Reserva eliminada");

        mockMvc.perform(delete("/api/reservas/1")
                .with(httpBasic("testuser", "testpass"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reserva eliminada"));
    }

    @Test
    void testDeleteReserva_NotFound() throws Exception {
        when(reservaService.deleteReservaHotel(1L)).thenThrow(new NoSuchElementException("No trobada"));

        mockMvc.perform(delete("/api/reservas/1")
                .with(httpBasic("testuser", "testpass"))
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No trobada"));
    }
}

