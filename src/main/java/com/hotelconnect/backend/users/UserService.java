package com.hotelconnect.backend.users;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, Object> registerUser(User user) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.existsByEmail(user.getEmail())) {
            response.put("success", false);
            response.put("message", "El correo ya está registrado");
            return response;
        }

        // 🔐 Encriptar la contraseña antes de guardar
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Guardar el usuario en la base de datos
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "Usuario registrado con éxito");
        return response;
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
}

