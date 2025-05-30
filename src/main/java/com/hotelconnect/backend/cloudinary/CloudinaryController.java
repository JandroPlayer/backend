package com.hotelconnect.backend.cloudinary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CloudinaryController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Operation(
            summary = "Obtener firma y configuración de Cloudinary",
            description = "Genera una firma segura para subir archivos a Cloudinary"
    )
    @ApiResponse(responseCode = "200", description = "Firma generada correctamente")
    @GetMapping("/cloudinary-signature")
    public Map<String, Object> getCloudinarySignature() throws NoSuchAlgorithmException {
        return cloudinaryService.generateSignatureData();
    }
}
