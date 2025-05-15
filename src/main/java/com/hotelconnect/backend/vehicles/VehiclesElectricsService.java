package com.hotelconnect.backend.vehicles;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehiclesElectricsService {
    private final VehiclesElectricsRepository repository;

    public VehiclesElectricsService(VehiclesElectricsRepository repository) {
        this.repository = repository;
    }

    public List<VehiclesElectrics> findAll() {
        return repository.findAll();
    }

    public VehiclesElectrics save(VehiclesElectrics vehicle) {
        return repository.save(vehicle);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public VehiclesElectrics findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    // 🔽 Mètode per filtrar només autobusos
    public List<Autobusos> findAllAutobusos() {
        return repository.findAll().stream()
                .filter(v -> v instanceof Autobusos)
                .map(v -> (Autobusos) v)
                .collect(Collectors.toList());
    }

    // 🔽 Mètode per filtrar només autobusos
    public List<Taxis> findAllTaxis() {
        return repository.findAll().stream()
                .filter(v -> v instanceof Taxis)
                .map(v -> (Taxis) v)
                .collect(Collectors.toList());
    }
}

