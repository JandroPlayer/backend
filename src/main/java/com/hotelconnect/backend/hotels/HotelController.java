package com.hotelconnect.backend.hotels;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "*") // Permet connexions des del frontend
public class HotelController {

    @Autowired
    private HotelService hotelService;

    // Obtenir tots els hotels
    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @PostMapping
    public ResponseEntity<Hotel> saveHotel(@RequestBody Hotel hotel) {
        return ResponseEntity.ok(hotelService.saveHotel(hotel));
    }

    // 🔹 Obtenir hotel per ID
    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable Long id) {
        Hotel hotel = hotelService.getHotelById(id);
        if (hotel != null) {
            return ResponseEntity.ok(hotel);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/without-activities")
    public List<Hotel> getHotelsWithoutActivitats() {
        return hotelService.obtenirTotsElsHotelsSenseActivitats();
    }

    @GetMapping("/{id}/without-activities")
    public ResponseEntity<HotelDTO> getHotelWithoutActivities(@PathVariable Long id) {
        Hotel hotel = hotelService.obtenerHotelSinActividades(id);  // Metodo que omite las actividades
        HotelDTO hotelDTO = new HotelDTO(hotel);
        return ResponseEntity.ok(hotelDTO);
    }

    @PostMapping("/importar")
    public Map<String, Object> importar() throws Exception {
        List<String> insertados = hotelService.importarHotelesDesdeGoogle();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "✅ Hoteles insertados: " + insertados.size());
        respuesta.put("hoteles", insertados);

        return respuesta;
    }

    @PostMapping("/actualizarimg")
    public Map<String, Object> actualizarImagenes() throws Exception {
        List<String> actualizados = hotelService.actualizarFotosHotelesDesdeGoogle();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "🖼️ Imágenes actualizadas: " + actualizados.size());
        respuesta.put("hoteles", actualizados);

        return respuesta;
    }

    // En HotelController.java
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

