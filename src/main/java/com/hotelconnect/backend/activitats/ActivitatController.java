package com.hotelconnect.backend.activitats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/activitats")
@CrossOrigin(origins = "*")
public class ActivitatController {

    @Autowired
    private ActivitatRepository activitatRepo;
    @Autowired
    private ActivitatService activitatService;

    // Obtener todas las actividades
    @GetMapping
    public List<Activitat> getAll() {
        return activitatRepo.findAll();
    }

    @GetMapping("/per-hotel")
    public ResponseEntity<List<ActivitatAmbDistancia>> getByHotelLocation(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10") double radiusKm) {

        System.out.println("Latitud recibida: " + lat);  // Verifica los parámetros recibidos
        System.out.println("Longitud recibida: " + lng); // Verifica los parámetros recibidos
        System.out.println("Radio recibido: " + radiusKm); // Verifica el radio recibido

        // Usamos el metodo con depuración
        List<ActivitatAmbDistancia> activitats = activitatRepo.findByLocationWithinRadiusWithLogging(lat, lng, radiusKm);

        // Depuración: Verifica el número de actividades recibidas
        System.out.println("Actividades encontradas: " + activitats.size());

        // Si no se encuentran actividades cercanas, devolver una respuesta vacía o un mensaje adecuado
        if (activitats.isEmpty()) {
            System.out.println("No se encontraron actividades cercanas.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(activitats); // Puedes devolver un mensaje o una lista vacía
        }

        return ResponseEntity.ok(activitats);
    }

    // Obtener actividad por nombre y coordenadas (Lat/Lng)
    @GetMapping("/{nom}/{lat}/{lng}")
    public ResponseEntity<Activitat> getActivitatByNomAndLocation(
            @PathVariable String nom,
            @PathVariable double lat,
            @PathVariable double lng) {

        Optional<Activitat> activitatOpt = activitatService.findByNomAndLatLng(nom, lat, lng);

        // Si no se encuentra la actividad
        // Devolver 404 si no se encuentra
        return activitatOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .build());

    }
}
