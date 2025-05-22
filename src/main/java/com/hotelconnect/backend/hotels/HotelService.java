package com.hotelconnect.backend.hotels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Hotel saveHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    // Implementar el mètode per obtenir un hotel per ID
    public Hotel getHotelById(Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        return hotel.orElse(null); // Retorna null si no es troba
    }

    public List<Hotel> obtenirTotsElsHotelsSenseActivitats() {
        List<Hotel> hotels = hotelRepository.findAll();

        // Eliminar la carga de las actividades para cada hotel
        for (Hotel hotel : hotels) {
            hotel.setActivitats(null);
        }

        return hotels;
    }

    // Metodo para obtener un hotel sin actividades
    public Hotel obtenerHotelSinActividades(Long id) {
        // Aquí puedes hacer una consulta personalizada si es necesario, por ejemplo:
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));

        // Aquí nos aseguramos de no cargar las actividades asociadas, eliminando la carga de la relación
        hotel.setActivitats(null);  // Si el objeto Hotel tiene una relación con actividades

        return hotel;
    }

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${google.api.key}")
    private String googleApiKey;

    public List<String> importarHotelesDesdeGoogle() throws Exception {
        Random random = new Random();
        String searchUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=agroturismo+hotel+mallorca&key=" + googleApiKey;
        JsonNode response = mapper.readTree(restTemplate.getForObject(searchUrl, String.class));

        List<String> insertados = new ArrayList<>();
        System.out.println("Total lugares encontrados: " + response.get("results").size());

        for (JsonNode lugar : response.get("results")) {
            String placeId = lugar.get("place_id").asText();

            if (hotelRepository.findByPlaceId(placeId).isEmpty()) {
                String detailsUrl = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + placeId + "&fields=name,formatted_address,geometry,rating,formatted_phone_number,website,url,photos&key=" + googleApiKey;
                JsonNode details = mapper.readTree(restTemplate.getForObject(detailsUrl, String.class)).get("result");

                Hotel hotel = new Hotel();
                hotel.setPlaceId(placeId);
                hotel.setName(details.get("name").asText());
                hotel.setAddress(details.get("formatted_address").asText());
                hotel.setLat(details.get("geometry").get("location").get("lat").asDouble());
                hotel.setLng(details.get("geometry").get("location").get("lng").asDouble());
                hotel.setRating(details.has("rating") ? details.get("rating").asDouble() : null);
                hotel.setPhone(details.has("formatted_phone_number") ? details.get("formatted_phone_number").asText() : null);
                hotel.setWebsite(details.has("website") ? details.get("website").asText() : null);
                hotel.setGoogleMapsUrl(details.has("url") ? details.get("url").asText() : null);

                // 🔽 Añadir la imagen si hay 'photos'
                if (details.has("photos") && details.get("photos").isArray() && !details.get("photos").isEmpty()) {
                    String photoReference = details.get("photos").get(0).get("photo_reference").asText();
                    String imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photo_reference="
                            + photoReference + "&key=" + googleApiKey;
                    hotel.setImageUrl(imageUrl);
                }

                // Simular price_per_night y available_rooms
                hotel.setPricePerNight(50 + random.nextDouble() * 150);  // Entre 50 y 200 €
                hotel.setAvailableRooms(5 + random.nextInt(30));  // Entre 5 y 35 habitaciones

                hotelRepository.save(hotel);
                insertados.add(hotel.getName());
            }
        }

        return insertados;
    }

    public List<String> actualizarFotosHotelesDesdeGoogle() throws Exception {
        List<Hotel> hoteles = hotelRepository.findAll();
        List<String> actualizados = new ArrayList<>();

        for (Hotel hotel : hoteles) {
            String placeId = hotel.getPlaceId();

            // Construir URL para obtener los detalles del lugar
            String detailsUrl = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + placeId +
                    "&fields=photos&key=" + googleApiKey;

            // Llamada a la API de Google Places
            JsonNode detailsResponse = mapper.readTree(restTemplate.getForObject(detailsUrl, String.class));
            JsonNode details = detailsResponse.get("result");

            if (details != null && details.has("photos") && details.get("photos").isArray() && !details.get("photos").isEmpty()) {
                String photoReference = details.get("photos").get(0).get("photo_reference").asText();

                String nuevaImageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photo_reference="
                        + photoReference + "&key=" + googleApiKey;

                // Solo actualizar si la URL ha cambiado
                if (!nuevaImageUrl.equals(hotel.getImageUrl())) {
                    hotel.setImageUrl(nuevaImageUrl);
                    hotelRepository.save(hotel);
                    actualizados.add(hotel.getName());
                }
            }
        }

        return actualizados;
    }

    // En HotelService.java
    @Transactional
    public void disminuirHabitacionsDisponibles(Long hotelId, int roomsBooked) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));

        int habitacionesDisponibles = hotel.getAvailableRooms();
        if (roomsBooked > habitacionesDisponibles) {
            throw new RuntimeException("No hay suficientes habitaciones disponibles");
        }

        hotel.setAvailableRooms(habitacionesDisponibles - roomsBooked);
        hotelRepository.save(hotel);
    }

}



