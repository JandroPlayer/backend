package com.hotelconnect.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    // 🔹 Implementar el mètode per obtenir un hotel per ID
    public Hotel getHotelById(Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        return hotel.orElse(null); // Retorna null si no es troba
    }

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${google.api.key}")
    private String googleApiKey;

    public List<String> importarHotelesDesdeGoogle() throws Exception {
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

                hotelRepository.save(hotel);
                insertados.add(hotel.getName());
            }
        }

        return insertados;
    }
}



