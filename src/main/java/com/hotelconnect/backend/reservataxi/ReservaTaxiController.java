package com.hotelconnect.backend.reservataxi;

import com.hotelconnect.backend.logica.Logica;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas-taxis")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReservaTaxiController {

    private final ReservaTaxiService reservaTaxiService;
    private final ReservaTaxiRepository reservaTaxiRepository;
    private final Logica logica;

    @GetMapping
    public List<ReservaTaxi> getAll() {
        return reservaTaxiService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaTaxi> getById(@PathVariable Long id) {
        return reservaTaxiService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener todas las reservas de un usuario
    @GetMapping("/user/{userId}")
    public List<ReservaTaxi> obtenerReservasPorUsuario(@PathVariable Integer userId) {
        return reservaTaxiService.getReservasByUser(userId);
    }

    @PostMapping
    public ReservaTaxi create(@RequestBody ReservaTaxi reservaTaxi) {
        return reservaTaxiService.save(reservaTaxi);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaTaxi> updateReservaTaxi(@PathVariable Long id, @RequestBody ReservaTaxi reservaTaxi) {
        try {
            ReservaTaxi updated = reservaTaxiService.updateReservaTaxi(id, reservaTaxi);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Eliminar una reserva de autobús por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReserva(@PathVariable Long id) {
        String mensaje = reservaTaxiService.deleteReservaTaxi(id);
        return ResponseEntity.ok(Map.of("message", mensaje));
    }

    @PostMapping("/user/{userId}/taxi/{taxiId}")
    public ResponseEntity<ReservaTaxi> reservarTaxi(
            @PathVariable Long userId,
            @PathVariable Long taxiId,
            @RequestBody ReservaTaxi reservaRequest) {

        ReservaTaxi reserva = reservaTaxiService.crearReserva(userId, taxiId, reservaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @PutMapping("/{reservaId}/pagar")
    public ResponseEntity<?> pagarReserva(@PathVariable Long reservaId) {
        try {
            var reserva = logica.pagarReserva(reservaTaxiRepository, reservaId);
            return ResponseEntity.ok(reserva);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

