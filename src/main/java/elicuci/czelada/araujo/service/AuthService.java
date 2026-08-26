package elicuci.czelada.araujo.service;

import elicuci.czelada.araujo.dto.AuthResponseDTO;
import elicuci.czelada.araujo.dto.LoginRequestDTO;
import elicuci.czelada.araujo.dto.RegisterRequestDTO;
import elicuci.czelada.araujo.entity.Usuario;
import elicuci.czelada.araujo.repository.UsuarioRepository;
import elicuci.czelada.araujo.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDTO login(LoginRequestDTO request) {

        // Busca Dni
        Usuario usuario = usuarioRepository.findByDni(request.getDni())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        // Verificar si el rol coincide con el seleccionado en el login
        if (!usuario.getRol().name().equals(request.getRol().name())) {
            throw new RuntimeException("No tienes acceso con ese rol");
        }

        // verifica tu contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        //  Verificar que esté activo
        if (!usuario.isActivo()) {
            throw new RuntimeException("Usuario desactivado");
        }

        // Genera token con DNI y rol
        String token = jwtService.generarToken(usuario.getDni(), usuario.getRol().name());

        return AuthResponseDTO.builder()
                .token(token)
                .nombreCompleto(usuario.getNombreCompleto())
                .dni(usuario.getDni())
                .rol(usuario.getRol().name())
                .mensaje("Login exitoso")
                .build();
    }
    // para registar un usuario
    @Transactional
    public AuthResponseDTO registrar(RegisterRequestDTO request) {

        // Verificar que el DNI no exista vv
        if (usuarioRepository.existsByDni(request.getDni())) {
            throw new RuntimeException("Ya existe un usuario con el DNI: " + request.getDni());
        }

        // creacion del usuario con los parametros del DTO
        Usuario usuario = new Usuario();
        usuario.setDni(request.getDni());
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setTelefono(request.getTelefono());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        // Generar Token
        String token = jwtService.generarToken(usuario.getDni(), usuario.getRol().name());

        return AuthResponseDTO.builder()
                .token(token)
                .nombreCompleto(usuario.getNombreCompleto())
                .dni(usuario.getDni())
                .rol(usuario.getRol().name())
                .mensaje("Usuario registrado exitosamente")
                .build();
    }
}