package com.hotelconnect.backend.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // Metodo para registrar un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        // Llamamos al servicio para registrar el usuario
        Map<String, Object> registerResponse = userService.registerUser(user);

        // Si el registro fue exitoso, retornamos un status 200 (OK) con el mensaje
        if ((boolean) registerResponse.get("success")) {
            return ResponseEntity.ok(registerResponse);
        } else {
            // Si el correo ya está registrado, retornamos un status 400 (BAD_REQUEST) con el mensaje
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registerResponse);
        }
    }

    // Metodo de login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody User user) {
        // Llamamos al servicio para verificar el login
        Map<String, Object> loginResponse = userService.loginUser(user);

        // Si el login es exitoso, retornamos un status 200 (OK) con el mensaje
        if ((boolean) loginResponse.get("success")) {
            return ResponseEntity.ok(loginResponse);
        } else {
            // Si el login falla, retornamos un status 400 (BAD_REQUEST) con el mensaje
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loginResponse);
        }
    }


    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Poner el nombre de la variable entre parentesis en el @PathVariable
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Integer id) {
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Actualizar un usuario
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user); // Llamamos al servicio para actualizar
            return ResponseEntity.ok(updatedUser); // Devuelve el usuario actualizado
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Si el usuario no existe, retornamos error
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
    }
}


