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

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            CiudadRepository ciudadRepository,
            ChoferRepository choferRepository,
            UsuarioRepository usuarioRepository) {
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
                chofer.setLicencia("A-IIIc-72819203"); //manera de codificar una licencia (TIPO- subtipo-dni) no sé si esté bien xd
                chofer.setTelefono("976543210");
                chofer.setEstado(EstadoVehiculo.OPERATIVO);
                choferRepository.save(chofer);
            }

            // Cargar un usuario cajero de prueba si la tabla está vacía
            // (ESTA WEA ELIMINAR AL MOMENTO DE SUBIR EL PROYECYO XD ES SOLO PRUEBA)
            if (usuarioRepository.count() == 0) {
                Usuario usuario = new Usuario();
                usuario.setDni("77777777");
                usuario.setPassword("123456");
                usuario.setNombreCompleto("Vendedor Ventanilla 1");
                usuario.setTelefono("987654321");
                usuario.setRol(RolUsuario.VENTANILLA);
                usuario.setActivo(true);
                usuarioRepository.save(usuario);
            }
        };
    }
}