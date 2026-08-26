package elicuci.czelada.araujo.controller;

import elicuci.czelada.araujo.dto.AuthResponseDTO;
import elicuci.czelada.araujo.dto.LoginRequestDTO;
import elicuci.czelada.araujo.dto.RegisterRequestDTO;
import elicuci.czelada.araujo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/registrar")
    public ResponseEntity<AuthResponseDTO> registrar(
            @Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registrar(request));
    }
}