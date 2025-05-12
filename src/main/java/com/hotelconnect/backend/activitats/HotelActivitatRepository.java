package com.hotelconnect.backend.activitats;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelActivitatRepository extends JpaRepository<HotelActivitat, Long> {

    boolean existsByHotelIdAndActivitatId(Long hotelId, Long activitatId);
}
