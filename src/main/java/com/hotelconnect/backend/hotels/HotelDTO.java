package com.hotelconnect.backend.hotels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HotelDTO {
    private Long id;
    private String placeId;
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private Double rating;
    private String phone;
    private String website;
    private String googleMapsUrl;
    private String imageUrl;

    public HotelDTO(Hotel hotel) {
        this.id = hotel.getId();
        this.placeId = hotel.getPlaceId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();
        this.lat = hotel.getLat();
        this.lng = hotel.getLng();
        this.rating = hotel.getRating();
        this.phone = hotel.getPhone();
        this.website = hotel.getWebsite();
        this.googleMapsUrl = hotel.getGoogleMapsUrl();
        this.imageUrl = hotel.getImageUrl();
    }

}

