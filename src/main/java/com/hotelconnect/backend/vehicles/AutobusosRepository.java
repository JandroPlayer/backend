package com.hotelconnect.backend.vehicles;

import com.hotelconnect.backend.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutobusosRepository extends JpaRepository<Autobusos, Long> {
}

