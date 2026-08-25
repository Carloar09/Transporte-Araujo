package elicuci.czelada.araujo.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public String procesarLogin(String username, String password) {
        // Lógica temporal de prueba
        if ("admin".equals(username) && "1234".equals(password)) {
            return "Login exitoso - Aquí irá el token real";
        }
        throw new RuntimeException("Credenciales inválidas");
    }
}