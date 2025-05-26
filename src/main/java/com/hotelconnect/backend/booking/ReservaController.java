package com.hotelconnect.backend.booking;

import com.hotelconnect.backend.activitats.*;
import com.hotelconnect.backend.hotels.Hotel;
import com.hotelconnect.backend.hotels.HotelRepository;
import com.hotelconnect.backend.logica.Logica;
import com.hotelconnect.backend.users.User;
import com.hotelconnect.backend.users.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Controlador REST per gestionar les reserves d'hotels.
 * Proporciona operacions per crear, obtenir, pagar i eliminar reserves,
 * així com per carregar activitats relacionades amb una reserva.
 */
@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
@Tag(name = "Reserves", description = "API per gestionar les reserves d'hotels")
public class ReservaController {

    @Autowired
    private HotelRepository hotelRepository;

    private final ReservaService reservaService;

    @Autowired
    private ReservaRepository reservaRepository;

    private final Logica logica;

    @Autowired
    private ActivitatRepository activitatRepo;

    @Autowired
    private HotelActivitatRepository hotelActivitatRepo;

    @Value("${google.api.key}")
    private String apiKey;

    @Autowired
    private ActivitatService activitatService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public ReservaController(ReservaService reservaService, Logica logica) {
        this.reservaService = reservaService;
        this.logica = logica;
    }

    /**
     * Crea una nova reserva.
     *
     * @param reserva La reserva a crear amb hotel i usuari especificats.
     * @return La reserva creada.
     * @throws RuntimeException Si no es troba l'hotel o l'usuari indicats.
     */
    @Operation(summary = "Crear reserva", description = "Crea una nova reserva per un usuari i hotel donats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva creada correctament",
                    content = @Content(schema = @Schema(implementation = Reserva.class))),
            @ApiResponse(responseCode = "400", description = "Error en dades d'entrada")
    })
    @PostMapping
    public Reserva createReserva(@RequestBody Reserva reserva) {
        if (reserva.getHotel() != null && reserva.getHotel().getId() != null) {
            Hotel hotel = hotelRepository.findById(reserva.getHotel().getId())
                    .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));
            reserva.setHotel(hotel);
        } else {
            throw new RuntimeException("El hotel no puede ser nulo");
        }

        if (reserva.getUser() != null && reserva.getUser().getId() != null) {
            User user = userRepository.findById(reserva.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            reserva.setUser(user);
        } else {
            throw new RuntimeException("El usuario no puede ser nulo");
        }

        return reservaService.crearReserva(reserva);
    }

    /**
     * Obté totes les reserves.
     *
     * @return Llista amb totes les reserves.
     */
    @Operation(summary = "Obtenir totes les reserves")
    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerReservas() {
        List<Reserva> reservas = reservaService.obtenerReservas();
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    /**
     * Obté una reserva per ID.
     *
     * @param id ID de la reserva.
     * @return Reserva trobada o NOT FOUND si no existeix.
     */
    @Operation(summary = "Obtenir reserva per ID")
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerReservaPorId(@PathVariable Long id) {
        Reserva reserva = reservaService.obtenerReservaPorId(id);
        if (reserva != null) {
            return new ResponseEntity<>(reserva, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Obté les reserves associades a un hotel.
     *
     * @param hotelId ID de l'hotel.
     * @return Llista de reserves de l'hotel.
     */
    @Operation(summary = "Obtenir reserves per hotel")
    @GetMapping("/hotel/{hotelId}")
    public List<Reserva> getReservasByHotel(@PathVariable Long hotelId) {
        return reservaService.getReservasByHotel(hotelId);
    }

    /**
     * Obté les reserves d'un usuari.
     *
     * @param userId ID de l'usuari.
     * @return Llista de reserves de l'usuari.
     */
    @Operation(summary = "Obtenir reserves per usuari")
    @GetMapping("/usuario/{userId}")
    public List<Reserva> obtenerReservasPorUsuario(@PathVariable Integer userId) {
        return reservaService.getReservasByUser(userId);
    }

    /**
     * Carrega activitats properes a l'hotel d'una reserva i les desa a la base de dades.
     *
     * @param id ID de la reserva.
     * @return Missatge d'èxit o error.
     */
    @Operation(summary = "Carregar activitats per reserva")
    @GetMapping("/{id}/activitats/load")
    public String carregarActivitats(@PathVariable Long id) {
        Reserva reserva = reservaService.obtenerReservaPorId(id);
        Hotel hotel = reserva.getHotel();
        if (hotel == null) {
            return "Error: No se ha encontrado el hotel asociado a la reserva.";
        }

        double lat = hotel.getLat();
        double lng = hotel.getLng();
        int radius = 15000;

        List<String> keywords = Arrays.asList(
                "golf",
                "senderismo",
                "restaurantes",
                "enoturismo",
                "cuevas de arta",
                "cuevas",
                "playas",
                "rutas de ciclismo",
                "actividades náuticas",
                "birdwatching",
                "parque natural Es Trenc",
                "parque natural albufera",
                "museos",
                "montañas"
        );

        RestTemplate rest = new RestTemplate();

        for (String keyword : keywords) {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                    .queryParam("location", lat + "," + lng)
                    .queryParam("radius", radius)
                    .queryParam("keyword", keyword)
                    .queryParam("key", apiKey)
                    .toUriString();

            try {
                var response = rest.getForObject(url, Map.class);
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

                if (results == null) continue;

                results.forEach(place -> {
                    Map<String, Object> location = (Map<String, Object>) ((Map<String, Object>) place.get("geometry")).get("location");

                    String nom = (String) place.get("name");
                    double latAct = (Double) location.get("lat");
                    double lngAct = (Double) location.get("lng");

                    Activitat activitat = activitatService.findByNomAndLatLng(nom, latAct, lngAct)
                            .orElseGet(() -> {
                                Activitat nueva = new Activitat();
                                nueva.setNom(nom);
                                nueva.setTipus(keyword);
                                nueva.setLat_activitat(latAct);
                                nueva.setLng_activitat(lngAct);
                                nueva.setDescripcio((String) place.get("vicinity"));
                                return activitatRepo.save(nueva);
                            });

                    if (hotel.getId() != null && activitat.getId() != null) {
                        boolean exists = hotelActivitatRepo.existsByHotelIdAndActivitatId(hotel.getId(), activitat.getId());
                        if (!exists) {
                            HotelActivitat relacion = new HotelActivitat();
                            relacion.setHotelId(hotel.getId());
                            relacion.setActivitatId(activitat.getId());
                            hotelActivitatRepo.save(relacion);
                        }
                    }
                });

            } catch (Exception e) {
                System.out.println("Error obteniendo actividades para keyword '" + keyword + "': " + e.getMessage());
            }
        }

        return "Activitats carregades per a l'hotel: " + hotel.getName();
    }

    /**
     * Obté les activitats associades a una reserva.
     *
     * @param id ID de la reserva.
     * @return Llista d'activitats.
     */
    @Operation(summary = "Obtenir activitats per reserva")
    @GetMapping("/{id}/activitats")
    public ResponseEntity<List<Activitat>> obtenirActivitatsReserva(@PathVariable Long id) {
        List<Activitat> activitats = reservaService.getActivitatsByReservaId(id);
        return ResponseEntity.ok(activitats);
    }

    /**
     * Marca una reserva com a pagada.
     *
     * @param reservaId ID de la reserva.
     * @return Reserva actualitzada o missatge d'error.
     */
    @PutMapping("/{reservaId}/pagar")
    public ResponseEntity<?> pagarReserva(@PathVariable Long reservaId) {
        try {
            var reserva = logica.pagarReserva(reservaRepository, reservaId);
            return ResponseEntity.ok(reserva);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error inesperat en pagar la reserva."));
        }
    }

    /**
     * Elimina una reserva pel seu ID.
     *
     * @param id ID de la reserva.
     * @return Missatge de confirmació.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReserva(@PathVariable Long id) {
        try {
            String missatge = reservaService.deleteReservaHotel(id);
            return ResponseEntity.ok(Map.of("message", missatge));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error inesperat en eliminar la reserva."));
        }
    }

}

