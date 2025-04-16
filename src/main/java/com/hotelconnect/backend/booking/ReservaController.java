package com.hotelconnect.backend.booking;

import com.hotelconnect.backend.Hotel;
import com.hotelconnect.backend.HotelRepository;
import com.hotelconnect.backend.users.User;
import com.hotelconnect.backend.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    @Autowired
    private HotelRepository hotelRepository;

    private final ReservaService reservaService;

    @Autowired
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
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
}

