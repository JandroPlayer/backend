package com.hotelconnect.backend.booking;

import com.hotelconnect.backend.activitats.Activitat;
import com.hotelconnect.backend.activitats.ActivitatRepository;
import com.hotelconnect.backend.hotels.Hotel;
import com.hotelconnect.backend.hotels.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private HotelRepository hotelRepo;

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

    // Activitats
    public List<Activitat> getActivitatsByReservaId(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no trobada"));

        Long hotelId = reserva.getHotel().getId();

        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel no trobat"));

        return hotel.getActivitats(); // assuming has @ManyToMany
    }

}
