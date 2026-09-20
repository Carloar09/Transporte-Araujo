package elicuci.czelada.araujo.controller;

import elicuci.czelada.araujo.repository.PasajeRepository;
import elicuci.czelada.araujo.repository.VehiculoRepository;
import elicuci.czelada.araujo.repository.ViajeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
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

        try {
            long totalViajes = viajeRepository != null ? viajeRepository.count() : 0;
            long totalVehiculos = vehiculoRepository != null ? vehiculoRepository.count() : 0;
            long totalPasajeros = pasajeRepository != null ? pasajeRepository.count() : 0;

            response.put("viajesHoy", totalViajes);
            response.put("flotaDisponible", totalVehiculos);
            response.put("totalFlota", totalVehiculos);
            response.put("enMantenimiento", 0);
            response.put("enRuta", 0);
            response.put("totalPasajeros", totalPasajeros);
            response.put("pasajerosCajamarca", totalPasajeros / 2);
            response.put("pasajerosCelendin", totalPasajeros - (totalPasajeros / 2));

            List<Map<String, Object>> listaSalidas = Collections.emptyList();
            if (viajeRepository != null) {
                listaSalidas = viajeRepository.findAll().stream().map(viaje -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idViaje", viaje.getIdViaje() != null ? viaje.getIdViaje() : 1L);

                    String origen = (viaje.getCiudadOrigen() != null && viaje.getCiudadOrigen().getNombre() != null)
                            ? viaje.getCiudadOrigen().getNombre() : "Cajamarca";
                    String destino = (viaje.getCiudadDestino() != null && viaje.getCiudadDestino().getNombre() != null)
                            ? viaje.getCiudadDestino().getNombre() : "Celendín";
                    map.put("ruta", origen + " - " + destino);

                    map.put("hora", viaje.getFechaHora() != null ? viaje.getFechaHora().toString() : "08:00 AM");

                    String choferNom = "Sin Chofer";
                    String iniciales = "N/A";
                    if (viaje.getChofer() != null && viaje.getChofer().getNombreCompleto() != null && !viaje.getChofer().getNombreCompleto().isBlank()) {
                        choferNom = viaje.getChofer().getNombreCompleto();
                        iniciales = choferNom.substring(0, 1).toUpperCase();
                    }
                    map.put("chofer", choferNom);
                    map.put("iniciales", iniciales);

                    String placa = (viaje.getVehiculo() != null && viaje.getVehiculo().getPlaca() != null)
                            ? viaje.getVehiculo().getPlaca() : "V-100";
                    map.put("vehiculoId", placa);

                    map.put("ocupacion", "10/15");
                    map.put("porcentaje", 66);
                    map.put("estado", "On Time");
                    return map;
                }).toList();
            }

            response.put("proximasSalidas", listaSalidas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("viajesHoy", 0);
            response.put("flotaDisponible", 0);
            response.put("totalFlota", 0);
            response.put("enMantenimiento", 0);
            response.put("enRuta", 0);
            response.put("totalPasajeros", 0);
            response.put("pasajerosCajamarca", 0);
            response.put("pasajerosCelendin", 0);
            response.put("proximasSalidas", Collections.emptyList());
            return ResponseEntity.ok(response);
        }
    }
}