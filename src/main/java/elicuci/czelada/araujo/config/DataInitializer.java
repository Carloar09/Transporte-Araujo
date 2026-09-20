package elicuci.czelada.araujo.config;

import elicuci.czelada.araujo.entity.Chofer;
import elicuci.czelada.araujo.entity.Ciudad;
import elicuci.czelada.araujo.entity.Usuario;
import elicuci.czelada.araujo.entity.enums.EstadoVehiculo;
import elicuci.czelada.araujo.entity.enums.RolUsuario;
import elicuci.czelada.araujo.repository.ChoferRepository;
import elicuci.czelada.araujo.repository.CiudadRepository;
import elicuci.czelada.araujo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            CiudadRepository ciudadRepository,
            ChoferRepository choferRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Cargar ciudades si la tabla está vacía
            if (ciudadRepository.count() == 0) {
                ciudadRepository.save(new Ciudad(null, "Cajamarca", "Cajamarca", true));
                ciudadRepository.save(new Ciudad(null, "Celendín", "Celendín", true));
                ciudadRepository.save(new Ciudad(null, "Balsas", "Chachapoyas", true));
                ciudadRepository.save(new Ciudad(null, "Leymebamba", "Chachapoyas", true));
                ciudadRepository.save(new Ciudad(null, "Chachapoyas", "Chachapoyas", true));
            }

            // Cargar un chofer de prueba si la tabla está vacía
            if (choferRepository.count() == 0) {
                Chofer chofer = new Chofer();
                chofer.setNombreCompleto("Carlos Mendoza");
                chofer.setDni("72819203");
                chofer.setLicencia("A-IIIc-72819203");
                chofer.setTelefono("976543210");
                chofer.setEstado(EstadoVehiculo.OPERATIVO);
                choferRepository.save(chofer);
            }

            // Cargar o actualizar usuario cajero de prueba
            Usuario ventanilla = usuarioRepository.findByDni("77777777").orElse(null);
            if (ventanilla == null) {
                ventanilla = new Usuario();
                ventanilla.setDni("77777777");
                ventanilla.setPassword(passwordEncoder.encode("123456"));
                ventanilla.setNombreCompleto("Vendedor Ventanilla 1");
                ventanilla.setTelefono("987654321");
                ventanilla.setRol(RolUsuario.VENTANILLA);
                ventanilla.setActivo(true);
                usuarioRepository.save(ventanilla);
            } else if (!ventanilla.getPassword().startsWith("$2a$") && !ventanilla.getPassword().startsWith("$2b$")) {
                // Si la contraseña estaba en texto plano en la BD, la actualizamos a BCrypt
                ventanilla.setPassword(passwordEncoder.encode("123456"));
                usuarioRepository.save(ventanilla);
            }

            // Cargar usuario administrador de prueba si no existe
            if (!usuarioRepository.existsByDni("88888888")) {
                Usuario admin = new Usuario();
                admin.setDni("88888888");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setNombreCompleto("Administrador General");
                admin.setTelefono("999888777");
                admin.setRol(RolUsuario.ADMINISTRADOR);
                admin.setActivo(true);
                usuarioRepository.save(admin);
            }
        };
    }
}