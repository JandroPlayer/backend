package com.hotelconnect.backend.reservabus;

import com.hotelconnect.backend.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaAutobusService {

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

    // Eliminar una reserva de autobús por su ID
    public void deleteReserva(Long id) {
        reservaAutobusRepository.deleteById(id);
    }
}
