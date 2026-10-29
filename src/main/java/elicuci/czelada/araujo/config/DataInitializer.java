package elicuci.czelada.araujo.config;

import elicuci.czelada.araujo.entity.*;
import elicuci.czelada.araujo.entity.enums.EstadoAsiento;
import elicuci.czelada.araujo.entity.enums.EstadoPasaje;
import elicuci.czelada.araujo.entity.enums.EstadoVehiculo;
import elicuci.czelada.araujo.entity.enums.EstadoViaje;
import elicuci.czelada.araujo.entity.enums.RolUsuario;
import elicuci.czelada.araujo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            CiudadRepository ciudadRepository,
            ChoferRepository choferRepository,
            VehiculoRepository vehiculoRepository,
            UsuarioRepository usuarioRepository,
            ViajeRepository viajeRepository,
            AsientoRepository asientoRepository,
            PasajeRepository pasajeRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Cargar ciudades iniciales si la tabla está vacía
            if (ciudadRepository.count() == 0) {
                ciudadRepository.save(new Ciudad(null, "Cajamarca", "Cajamarca", true));
                ciudadRepository.save(new Ciudad(null, "Celendín", "Celendín", true));
                ciudadRepository.save(new Ciudad(null, "Balsas", "Chachapoyas", true));
                ciudadRepository.save(new Ciudad(null, "Leymebamba", "Chachapoyas", true));
                ciudadRepository.save(new Ciudad(null, "Chachapoyas", "Chachapoyas", true));
            } else {
                // Limpieza de duplicados si existieran registros repetidos de Cajamarca o Celendín
                List<Ciudad> todas = ciudadRepository.findAll();
                Set<String> vistas = new HashSet<>();
                for (Ciudad c : todas) {
                    if (c.getNombre() != null) {
                        String key = c.getNombre().trim().toLowerCase();
                        if (vistas.contains(key)) {
                            c.setActiva(false);
                            ciudadRepository.save(c);
                        } else {
                            vistas.add(key);
                            c.setActiva(true);
                            ciudadRepository.save(c);
                        }
                    }
                }
            }

            // 2. Cargar vehículo de prueba si no existe
            Vehiculo combi = null;
            if (vehiculoRepository.count() == 0) {
                combi = new Vehiculo();
                combi.setPlaca("T3B-123");
                combi.setMarca("Toyota");
                combi.setModelo("HiAce");
                combi.setAnio(2024);
                combi.setCapacidad(15);
                combi.setEstado(EstadoVehiculo.OPERATIVO);
                combi.setUltimaRevision(LocalDate.now());
                combi = vehiculoRepository.save(combi);
            } else {
                combi = vehiculoRepository.findAll().get(0);
            }

            // 3. Cargar un chofer de prueba si la tabla está vacía
            Chofer chofer = null;
            if (choferRepository.count() == 0) {
                chofer = new Chofer();
                chofer.setNombreCompleto("Carlos Mendoza");
                chofer.setDni("72819203");
                chofer.setLicencia("A-IIIc-72819203");
                chofer.setTelefono("976543210");
                chofer.setEstado(EstadoVehiculo.OPERATIVO);
                chofer.setVehiculo(combi);
                chofer = choferRepository.save(chofer);
            } else {
                chofer = choferRepository.findAll().get(0);
                if (chofer.getVehiculo() == null && combi != null) {
                    chofer.setVehiculo(combi);
                    choferRepository.save(chofer);
                }
            }

            // 4. Cargar o actualizar usuario cajero de prueba
            Usuario ventanilla = usuarioRepository.findByDni("77777777").orElse(null);
            if (ventanilla == null) {
                ventanilla = new Usuario();
                ventanilla.setDni("77777777");
                ventanilla.setPassword(passwordEncoder.encode("123456"));
                ventanilla.setNombreCompleto("Vendedor Ventanilla 1");
                ventanilla.setTelefono("987654321");
                ventanilla.setRol(RolUsuario.VENTANILLA);
                ventanilla.setActivo(true);
                ventanilla = usuarioRepository.save(ventanilla);
            } else if (!ventanilla.getPassword().startsWith("$2a$") && !ventanilla.getPassword().startsWith("$2b$")) {
                ventanilla.setPassword(passwordEncoder.encode("123456"));
                ventanilla = usuarioRepository.save(ventanilla);
            }

            // 5. Cargar usuario administrador de prueba si no existe
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

            // 6. Cargar viajes de ayer y de hoy si no hay suficientes viajes en la BD
            LocalDate hoy = LocalDate.now();
            LocalDateTime inicioAyer = hoy.minusDays(1).atStartOfDay();
            LocalDateTime finHoy = hoy.plusDays(1).atStartOfDay();
            long viajesRecientes = viajeRepository.countByFechaHoraBetween(inicioAyer, finHoy);

            if (viajesRecientes == 0) {
                Ciudad cajamarca = ciudadRepository.findByActivaTrue().stream()
                        .filter(c -> c.getNombre().toLowerCase().contains("cajamarca"))
                        .findFirst().orElse(null);
                Ciudad celendin = ciudadRepository.findByActivaTrue().stream()
                        .filter(c -> c.getNombre().toLowerCase().contains("celend"))
                        .findFirst().orElse(null);

                if (cajamarca != null && celendin != null && combi != null && chofer != null) {
                    // --- VIAJE DE AYER 1 (Cajamarca -> Celendin) ---
                    crearViajeConPasajeros(viajeRepository, asientoRepository, pasajeRepository,
                            cajamarca, celendin, combi, chofer, ventanilla,
                            hoy.minusDays(1).atTime(8, 0), 10.0, 5);

                    // --- VIAJE DE AYER 2 (Celendin -> Cajamarca) ---
                    crearViajeConPasajeros(viajeRepository, asientoRepository, pasajeRepository,
                            celendin, cajamarca, combi, chofer, ventanilla,
                            hoy.minusDays(1).atTime(14, 0), 10.0, 4);

                    // --- VIAJES DE HOY 1 (08:30 Cajamarca -> Celendin) ---
                    crearViajeConPasajeros(viajeRepository, asientoRepository, pasajeRepository,
                            cajamarca, celendin, combi, chofer, ventanilla,
                            hoy.atTime(8, 30), 10.0, 3);

                    // --- VIAJES DE HOY 2 (11:30 Celendin -> Cajamarca) ---
                    crearViajeConPasajeros(viajeRepository, asientoRepository, pasajeRepository,
                            celendin, cajamarca, combi, chofer, ventanilla,
                            hoy.atTime(11, 30), 10.0, 2);

                    // --- VIAJES DE HOY 3 (15:00 Cajamarca -> Celendin) ---
                    crearViajeConPasajeros(viajeRepository, asientoRepository, pasajeRepository,
                            cajamarca, celendin, combi, chofer, ventanilla,
                            hoy.atTime(15, 0), 10.0, 0);
                }
            }
        };
    }

    private void crearViajeConPasajeros(
            ViajeRepository viajeRepository,
            AsientoRepository asientoRepository,
            PasajeRepository pasajeRepository,
            Ciudad origen, Ciudad destino, Vehiculo vehiculo, Chofer chofer, Usuario ventanilla,
            LocalDateTime fechaHora, double precio, int cantPasajerosOcupados) {

        Viaje viaje = new Viaje();
        viaje.setCiudadOrigen(origen);
        viaje.setCiudadDestino(destino);
        viaje.setVehiculo(vehiculo);
        viaje.setChofer(chofer);
        viaje.setFechaHora(fechaHora);
        viaje.setPrecio(BigDecimal.valueOf(precio));
        viaje.setEstado(EstadoViaje.PROGRAMADO);
        Viaje guardado = viajeRepository.save(viaje);

        // Crear asientos
        List<Asiento> asientos = new ArrayList<>();
        Asiento choferAsiento = new Asiento();
        choferAsiento.setViaje(guardado);
        choferAsiento.setNumero(0);
        choferAsiento.setEsChofer(true);
        choferAsiento.setEstado(EstadoAsiento.OCUPADO);
        asientos.add(choferAsiento);

        for (int i = 1; i <= vehiculo.getCapacidad(); i++) {
            Asiento a = new Asiento();
            a.setViaje(guardado);
            a.setNumero(i);
            a.setEsChofer(false);
            if (i <= cantPasajerosOcupados) {
                a.setEstado(EstadoAsiento.OCUPADO);
                a.setDniPasajero("456789" + String.format("%02d", i));
                a.setNombrePasajero("Pasajero Muestra " + i);
                a.setPrecio(precio);
            } else {
                a.setEstado(EstadoAsiento.LIBRE);
            }
            asientos.add(a);
        }
        List<Asiento> asientosGuardados = asientoRepository.saveAll(asientos);

        // Crear pasajes para los ocupados
        for (int i = 1; i <= cantPasajerosOcupados; i++) {
            final int num = i;
            Asiento a = asientosGuardados.stream().filter(as -> as.getNumero() == num).findFirst().orElse(null);
            if (a != null && ventanilla != null) {
                Pasaje p = new Pasaje();
                p.setViaje(guardado);
                p.setAsiento(a);
                p.setNombrePasajero(a.getNombrePasajero());
                p.setDniPasajero(a.getDniPasajero());
                p.setPrecio(BigDecimal.valueOf(precio));
                p.setFechaVenta(fechaHora.minusMinutes(30));
                p.setVendidoPor(ventanilla);
                p.setEstado(EstadoPasaje.VENDIDO);
                pasajeRepository.save(p);
            }
        }
    }
}