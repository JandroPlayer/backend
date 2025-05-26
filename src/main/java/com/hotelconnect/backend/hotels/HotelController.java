package com.hotelconnect.backend.hotels;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controlador REST per gestionar operacions relacionades amb hotels.
 */
@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "*")
@Tag(name = "Hotels", description = "Operacions CRUD i funcions addicionals sobre hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    /**
     * Obté tots els hotels registrats.
     *
     * @return Llista d'hotels
     */
    @Operation(summary = "Llistar tots els hotels")
    @ApiResponse(responseCode = "200", description = "Llista d'hotels obtinguda correctament")
    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelService.getAllHotels();
    }

    /**
     * Desa un nou hotel.
     *
     * @param hotel Hotel a desar
     * @return L'hotel desat
     */
    @Operation(summary = "Desar un hotel")
    @ApiResponse(responseCode = "200", description = "Hotel desat correctament")
    @PostMapping
    public ResponseEntity<Hotel> saveHotel(@RequestBody Hotel hotel) {
        return ResponseEntity.ok(hotelService.saveHotel(hotel));
    }

    /**
     * Obté un hotel pel seu ID.
     *
     * @param id Identificador de l'hotel
     * @return L'hotel si existeix, o 404 si no
     */
    @Operation(summary = "Obtenir un hotel per ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hotel trobat"),
            @ApiResponse(responseCode = "404", description = "Hotel no trobat")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable Long id) {
        Hotel hotel = hotelService.getHotelById(id);
        if (hotel != null) {
            return ResponseEntity.ok(hotel);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Obté hotels que no tenen activitats associades.
     *
     * @return Llista d'hotels sense activitats
     */
    @Operation(summary = "Obtenir hotels sense activitats")
    @ApiResponse(responseCode = "200", description = "Hotels obtinguts correctament")
    @GetMapping("/without-activities")
    public List<Hotel> getHotelsWithoutActivitats() {
        return hotelService.obtenirTotsElsHotelsSenseActivitats();
    }

    /**
     * Obté un hotel (en format DTO) pel seu ID, sense carregar activitats.
     *
     * @param id Identificador de l'hotel
     * @return HotelDTO amb la informació bàsica
     */
    @Operation(summary = "Obtenir un hotel per ID sense activitats")
    @ApiResponse(responseCode = "200", description = "Hotel obtingut correctament")
    @GetMapping("/{id}/without-activities")
    public ResponseEntity<HotelDTO> getHotelWithoutActivities(@PathVariable Long id) {
        Hotel hotel = hotelService.obtenerHotelSinActividades(id);
        HotelDTO hotelDTO = new HotelDTO(hotel);
        return ResponseEntity.ok(hotelDTO);
    }

    /**
     * Importa hotels des de Google Places.
     *
     * @return Informació sobre els hotels importats
     * @throws Exception Si hi ha un error en la importació
     */
    @Operation(summary = "Importar hotels des de Google Places")
    @ApiResponse(responseCode = "200", description = "Hotels importats correctament")
    @PostMapping("/importar")
    public Map<String, Object> importar() throws Exception {
        List<String> insertados = hotelService.importarHotelesDesdeGoogle();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "✅ Hoteles insertados: " + insertados.size());
        respuesta.put("hoteles", insertados);

        return respuesta;
    }

    /**
     * Actualitza les imatges dels hotels des de Google.
     *
     * @return Informació sobre els hotels actualitzats
     * @throws Exception Si hi ha un error en l'actualització
     */
    @Operation(summary = "Actualitzar imatges dels hotels")
    @ApiResponse(responseCode = "200", description = "Imatges actualitzades correctament")
    @PostMapping("/actualizarimg")
    public Map<String, Object> actualizarImagenes() throws Exception {
        List<String> actualizados = hotelService.actualizarFotosHotelesDesdeGoogle();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "🖼️ Imágenes actualizadas: " + actualizados.size());
        respuesta.put("hoteles", actualizados);

        return respuesta;
    }

    /**
     * Actualitza les habitacions disponibles d'un hotel.
     *
     * @param id           ID de l'hotel
     * @param roomsBooked  Nombre d'habitacions a disminuir
     * @return ResponseEntity amb el resultat de l'operació
     */
    @Operation(summary = "Actualitzar habitacions disponibles d'un hotel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Actualització correcta"),
            @ApiResponse(responseCode = "400", description = "Error en la petició")
    })
    @PutMapping("/{id}/update-available-rooms")
    public ResponseEntity<?> updateAvailableRooms(@PathVariable Long id, @RequestParam int roomsBooked) {
        try {
            hotelService.disminuirHabitacionsDisponibles(id, roomsBooked);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}


