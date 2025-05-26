package com.hotelconnect.backend.activitats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotelconnect.backend.hotels.Hotel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "activitats")
public class Activitat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String tipus;
    private String descripcio;
    private Double lat_activitat;
    private Double lng_activitat;

    @Transient
    private Double distance; // NO es guarda a la BBDD

    @ManyToMany(mappedBy = "activitats")
    @JsonIgnore
    private List<Hotel> hotels;
}
