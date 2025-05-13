package com.hotelconnect.backend.reservabus;

import com.hotelconnect.backend.users.User;
import com.hotelconnect.backend.users.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaAutobusService {

    @Autowired
    private UserRepository userRepository;

    private final ReservaAutobusRepository reservaAutobusRepository;

    @Autowired
    public ReservaAutobusService(ReservaAutobusRepository reservaAutobusRepository) {
        this.reservaAutobusRepository = reservaAutobusRepository;
    }

    // Crear una nueva reserva de autobús
    public ReservaAutobus createReserva(ReservaAutobus reservaAutobus) {
        return reservaAutobusRepository.save(reservaAutobus);
    }

    // Obtener todas las reservas de autobuses
    public List<ReservaAutobus> getAllReservas() {
        return reservaAutobusRepository.findAll();
    }

    // Obtener una reserva de autobús por su ID
    public Optional<ReservaAutobus> getReservaById(Long id) {
        return reservaAutobusRepository.findById(id);
    }

    // Obtener todas las reservas de un usuario específico
    public List<ReservaAutobus> getReservasByUser(User user) {
        return reservaAutobusRepository.findAll();  // Puedes personalizar la consulta si lo necesitas
    }

    // Eliminar una reserva de autobús por su ID y reembolsar
    @Transactional
    public String deleteReservaConRespuesta(Long reservaId) {
        ReservaAutobus reserva = reservaAutobusRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no trobada"));

        User user = reserva.getUser();

        if (reserva.isPagada()) {
            BigDecimal importe = BigDecimal.valueOf(reserva.getPreu());
            user.setSaldo(user.getSaldo().add(importe));
            userRepository.save(user);
            reservaAutobusRepository.deleteById(reservaId);
            return "Reserva cancel·lada i import reemborsat: " + importe + "€";
        } else {
            reservaAutobusRepository.deleteById(reservaId);
            return "Reserva cancel·lada (no estava pagada)";
        }
    }

}
