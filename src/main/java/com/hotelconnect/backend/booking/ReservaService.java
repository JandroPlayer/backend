package com.hotelconnect.backend.booking;

import com.hotelconnect.backend.activitats.Activitat;
import com.hotelconnect.backend.hotels.Hotel;
import com.hotelconnect.backend.hotels.HotelRepository;
import com.hotelconnect.backend.logica.Logica;
import com.hotelconnect.backend.users.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private HotelRepository hotelRepository;

    private Logica logica;

    @Autowired
    private final ReservaRepository reservaRepository;
    @Autowired
    private UserRepository userRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @PostConstruct
    public void init() {
        logica = new Logica(userRepository);
    }

    public Reserva crearReserva(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public List<Reserva> obtenerReservas() {
        return reservaRepository.findAll();
    }

    public Reserva obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id).orElse(null);
    }

    // Actualizar ReservaHotel
    public Reserva updateReservaHotel(Long id, Reserva reservaHotel) {
        if (!reservaRepository.existsById(id)) {
            throw new IllegalArgumentException("ReservaHotel no encontrada.");
        }
        reservaHotel.setId(id); // mantener el mismo ID
        return reservaRepository.save(reservaHotel);
    }

    public List<Reserva> getReservasByHotel(Long hotelId) {
        return reservaRepository.findByHotelId(hotelId);
    }

    public List<Reserva> getReservasByUser(Integer userId) {
        return reservaRepository.findByUserId((userId));
    }

    // Activitats
    public List<Activitat> getActivitatsByReservaId(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no trobada"));

        Long hotelId = reserva.getHotel().getId();

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel no trobat"));

        return hotel.getActivitats(); // assuming has @ManyToMany
    }

    @Transactional
    public String deleteReservaHotel(Long reservaId) {
        return logica.deleteReservaConRespuesta(
                reservaId,
                reservaRepository::findById,
                reservaRepository::deleteById
        );
    }
}
