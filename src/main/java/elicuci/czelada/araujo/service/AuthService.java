package elicuci.czelada.araujo.service;

import elicuci.czelada.araujo.dto.AuthResponseDTO;
import elicuci.czelada.araujo.dto.LoginRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public AuthResponseDTO procesarLogin(LoginRequestDTO request) {
        // Simulación temporal usando Lombok @Builder con los campos del DTO
        if ("12345678".equals(request.getDni()) && "1234".equals(request.getPassword())) {
            return AuthResponseDTO.builder()
                    .token("mock-jwt-token-xyz-12345")
                    .nombreCompleto("Usuario de Prueba")
                    .dni(request.getDni())
                    .rol(request.getRol() != null ? request.getRol().name() : "ADMIN")
                    .mensaje("Login exitoso")
                    .build();
        }
        throw new RuntimeException("Credenciales inválidas");
    }
}