package com.hotelconnect.backend.logica;

import com.hotelconnect.backend.users.User;
import com.hotelconnect.backend.users.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class Logica {

    private final UserRepository userRepository;

    @Autowired
    public Logica(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public <T extends Reservable> String deleteReservaConRespuesta(
            Long reservaId,
            Function<Long, Optional<T>> findByIdFunction,
            Consumer<Long> deleteByIdConsumer
    ) {
        T reserva = findByIdFunction.apply(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no trobada"));

        User user = reserva.getUser();

        if (reserva.isPagada()) {
            BigDecimal importe = BigDecimal.valueOf(reserva.getPreu());
            user.setSaldo(user.getSaldo().add(importe));
            userRepository.save(user);
            deleteByIdConsumer.accept(reservaId);
            return "Reserva cancel·lada i import reemborsat: " + importe + "€";
        } else {
            deleteByIdConsumer.accept(reservaId);
            return "Reserva cancel·lada (no estava pagada)";
        }
    }

    @Transactional
    public <T extends Reservable, R extends JpaRepository<T, Long>> T pagarReserva(R repo, Long reservaId) {
        T reserva = repo.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no trobada"));

        if (reserva.isPagada()) {
            throw new IllegalStateException("Reserva ja està pagada");
        }

        reserva.setPagada(true);
        return repo.save(reserva);
    }
}
