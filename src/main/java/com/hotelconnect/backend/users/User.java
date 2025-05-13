package com.hotelconnect.backend.users;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hotelconnect.backend.hotels.Hotel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String img;
    private BigDecimal saldo = BigDecimal.ZERO;

    @ManyToMany
    @JoinTable(
            name = "users_favorits",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "hotel_id")
    )
    @JsonBackReference
    @JsonIgnore
    private List<Hotel> hotelsFavorits = new ArrayList<>();

    // toString personalizado para evitar StackOverflowError
    @Override
    public String toString() {
        return "User{id=" + id + ", " +
                "name='" + name + "', " +
                "email='" + email + "', " +
                "createdAt=" + createdAt + ", " +
                "img='" + img + "', " +
                "saldo=" + saldo + "}";
    }
}
