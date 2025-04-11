package com.hotelconnect.backend.booking;

import com.hotelconnect.backend.Hotel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Date startDate;
    private Date endDate;

    @Column(nullable = false)
    private int adults;

    @Column(nullable = false)
    private int children;

    @Column(nullable = false)
    private int rooms;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
}
