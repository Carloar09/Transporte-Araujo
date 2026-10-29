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
            java.time.LocalDate hoy = java.time.LocalDate.now();
            java.time.LocalDateTime hoyInicio = hoy.atStartOfDay();
            java.time.LocalDateTime hoyFin = hoy.plusDays(1).atStartOfDay();

            java.time.LocalDateTime ayerInicio = hoy.minusDays(1).atStartOfDay();
            java.time.LocalDateTime ayerFin = hoyInicio;

            // 1. Viajes Hoy y Ayer
            long viajesHoy = viajeRepository != null ? viajeRepository.countByFechaHoraBetween(hoyInicio, hoyFin) : 0;
            long viajesAyer = viajeRepository != null ? viajeRepository.countByFechaHoraBetween(ayerInicio, ayerFin) : 0;

            String tendenciaViajes;
            boolean tendenciaPositiva;
            if (viajesAyer == 0) {
                if (viajesHoy > 0) {
                    tendenciaViajes = "↑ +" + viajesHoy + " vs ayer";
                    tendenciaPositiva = true;
                } else {
                    tendenciaViajes = "= 0 viajes registrados hoy";
                    tendenciaPositiva = true;
                }
            } else {
                long diff = viajesHoy - viajesAyer;
                double pct = Math.abs(((double) diff / viajesAyer) * 100);
                if (diff > 0) {
                    tendenciaViajes = String.format("↑ +%d (%.0f%%) vs ayer", diff, pct);
                    tendenciaPositiva = true;
                } else if (diff < 0) {
                    tendenciaViajes = String.format("↓ %d (-%.0f%%) vs ayer", diff, pct);
                    tendenciaPositiva = false;
                } else {
                    tendenciaViajes = "= Igual que ayer (" + viajesHoy + " viajes)";
                    tendenciaPositiva = true;
                }
            }

            // 2. Pasajeros de hoy y ayer
            List<elicuci.czelada.araujo.entity.Pasaje> pasajesHoy = pasajeRepository != null
                    ? pasajeRepository.findByFechaBetween(hoyInicio, hoyFin) : Collections.emptyList();
            List<elicuci.czelada.araujo.entity.Pasaje> pasajesAyer = pasajeRepository != null
                    ? pasajeRepository.findByFechaBetween(ayerInicio, ayerFin) : Collections.emptyList();

            long totalPasajerosHoy = pasajesHoy.size();
            long totalPasajerosAyer = pasajesAyer.size();

            long cajamarcaHoy = pasajesHoy.stream()
                    .filter(p -> p.getViaje() != null && p.getViaje().getCiudadOrigen() != null
                            && p.getViaje().getCiudadOrigen().getNombre().toLowerCase().contains("cajamarca"))
                    .count();
            long celendinHoy = totalPasajerosHoy - cajamarcaHoy;

            String tendenciaPasajeros;
            if (totalPasajerosAyer == 0) {
                tendenciaPasajeros = totalPasajerosHoy > 0 ? ("+" + totalPasajerosHoy + " vs ayer") : "Sin pasajeros ayer";
            } else {
                long diffP = totalPasajerosHoy - totalPasajerosAyer;
                tendenciaPasajeros = (diffP >= 0 ? "+" : "") + diffP + " pasajeros vs ayer";
            }

            // 3. Flota
            long totalVehiculos = vehiculoRepository != null ? vehiculoRepository.count() : 0;
            long enMantenimiento = vehiculoRepository != null ? vehiculoRepository.findAll().stream()
                    .filter(v -> v.getEstado() != null && (v.getEstado().name().contains("MANTENIMIENTO") || v.getEstado().name().contains("TALLER")))
                    .count() : 0;
            long flotaDisponible = Math.max(0, totalVehiculos - enMantenimiento);

            response.put("viajesHoy", viajesHoy);
            response.put("viajesAyer", viajesAyer);
            response.put("tendenciaViajes", tendenciaViajes);
            response.put("tendenciaPositiva", tendenciaPositiva);

            response.put("flotaDisponible", flotaDisponible);
            response.put("totalFlota", totalVehiculos);
            response.put("enMantenimiento", enMantenimiento);
            response.put("enRuta", viajesHoy);

            response.put("totalPasajeros", totalPasajerosHoy);
            response.put("pasajerosCajamarca", cajamarcaHoy);
            response.put("pasajerosCelendin", celendinHoy);
            response.put("tendenciaPasajeros", tendenciaPasajeros);

            // 4. Próximas salidas de HOY
            List<Map<String, Object>> listaSalidas = Collections.emptyList();
            if (viajeRepository != null) {
                List<elicuci.czelada.araujo.entity.Viaje> salidasHoy = viajeRepository.findByFechaBetween(hoyInicio, hoyFin);
                if (salidasHoy.isEmpty()) {
                    // Si no hay salidas hoy, muestra las más recientes para no dejar la tabla vacía
                    salidasHoy = viajeRepository.findAll();
                }
                listaSalidas = salidasHoy.stream().map(viaje -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idViaje", viaje.getIdViaje() != null ? viaje.getIdViaje() : 1L);

                    String origen = (viaje.getCiudadOrigen() != null && viaje.getCiudadOrigen().getNombre() != null)
                            ? viaje.getCiudadOrigen().getNombre() : "Cajamarca";
                    String destino = (viaje.getCiudadDestino() != null && viaje.getCiudadDestino().getNombre() != null)
                            ? viaje.getCiudadDestino().getNombre() : "Celendín";
                    map.put("ruta", origen + " → " + destino);

                    String horaFormatted = "08:00 AM";
                    if (viaje.getFechaHora() != null) {
                        horaFormatted = viaje.getFechaHora().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
                    }
                    map.put("hora", horaFormatted);

                    String choferNom = "Sin Chofer";
                    String iniciales = "N/A";
                    if (viaje.getChofer() != null && viaje.getChofer().getNombreCompleto() != null && !viaje.getChofer().getNombreCompleto().isBlank()) {
                        choferNom = viaje.getChofer().getNombreCompleto();
                        iniciales = choferNom.substring(0, 1).toUpperCase();
                    }
                    map.put("chofer", choferNom);
                    map.put("iniciales", iniciales);

                    String placa = (viaje.getVehiculo() != null && viaje.getVehiculo().getPlaca() != null)
                            ? viaje.getVehiculo().getPlaca() : "---";
                    map.put("vehiculoId", placa);

                    int capacidad = viaje.getVehiculo() != null ? viaje.getVehiculo().getCapacidad() : 15;
                    long ocupados = 0;
                    if (viaje.getAsientos() != null) {
                        ocupados = viaje.getAsientos().stream()
                                .filter(a -> !a.isEsChofer() && a.getEstado() == elicuci.czelada.araujo.entity.enums.EstadoAsiento.OCUPADO)
                                .count();
                    }
                    int pct = capacidad > 0 ? (int) ((ocupados * 100) / capacidad) : 0;

                    map.put("ocupacion", ocupados + "/" + capacidad);
                    map.put("porcentaje", pct);

                    // Cálculo realista del estado según fecha y hora actual
                    java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
                    java.time.LocalDateTime horaSalida = viaje.getFechaHora();
                    String estadoCalculado = "On Time";

                    if (viaje.getEstado() == elicuci.czelada.araujo.entity.enums.EstadoViaje.CANCELADO) {
                        estadoCalculado = "Cancelado";
                    } else if (viaje.getEstado() == elicuci.czelada.araujo.entity.enums.EstadoViaje.COMPLETADO) {
                        estadoCalculado = "Finalizado";
                    } else if (horaSalida != null) {
                        if (ahora.isAfter(horaSalida.plusHours(2))) {
                            estadoCalculado = "Finalizado";
                        } else if (ahora.isAfter(horaSalida)) {
                            estadoCalculado = "En Ruta";
                        } else if (ahora.isAfter(horaSalida.minusMinutes(20)) || (capacidad > 0 && ocupados >= capacidad)) {
                            estadoCalculado = "Boarding";
                        } else {
                            estadoCalculado = "On Time";
                        }
                    } else if (capacidad > 0 && ocupados >= capacidad) {
                        estadoCalculado = "Boarding";
                    }

                    map.put("estado", estadoCalculado);
                    return map;
                }).toList();
            }

            response.put("proximasSalidas", listaSalidas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("viajesHoy", 0);
            response.put("viajesAyer", 0);
            response.put("tendenciaViajes", "0% vs ayer");
            response.put("tendenciaPositiva", true);
            response.put("flotaDisponible", 0);
            response.put("totalFlota", 0);
            response.put("enMantenimiento", 0);
            response.put("enRuta", 0);
            response.put("totalPasajeros", 0);
            response.put("pasajerosCajamarca", 0);
            response.put("pasajerosCelendin", 0);
            response.put("tendenciaPasajeros", "");
            response.put("proximasSalidas", Collections.emptyList());
            return ResponseEntity.ok(response);
        }
    }
}