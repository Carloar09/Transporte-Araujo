package elicuci.czelada.araujo.config;

import elicuci.czelada.araujo.entity.Chofer;
import elicuci.czelada.araujo.entity.Ciudad;
import elicuci.czelada.araujo.entity.enums.EstadoVehiculo;
import elicuci.czelada.araujo.repository.ChoferRepository;
import elicuci.czelada.araujo.repository.CiudadRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(CiudadRepository ciudadRepository, ChoferRepository choferRepository) {
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
        };
    }
}