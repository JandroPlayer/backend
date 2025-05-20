package com.hotelconnect.backend.reservataxi;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaTaxiRepository extends JpaRepository<ReservaTaxi, Long> {
    List<ReservaTaxi> findByUserId(Integer user_id);
}

