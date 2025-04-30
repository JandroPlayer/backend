package com.hotelconnect.backend.reservabus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaAutobusRepository extends JpaRepository<ReservaAutobus, Long> {
    // Aquí puedes añadir métodos personalizados si es necesario, como buscar por usuario o autobús
    // Por ejemplo:
    // List<ReservaAutobus> findByUserId(Long userId);
}

