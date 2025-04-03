package com.hotelconnect.backend.users;

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


    public Map<String, Object> registerUser(User user) {
        Map<String, Object> response = new HashMap<>();

        // Verificar si el usuario ya existe
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            response.put("success", false);
            response.put("message", "El correo ya está registrado.");
            return response;  // Si ya existe, devolvemos el mensaje y el estado de error
        }

        // Guardar el nuevo usuario
        userRepository.save(user);
        response.put("success", true);
        response.put("message", "Usuario registrado con éxito.");
        return response;  // Si el registro fue exitoso, devolvemos el mensaje de éxito
    }

    // Metodo modificado para el login
    public Map<String, Object> loginUser(User user) {
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());

        Map<String, Object> response = new HashMap<>();

        // Verificamos si el usuario existe y la contraseña es correcta
        if (foundUser.isPresent() && foundUser.get().getPassword().equals(user.getPassword())) {
            response.put("success", true);
            response.put("message", "Inicio de sesión exitoso");
        } else {
            response.put("success", false);
            response.put("message", "Correo o contraseña incorrectos");
        }

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

    // Eliminar un usuario por ID
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }
        userRepository.deleteById(id); // Eliminar usuario por ID
    }
}

