package elicuci.czelada.araujo.controller;

import elicuci.czelada.araujo.repository.PasajeRepository;
import elicuci.czelada.araujo.repository.VehiculoRepository;
import elicuci.czelada.araujo.repository.ViajeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final ViajeRepository viajeRepository;
    private final VehiculoRepository vehiculoRepository;
    private final PasajeRepository pasajeRepository;

    public DashboardController(ViajeRepository viajeRepository,
                               VehiculoRepository vehiculoRepository,
                               PasajeRepository pasajeRepository) {
        this.viajeRepository = viajeRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.pasajeRepository = pasajeRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getResumen() {
        Map<String, Object> response = new HashMap<>();

        long totalViajes = viajeRepository.count();
        long totalVehiculos = vehiculoRepository.count();
        long totalPasajeros = pasajeRepository.count();

        response.put("viajesHoy", totalViajes);
        response.put("flotaDisponible", totalVehiculos);
        response.put("totalFlota", totalVehiculos);
        response.put("enMantenimiento", 0);
        response.put("enRuta", 0);
        response.put("totalPasajeros", totalPasajeros);
        response.put("pasajerosCajamarca", totalPasajeros / 2);
        response.put("pasajerosCelendin", totalPasajeros / 2);

        var listaSalidas = viajeRepository.findAll().stream().map(viaje -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idViaje", viaje.getIdViaje());

            // Extracción segura para evitar NullPointerException si la relación es nula
            String origen = viaje.getCiudadOrigen() != null ? viaje.getCiudadOrigen().getNombre() : "Cajamarca";
            String destino = viaje.getCiudadDestino() != null ? viaje.getCiudadDestino().getNombre() : "Celendín";
            map.put("ruta", origen + " - " + destino);

            map.put("hora", viaje.getFechaHora() != null ? viaje.getFechaHora().toString() : "08:00 AM");
            map.put("chofer", viaje.getChofer() != null ? viaje.getChofer().getNombreCompleto() : "Sin Chofer");
            map.put("iniciales", (viaje.getChofer() != null && viaje.getChofer().getNombreCompleto() != null)
                    ? viaje.getChofer().getNombreCompleto().substring(0, 1) : "N/A");
            map.put("vehiculoId", viaje.getVehiculo() != null ? viaje.getVehiculo().getPlaca() : "V-100");
            map.put("ocupacion", "10/15");
            map.put("porcentaje", 66);
            map.put("estado", "On Time");
            return map;
        }).toList();

        response.put("proximasSalidas", listaSalidas);

        return ResponseEntity.ok(response);
    }
}