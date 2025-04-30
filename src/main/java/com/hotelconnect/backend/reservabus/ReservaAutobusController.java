package com.hotelconnect.backend.reservabus;

import com.hotelconnect.backend.users.User;
import com.hotelconnect.backend.users.UserRepository;
import com.hotelconnect.backend.vehicles.Autobusos;
import com.hotelconnect.backend.vehicles.AutobusosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas-autobus")
@CrossOrigin(origins = "*")
public class ReservaAutobusController {

    private final ReservaAutobusService reservaAutobusService;

    @Autowired
    private AutobusosRepository autobusosRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public ReservaAutobusController(ReservaAutobusService reservaAutobusService) {
        this.reservaAutobusService = reservaAutobusService;
    }

    // Crear una nueva reserva de autobús
    @PostMapping
    public ReservaAutobus createReserva(@RequestBody ReservaAutobus reservaAutobus) {
        try {
        System.out.println("Reserva recibida: " + reservaAutobus);

        // Buscar Autobús real
        if (reservaAutobus.getAutobus() != null && reservaAutobus.getAutobus().getId() != null) {
            Autobusos autobus = autobusosRepository.findById(reservaAutobus.getAutobus().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Autobús no encontrado"));
            reservaAutobus.setAutobus(autobus);
        } else {
            throw new IllegalArgumentException("El autobús no puede ser nulo");
        }

        // Buscar Usuario real
        if (reservaAutobus.getUser() != null && reservaAutobus.getUser().getId() != null) {
            User user = userRepository.findById(reservaAutobus.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            reservaAutobus.setUser(user);
        } else {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        // Ahora el objeto está totalmente armado
        System.out.println("Reserva lista para guardar: " + reservaAutobus);

        return reservaAutobusService.createReserva(reservaAutobus);
    } catch (Exception e) {
        e.printStackTrace(); // esto imprime el error exacto en consola del servidor
        throw e; // vuelve a lanzar la excepción para que el cliente vea el 500
    }
    }


    // Obtener todas las reservas de autobuses
    @GetMapping
    public List<ReservaAutobus> getAllReservas() {
        return reservaAutobusService.getAllReservas();
    }

    // Obtener una reserva de autobús por ID
    @GetMapping("/{id}")
    public Optional<ReservaAutobus> getReservaById(@PathVariable Long id) {
        return reservaAutobusService.getReservaById(id);
    }

    // Obtener todas las reservas de un usuario
    @GetMapping("/user/{userId}")
    public List<ReservaAutobus> getReservasByUser(@PathVariable Long userId) {
        User user = new User();
        user.setId(Math.toIntExact(userId));  // Establecer el ID del usuario, puedes buscar al usuario en la base de datos si es necesario
        return reservaAutobusService.getReservasByUser(user);
    }

    // Eliminar una reserva de autobús por ID
    @DeleteMapping("/{id}")
    public void deleteReserva(@PathVariable Long id) {
        reservaAutobusService.deleteReserva(id);
    }
}

