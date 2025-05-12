package com.hotelconnect.backend.booking;

import com.hotelconnect.backend.hotels.Hotel;
import com.hotelconnect.backend.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.Date;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Entity
    @Table(name = "reservas")
    public class Reserva {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // Relación con la entidad Hotel
        @ManyToOne(fetch = FetchType.LAZY)  // Many reservas can belong to one hotel
        @JoinColumn(name = "hotel_id", referencedColumnName = "id", nullable = false)
        private Hotel hotel;  // Relacionamos la reserva con la entidad Hotel

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
        private User user;

        private Date startDate;
        private Date endDate;

        @Column(nullable = false)
        private int adults;

        @Column(nullable = false)
        private int children;

        @Column(nullable = false)
        private int rooms;

        @CreationTimestamp
        @Column(name = "created_at", updatable = false)
        private Date createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private Date updatedAt;

    }
