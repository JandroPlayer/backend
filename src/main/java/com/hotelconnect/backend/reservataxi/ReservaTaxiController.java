package com.hotelconnect.backend.reservataxi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas-taxis")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReservaTaxiController {

    private final ReservaTaxiService reservaTaxiService;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (reservaTaxiService.findById(id).isPresent()) {
            reservaTaxiService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/user/{userId}/taxi/{taxiId}")
    public ResponseEntity<ReservaTaxi> reservarTaxi(
            @PathVariable Long userId,
            @PathVariable Long taxiId,
            @RequestBody ReservaTaxi reservaRequest) {

        ReservaTaxi reserva = reservaTaxiService.crearReserva(userId, taxiId, reservaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }
}

