package com.hotelconnect.backend.activitats;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST per a la gestió de les activitats.
 * Permet consultar activitats disponibles o properes a una ubicació concreta.
 */
@RestController
@RequestMapping("/api/activitats")
@CrossOrigin(origins = "*")
public class ActivitatController {

    @Autowired
    private ActivitatRepository activitatRepo;

    @Autowired
    private ActivitatService activitatService;

    /**
     * Obté totes les activitats disponibles a la base de dades.
     *
     * @return una llista de totes les activitats {@link Activitat}.
     */
    @Operation(summary = "Obté totes les activitats disponibles")
    @ApiResponse(responseCode = "200", description = "Llista d'activitats obtinguda correctament")
    @GetMapping
    public List<Activitat> getAll() {
        return activitatRepo.findAll();
    }

    /**
     * Obté activitats dins un radi determinat des de la ubicació d’un hotel (lat/lng).
     *
     * @param lat      latitud de l'hotel
     * @param lng      longitud de l'hotel
     * @param radiusKm radi en quilòmetres (per defecte 10 km)
     * @return una llista d'activitats properes amb distància, o 204 si no es troben activitats
     */
    @Operation(summary = "Obté activitats properes a una ubicació")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activitats trobades correctament"),
            @ApiResponse(responseCode = "204", description = "No s'han trobat activitats properes")
    })
    @GetMapping("/per-hotel")
    public ResponseEntity<List<ActivitatAmbDistancia>> getByHotelLocation(
            @Parameter(description = "Latitud de l'hotel") @RequestParam double lat,
            @Parameter(description = "Longitud de l'hotel") @RequestParam double lng,
            @Parameter(description = "Radi en quilòmetres") @RequestParam(defaultValue = "10") double radiusKm) {

        List<ActivitatAmbDistancia> activitats = activitatRepo.findByLocationWithinRadiusWithLogging(lat, lng, radiusKm);

        if (activitats.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(activitats);
        }

        return ResponseEntity.ok(activitats);
    }

    /**
     * Cerca una activitat pel seu nom i coordenades exactes.
     *
     * @param nom nom de l'activitat
     * @param lat latitud de l'activitat
     * @param lng longitud de l'activitat
     * @return l'activitat trobada, o codi 404 si no existeix
     */
    @Operation(summary = "Obté una activitat pel seu nom i ubicació exacta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activitat trobada"),
            @ApiResponse(responseCode = "404", description = "Activitat no trobada")
    })
    @GetMapping("/{nom}/{lat}/{lng}")
    public ResponseEntity<Activitat> getActivitatByNomAndLocation(
            @Parameter(description = "Nom de l'activitat") @PathVariable String nom,
            @Parameter(description = "Latitud exacta de l'activitat") @PathVariable double lat,
            @Parameter(description = "Longitud exacta de l'activitat") @PathVariable double lng) {

        Optional<Activitat> activitatOpt = activitatService.findByNomAndLatLng(nom, lat, lng);
        return activitatOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
