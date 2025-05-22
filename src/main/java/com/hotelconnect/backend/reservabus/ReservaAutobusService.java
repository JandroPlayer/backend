package com.hotelconnect.backend.reservabus;

import com.hotelconnect.backend.logica.Logica;
import com.hotelconnect.backend.users.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaAutobusService {

    private final ReservaAutobusRepository reservaAutobusRepository;
    private final UserRepository userRepository;
    private Logica logica;

    @Autowired
    public ReservaAutobusService(ReservaAutobusRepository reservaAutobusRepository, UserRepository userRepository) {
        this.reservaAutobusRepository = reservaAutobusRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        logica = new Logica(userRepository);
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
    public List<ReservaAutobus> getReservasByUser(Integer user) {
        return reservaAutobusRepository.findByUserId(user);  // Puedes personalizar la consulta si lo necesitas
    }

    // Eliminar una reserva de autobús por su ID y reembolsar
    @Transactional
    public String deleteReservaAutobus(Long reservaId) {
        return logica.deleteReservaConRespuesta(
                reservaId,
                reservaAutobusRepository::findById,
                reservaAutobusRepository::deleteById
        );
    }
}
