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
@Table(name = "autobusos")
public class Autobusos extends VehiclesElectrics {
    @Column(name = "capacitatpassatgers")
    private int capacitatPassatgers;
    @Column(name = "numparadesassignades")
    private int numParadesAssignades;
}

