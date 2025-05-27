package com.hotelconnect.backend.users;

import com.hotelconnect.backend.hotels.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // Autentificació
    // Metodo para registrar un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (userService.emailExists(user.getEmail())) {
            response.put("status", "error");
            response.put("message", "El correo ya está registrado.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 🔐 Encriptar y guardar usuario
        userService.save(user);

        response.put("status", "success");
        response.put("message", "Usuario registrado con éxito");
        return ResponseEntity.ok(response);
    }


    // Metodo de login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody User user) {
        // Llamamos al servicio para verificar el login
        Map<String, Object> loginResponse = userService.loginUser(user);

        // Si el login es exitoso
        if ((boolean) loginResponse.get("success")) {
            // Obtener los detalles completos del usuario (suponiendo que ya tienes acceso al usuario)
            User loggedInUser = userService.getUserByEmail(user.getEmail()).orElse(null);

            // Si el usuario fue encontrado
            if (loggedInUser != null) {
                // Crear la respuesta con todos los datos del usuario
                Map<String, Object> userResponse = new HashMap<>();
                userResponse.put("id", loggedInUser.getId());
                userResponse.put("name", loggedInUser.getName());
                userResponse.put("email", loggedInUser.getEmail());
                userResponse.put("createdAt", loggedInUser.getCreatedAt());
                userResponse.put("img", loggedInUser.getImg());
                userResponse.put("isadmin", loggedInUser.isAdmin());

                // Puedes agregar más campos según sea necesario
                return ResponseEntity.ok(userResponse);
            } else {
                // Si no se encontró el usuario, retornar un error
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Usuario no encontrado"));
            }
        } else {
            // Si el login falla, retornar un error con el mensaje
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loginResponse);
        }
    }

    // CRUD
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

    // Pagaments
    @PutMapping("/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable int id, @RequestBody Map<String, Double> request) {
        try {
            double amount = request.get("amount");
            User updatedUser = userService.updateUserBalance(id, amount);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Favoritos
    // AÑADIR FAVORITO
    @PostMapping("/{id}/favorits/{hotelId}")
    public ResponseEntity<Map<String, String>> afegirFavorit(@PathVariable Integer id, @PathVariable Integer hotelId) {
        try {
            userService.afegirFavorit(id, hotelId);
            return ResponseEntity.ok(Map.of("message", "Hotel afegit a favorits correctament"));
        } catch (IllegalStateException e) {
            // ⚠️ No devolver error, sino éxito con mensaje informativo
            return ResponseEntity.ok(Map.of("message", "Aquest hotel ja està als teus favorits"));
        } catch (Exception e) {
            // Només en errors realment inesperats retornam 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error inesperat"));
        }
    }

    // ELIMINAR FAVORITO
    @DeleteMapping("/{id}/favorits/{hotelId}")
    public ResponseEntity<Map<String, String>> eliminarFavorit(@PathVariable Integer id, @PathVariable Integer hotelId) {
        try {
            userService.eliminarFavorit(id, hotelId);
            return ResponseEntity.ok(Map.of("message", "Hotel eliminado de favoritos correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error al eliminar el hotel de favoritos"));
        }
    }


    // Listar los favoritos de un usuario
    @GetMapping("/{id}/favorits")
    public ResponseEntity<List<Hotel>> llistarFavorits(@PathVariable Integer id) {
        try {
            List<Hotel> favoritos = userService.obtenirFavorits(id);
            return ResponseEntity.ok(favoritos);
        } catch (Exception ex) {
            ex.printStackTrace();  // Muestra el error detallado en la consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}


