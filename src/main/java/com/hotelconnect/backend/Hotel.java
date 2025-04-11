package com.hotelconnect.backend;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "agroturismo_hoteles")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placeId;
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private Double rating;
    private String phone;
    private String website;
    private String googleMapsUrl;
    @Column(length = 1000)
    private String imageUrl;
}

