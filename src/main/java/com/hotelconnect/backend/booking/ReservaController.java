package com.hotelconnect.backend.booking;

import com.hotelconnect.backend.activitats.*;
import com.hotelconnect.backend.hotels.Hotel;
import com.hotelconnect.backend.hotels.HotelRepository;
import com.hotelconnect.backend.logica.Logica;
import com.hotelconnect.backend.users.User;
import com.hotelconnect.backend.users.UserRepository;
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

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
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
    public ReservaController(ReservaService reservaService, Logica logica) {
        this.reservaService = reservaService;
        this.logica = logica;
    }

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public Reserva createReserva(@RequestBody Reserva reserva) {
        // Buscar el hotel por ID
        if (reserva.getHotel() != null && reserva.getHotel().getId() != null) {
            Hotel hotel = hotelRepository.findById(reserva.getHotel().getId())
                    .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));
            reserva.setHotel(hotel);
        } else {
            throw new RuntimeException("El hotel no puede ser nulo");
        }

        // Buscar el usuario por ID
        if (reserva.getUser() != null && reserva.getUser().getId() != null) {
            User user = userRepository.findById(reserva.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            reserva.setUser(user);
        } else {
            throw new RuntimeException("El usuario no puede ser nulo");
        }

        System.out.println("Reserva recibida: " + reserva);
        return reservaService.crearReserva(reserva);
    }

    // Obtener todas las reservas
    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerReservas() {
        List<Reserva> reservas = reservaService.obtenerReservas();
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // Obtener una reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerReservaPorId(@PathVariable Long id) {
        Reserva reserva = reservaService.obtenerReservaPorId(id);
        if (reserva != null) {
            return new ResponseEntity<>(reserva, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/hotel/{hotelId}")
    public List<Reserva> getReservasByHotel(@PathVariable Long hotelId) {
        return reservaService.getReservasByHotel(hotelId);
    }

    // 🔥 Nuevo endpoint: reservas por usuario
    @GetMapping("/usuario/{userId}")
    public List<Reserva> obtenerReservasPorUsuario(@PathVariable Integer userId) {
        return reservaService.getReservasByUser(userId);
    }

    // Activitats
    @GetMapping("/{id}/activitats/load")
    public String carregarActivitats(@PathVariable Long id) {
        // Obtener la reserva
        Reserva reserva = reservaService.obtenerReservaPorId(id);

        // Verificar que se encontró la reserva y obtener el hotel asociado
        Hotel hotel = reserva.getHotel();
        if (hotel == null) {
            return "Error: No se ha encontrado el hotel asociado a la reserva.";
        }

        double lat = hotel.getLat();
        double lng = hotel.getLng();
        int radius = 15000; // Radio en metros

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

                    // Verificar si ya existe una actividad con el mismo nombre y coordenadas
                    Activitat activitat = activitatService.findByNomAndLatLng(nom, latAct, lngAct)
                            .orElseGet(() -> {
                                // Crear nueva actividad si no existe
                                Activitat nueva = new Activitat();
                                nueva.setNom(nom);
                                nueva.setTipus(keyword);
                                nueva.setLat_activitat(latAct);
                                nueva.setLng_activitat(lngAct);
                                nueva.setDescripcio((String) place.get("vicinity"));
                                return activitatRepo.save(nueva);
                            });

                    // Verificar que el hotel y la actividad tienen IDs válidos antes de insertar la relación
                    if (hotel.getId() != null && activitat.getId() != null) {
                        // Comprobar si la relación ya existe antes de guardarla
                        boolean exists = hotelActivitatRepo.existsByHotelIdAndActivitatId(hotel.getId(), activitat.getId());
                        if (!exists) {
                            HotelActivitat relacion = new HotelActivitat();
                            relacion.setHotelId(hotel.getId());
                            relacion.setActivitatId(activitat.getId());
                            hotelActivitatRepo.save(relacion);
                            System.out.println("Relación insertada: Hotel ID = " + hotel.getId() + ", Actividad ID = " + activitat.getId());
                        }
                    } else {
                        System.out.println("Error: El hotel o la actividad no tienen ID válido.");
                    }
                });

            } catch (Exception e) {
                System.out.println("Error obteniendo actividades para keyword '" + keyword + "': " + e.getMessage());
            }
        }

        return "Activitats carregades per a l'hotel: " + hotel.getName();
    }

    @GetMapping("/{id}/activitats")
    public ResponseEntity<List<Activitat>> obtenirActivitatsReserva(@PathVariable Long id) {
        List<Activitat> activitats = reservaService.getActivitatsByReservaId(id);
        return ResponseEntity.ok(activitats);
    }

    @PutMapping("/{reservaId}/pagar")
    public ResponseEntity<?> pagarReserva(@PathVariable Long reservaId) {
        try {
            var reserva = logica.pagarReserva(reservaRepository, reservaId);
            return ResponseEntity.ok(reserva);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReserva(@PathVariable Long id) {
        String mensaje = reservaService.deleteReservaHotel(id);
        return ResponseEntity.ok(Map.of("message", mensaje));
    }

}

