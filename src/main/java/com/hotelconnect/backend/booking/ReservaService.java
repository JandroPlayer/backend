package com.hotelconnect.backend.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
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

    public List<Reserva> getReservasByHotel(Long hotelId) {
        return reservaRepository.findByHotelId(hotelId);
    }

    public List<Reserva> getReservasByUser(Integer userId) {
        return reservaRepository.findByUserId((userId));
    }
}
