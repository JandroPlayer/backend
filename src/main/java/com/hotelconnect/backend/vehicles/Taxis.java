package com.hotelconnect.backend.vehicles;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "taxis")
public class Taxis extends VehiclesElectrics {
    @Column(name = "numllicencia")
    private String numLlicencia;
    @Column(name = "conductorassignat")
    private String conductorAssignat;

    // Getters y setters
}

