package com.hotelconnect.backend.activitats;

import com.hotelconnect.backend.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios del controlador ActivitatController utilizando WebMvcTest.
 */
@SuppressWarnings("removal")
@WebMvcTest(ActivitatController.class)
@Import(SecurityConfig.class)
class ActivitatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivitatRepository activitatRepo;

    @MockBean
    private ActivitatService activitatService;

    /**
     * Test del endpoint GET /api/activitats que retorna totes les activitats.
     */
    @Test
    @DisplayName("GET /api/activitats - retorna llista de totes les activitats")
    void testGetAllActivitats() throws Exception {
        Activitat act1 = new Activitat(1L, "Excursió", "Natura", "Excursió a la muntanya", 39.5, 2.9, null, Collections.emptyList());
        Activitat act2 = new Activitat(2L, "Tast de vins", "Gastronomia", "Visita a un celler", 39.7, 3.0, null, Collections.emptyList());

        Mockito.when(activitatRepo.findAll()).thenReturn(Arrays.asList(act1, act2));

        mockMvc.perform(get("/api/activitats")
                        .with(httpBasic("testuser", "testpass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nom", is("Excursió")))
                .andExpect(jsonPath("$[1].tipus", is("Gastronomia")));
    }

    ActivitatAmbDistancia act = new ActivitatAmbDistancia() {
        public Long getId() { return 1L; }
        public String getNom() { return "Museu"; }
        public String getTipus() { return "Cultura"; }
        public String getDescripcio() { return "Museu d'història local"; }
        public Double getLat_activitat() { return 39.5; }
        public Double getLng_activitat() { return 2.9; }
        public Double getDistance() { return 1.2; }
    };

    /**
     * Test del endpoint GET /api/activitats/per-hotel amb activitats dins d’un radi.
     */
    @Test
    @DisplayName("GET /api/activitats/per-hotel - retorna activitats properes")
    void testGetByHotelLocation_WithResults() throws Exception {

        Mockito.when(activitatRepo.findByLocationWithinRadiusWithLogging(39.7, 2.9, 10))
                .thenReturn(Collections.singletonList(act));


        mockMvc.perform(get("/api/activitats/per-hotel")
                        .with(httpBasic("testuser", "testpass"))
                        .param("lat", "39.7")
                        .param("lng", "2.9")
                        .param("radiusKm", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom", is("Museu")))
                .andExpect(jsonPath("$[0].distance", is(1.2)));
    }

    /**
     * Test del endpoint GET /api/activitats/per-hotel quan no hi ha activitats dins del radi.
     */
    @Test
    @DisplayName("GET /api/activitats/per-hotel - sense activitats properes")
    void testGetByHotelLocation_Empty() throws Exception {
        Mockito.when(activitatRepo.findByLocationWithinRadiusWithLogging(40.0, 3.0, 5))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/activitats/per-hotel")
                        .with(httpBasic("testuser", "testpass"))
                        .param("lat", "40.0")
                        .param("lng", "3.0")
                        .param("radiusKm", "5"))
                .andExpect(status().isNoContent());
    }

    /**
     * Test del endpoint GET /api/activitats/{nom}/{lat}/{lng} amb resultat.
     */
    @Test
    @DisplayName("GET /api/activitats/{nom}/{lat}/{lng} - retorna activitat concreta")
    void testGetActivitatByNomAndLocation_Found() throws Exception {
        Activitat act = new Activitat(3L, "Caminada", "Esport", "Ruta saludable", 39.8, 3.1, null, Collections.emptyList());

        Mockito.when(activitatService.findByNomAndLatLng("Caminada", 39.8, 3.1))
                .thenReturn(Optional.of(act));

        mockMvc.perform(get("/api/activitats/Caminada/39.8/3.1")
                        .with(httpBasic("testuser", "testpass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("Caminada")))
                .andExpect(jsonPath("$.tipus", is("Esport")));
    }

    /**
     * Test del endpoint GET /api/activitats/{nom}/{lat}/{lng} quan no es troba cap activitat.
     */
    @Test
    @DisplayName("GET /api/activitats/{nom}/{lat}/{lng} - no trobada")
    void testGetActivitatByNomAndLocation_NotFound() throws Exception {
        Mockito.when(activitatService.findByNomAndLatLng("Inexistent", 0.0, 0.0))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/activitats/Inexistent/0.0/0.0")
                        .with(httpBasic("testuser", "testpass")))
                .andExpect(status().isNotFound());
    }
}
