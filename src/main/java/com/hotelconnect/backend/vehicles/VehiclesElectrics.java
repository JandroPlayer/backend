package com.hotelconnect.backend.vehicles;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "vehicleselectrics")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class VehiclesElectrics {
    @Id
    private Long id;
    private String marca;
    private String model;
    @Column(name = "anyfabricacio")
    private int anyFabricacio;
    @Column(name = "autonomiakm")
    private int autonomiaKm;
    @Column(name = "preu_per_persona")
    private Double preuPerPersona;
    @Column(name = "image_url")
    private String imageUrl;
}

