package com.hotelconnect.backend.reservataxi;

import com.hotelconnect.backend.users.User;
import com.hotelconnect.backend.users.UserRepository;
import com.hotelconnect.backend.vehicles.Taxis;
import com.hotelconnect.backend.vehicles.TaxisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservaTaxiService {

    private final ReservaTaxiRepository reservaTaxiRepository;
    private final TaxisRepository taxisRepository;
    private final UserRepository userRepository;

    public List<ReservaTaxi> findAll() {
        return reservaTaxiRepository.findAll();
    }

    public Optional<ReservaTaxi> findById(Long id) {
        return reservaTaxiRepository.findById(id);
    }

    // Obtener todas las reservas de un usuario específico
    public List<ReservaTaxi> getReservasByUser(Integer user) {
        return reservaTaxiRepository.findByUserId(user);  // Puedes personalizar la consulta si lo necesitas
    }

    public ReservaTaxi save(ReservaTaxi reservaTaxi) {
        return reservaTaxiRepository.save(reservaTaxi);
    }

    public void deleteById(Long id) {
        reservaTaxiRepository.deleteById(id);
    }

    public ReservaTaxi crearReserva(Long userId, Long taxiId, ReservaTaxi reservaRequest) {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("Usuari no trobat"));

        Taxis taxi = taxisRepository.findById(taxiId)
                .orElseThrow(() -> new RuntimeException("Taxi no trobat"));

        double preu = taxi.getTarifaBase() + (taxi.getCostPerKm() * reservaRequest.getDistanciaKm());
        reservaRequest.setUser(user);
        reservaRequest.setTaxi(taxi);
        reservaRequest.setPreu(preu);
        reservaRequest.setPagada(false); // o true si s'implementa pagament

        return reservaTaxiRepository.save(reservaRequest);
    }
}

