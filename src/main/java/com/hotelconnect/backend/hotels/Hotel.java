package com.hotelconnect.backend.hotels;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hotelconnect.backend.activitats.Activitat;
import com.hotelconnect.backend.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "agroturismo_hoteles")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(length = 1000)
    private String imageUrl;
    @Column(name = "price_per_night")
    private Double pricePerNight;
    @Column(name = "available_rooms")
    private Integer availableRooms;

    @ManyToMany
    @JoinTable(
            name = "hotel_activitat",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "activitat_id")
    )
    @JsonIgnore
    private List<Activitat> activitats;

    @ManyToMany
    @JoinTable(
            name = "users_favorits",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonBackReference
    @JsonIgnore
    private List<User> usuarisFavorits = new ArrayList<>();

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", rating=" + rating +
                ", phone='" + phone + '\'' +
                ", website='" + website + '\'' +
                ", googleMapsUrl='" + googleMapsUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

}


