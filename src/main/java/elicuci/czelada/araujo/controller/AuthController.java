package elicuci.czelada.araujo.controller;

import elicuci.czelada.araujo.dto.AuthResponseDTO;
import elicuci.czelada.araujo.dto.LoginRequestDTO;
import elicuci.czelada.araujo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.procesarLogin(request));
    }
}