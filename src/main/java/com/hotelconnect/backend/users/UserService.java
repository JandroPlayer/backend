package com.hotelconnect.backend.users;

import com.hotelconnect.backend.hotels.Hotel;
import com.hotelconnect.backend.hotels.HotelRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Mètodes per al registre
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void save(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    // Metodo modificado para el login
    public Map<String, Object> loginUser(User user) {
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());

        Map<String, Object> response = new HashMap<>();

        if (foundUser.isPresent()) {
            // 🔍 Compara la contrasenya introduïda amb la contrasenya encriptada
            boolean passwordMatches = passwordEncoder.matches(user.getPassword(), foundUser.get().getPassword());

            if (passwordMatches) {
                response.put("success", true);
                response.put("message", "Inicio de sesión exitoso");
                return response;
            }
        }

        response.put("success", false);
        response.put("message", "Correo o contraseña incorrectos");
        return response;
    }

    // Obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll(); // Retorna todos los usuarios
    }

    // Obtener un usuario por ID
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id); // Retorna un usuario por su ID
    }

    // Actualizar un usuario existente
    public User updateUser(Integer id, User user) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }
        user.setId(id); // Asegura que el ID se mantenga igual
        return userRepository.save(user); // Guardar los cambios
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);  // Este método buscará el usuario en la base de datos por su email
    }


    // Eliminar un usuario por ID
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }
        userRepository.deleteById(id); // Eliminar usuario por ID
    }

    public User updateUserBalance(int userId, double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        BigDecimal amountToSubtract = BigDecimal.valueOf(amount);

        // Verificar que el usuario tiene suficiente saldo
        if (user.getSaldo().compareTo(amountToSubtract) >= 0) {
            user.setSaldo(user.getSaldo().subtract(amountToSubtract));
            return userRepository.save(user);
        } else {
            throw new RuntimeException("Saldo insuficiente");
        }
    }

    // Favorits
    @Transactional
    public void afegirFavorit(Integer userId, Integer hotelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuari no trobat"));

        Hotel hotel = hotelRepository.findById(Long.valueOf(hotelId))
                .orElseThrow(() -> new RuntimeException("Hotel no trobat"));

        // Comprobar si ya es favorito
        if (user.getHotelsFavorits().contains(hotel)) {
            throw new IllegalStateException("Aquest hotel ja és als favorits de l'usuari.");
        }

        user.getHotelsFavorits().add(hotel);
        userRepository.save(user);
    }

    public void eliminarFavorit(Integer userId, Integer hotelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuari no trobat"));
        Hotel hotel = hotelRepository.findById(Long.valueOf(hotelId))
                .orElseThrow(() -> new RuntimeException("Hotel no trobat"));

        user.getHotelsFavorits().remove(hotel);
        userRepository.save(user);
    }

    public List<Hotel> obtenirFavorits(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getHotelsFavorits();
    }
}

