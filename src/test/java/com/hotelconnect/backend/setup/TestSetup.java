package com.hotelconnect.backend.setup;

import com.hotelconnect.backend.booking.Reserva;
import com.hotelconnect.backend.hotels.Hotel;
import com.hotelconnect.backend.users.User;

import java.math.BigDecimal;
import java.util.Date;

public class TestSetup {

    public static User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        user.setPassword("testpass");
        user.setImg("https://img.testuser.com/profile.jpg");
        user.setSaldo(new BigDecimal("100.00"));
        return user;
    }

    public static Hotel createTestHotel() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setPlaceId("ChIJ123456");
        hotel.setName("Hotel de Prova");
        hotel.setAddress("Carrer Major, 1");
        hotel.setLat(39.6953);
        hotel.setLng(3.0176);
        hotel.setRating(4.5);
        hotel.setPhone("+34971123456");
        hotel.setWebsite("https://hotelprova.com");
        hotel.setGoogleMapsUrl("https://maps.google.com/hotelprova");
        hotel.setImageUrl("https://img.hotelprova.com/foto.jpg");
        hotel.setPricePerNight(120.0);
        hotel.setAvailableRooms(10);
        return hotel;
    }

    public static Reserva createTestReserva(User user, Hotel hotel) {
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setHotel(hotel);
        reserva.setUser(user);
        reserva.setStartDate(new Date());
        reserva.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 dia
        reserva.setAdults(2);
        reserva.setChildren(1);
        reserva.setRooms(1);
        reserva.setPreu(120.0);
        reserva.setPagada(false);
        return reserva;
    }
}
