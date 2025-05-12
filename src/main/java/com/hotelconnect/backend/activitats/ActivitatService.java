package com.hotelconnect.backend.activitats;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActivitatService {

    private final ActivitatRepository activitatRepository;

    @Autowired
    public ActivitatService(ActivitatRepository activitatRepository) {
        this.activitatRepository = activitatRepository;
    }

    public Optional<Activitat> findByNomAndLatLng(String nom, double lat_activitat, double lng_activitat) {
        return activitatRepository.findActivitatByNomAndLocation(nom, lat_activitat, lng_activitat);
    }
}

