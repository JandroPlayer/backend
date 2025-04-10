package com.hotelconnect.backend;

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

    @PostMapping("/importar")
    public Map<String, Object> importar() throws Exception {
        List<String> insertados = hotelService.importarHotelesDesdeGoogle();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "✅ Hoteles insertados: " + insertados.size());
        respuesta.put("hoteles", insertados);

        return respuesta;
    }
}

