package com.hotelconnect.backend.vehicles;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/vehicles")
public class VehiclesElectricsController {

    private final VehiclesElectricsService service;

    public VehiclesElectricsController(VehiclesElectricsService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehiclesElectrics> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public VehiclesElectrics getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public VehiclesElectrics create(@RequestBody VehiclesElectrics vehicle) {
        return service.save(vehicle);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/autobusos")
    public List<Autobusos> getAutobusos() {
        return service.findAllAutobusos(); // implementa-ho al servei
    }

}

