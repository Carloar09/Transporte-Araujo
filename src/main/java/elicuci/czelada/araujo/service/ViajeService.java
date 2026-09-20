package elicuci.czelada.araujo.service;

import elicuci.czelada.araujo.dto.*;
import elicuci.czelada.araujo.entity.*;
import elicuci.czelada.araujo.entity.enums.EstadoAsiento;
import elicuci.czelada.araujo.entity.enums.EstadoViaje;
import elicuci.czelada.araujo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViajeService {

    private final ViajeRepository viajeRepository;
    private final CiudadRepository ciudadRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ChoferRepository choferRepository;
    private final AsientoRepository asientoRepository;

    @Value("${peruapi.token:}")
    private String apiToken;

    @Value("${peruapi.url:}")
    private String apiUrl;

    @Transactional(readOnly = true)
    public List<ViajeResponseDTO> listarProgramados() {
        log.info("Listando todos los viajes programados");
        try {
            List<Viaje> viajes = viajeRepository.findAll();
            return viajes.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al listar viajes programados: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public List<ViajeResponseDTO> buscarDisponibles(Long origenId, Long destinoId) {
        log.info("Buscando viajes desde origen ID {} hacia destino ID {}", origenId, destinoId);
        try {
            return viajeRepository.findViajesDisponibles(origenId, destinoId).stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al buscar viajes disponibles: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public ViajeResponseDTO obtenerPorId(Long id) {
        Viaje viaje = viajeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado con ID: " + id));
        return mapToResponseDTO(viaje);
    }

    @Transactional
    public ViajeResponseDTO crear(ViajeRequestDTO dto) {
        if (dto.getCiudadOrigenId().equals(dto.getCiudadDestinoId())) {
            throw new RuntimeException("La ciudad de origen y destino no pueden ser la misma");
        }

        Ciudad origen = ciudadRepository.findById(dto.getCiudadOrigenId())
                .orElseThrow(() -> new RuntimeException("Ciudad de origen no encontrada"));
        Ciudad destino = ciudadRepository.findById(dto.getCiudadDestinoId())
                .orElseThrow(() -> new RuntimeException("Ciudad de destino no encontrada"));
        Vehiculo vehiculo = vehiculoRepository.findById(dto.getVehiculoId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        Chofer chofer = choferRepository.findById(dto.getChoferId())
                .orElseThrow(() -> new RuntimeException("Chofer no encontrado"));

        Viaje viaje = new Viaje();
        viaje.setCiudadOrigen(origen);
        viaje.setCiudadDestino(destino);
        viaje.setFechaHora(dto.getFechaHora().toLocalDate());
        viaje.setPrecio(dto.getPrecio());
        viaje.setVehiculo(vehiculo);
        viaje.setChofer(chofer);
        viaje.setEstado(EstadoViaje.PROGRAMADO);

        Viaje viajeGuardado = viajeRepository.save(viaje);

        List<Asiento> asientos = new ArrayList<>();
        Asiento asientoChofer = new Asiento();
        asientoChofer.setViaje(viajeGuardado);
        asientoChofer.setNumero(0);
        asientoChofer.setEsChofer(true);
        asientoChofer.setEstado(EstadoAsiento.OCUPADO);
        asientos.add(asientoChofer);

        for (int i = 1; i <= vehiculo.getCapacidad(); i++) {
            Asiento asiento = new Asiento();
            asiento.setViaje(viajeGuardado);
            asiento.setNumero(i);
            asiento.setEsChofer(false);
            asiento.setEstado(EstadoAsiento.LIBRE);
            asientos.add(asiento);
        }
        asientoRepository.saveAll(asientos);
        return mapToResponseDTO(viajeGuardado);
    }

    @Transactional
    public ViajeResponseDTO cambiarEstado(Long id, EstadoViaje nuevoEstado) {
        Viaje viaje = viajeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado con ID: " + id));

        viaje.setEstado(nuevoEstado);
        Viaje actualizado = viajeRepository.save(viaje);
        return mapToResponseDTO(actualizado);
    }

    @Transactional(readOnly = true)
    public List<AsientoDTO> getCroquis(Long viajeId) {
        viajeRepository.findById(viajeId)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));

        return asientoRepository.findByViajeId(viajeId)
                .stream()
                .map(a -> AsientoDTO.builder()
                        .idAsiento(a.getIdAsieto())
                        .numero(a.getNumero())
                        .estado(a.getEstado().name())
                        .esChofer(a.isEsChofer())
                        // 👈 Se añaden los datos mapeados desde la entidad Asiento
                        .dniPasajero(a.getDniPasajero())
                        .nombrePasajero(a.getNombrePasajero())
                        .precio(a.getPrecio())
                        .build())
                .collect(Collectors.toList());
    }

    public PeruApiDniDTO consultarDni(String dni) {
        log.info("Consultando DNI real en API externa: {}", dni);
        if (apiUrl == null || apiUrl.isBlank()) {
            PeruApiDniDTO errorDto = new PeruApiDniDTO();
            errorDto.setNumeroDocumento(dni);
            errorDto.setNombreCompleto("");
            return errorDto;
        }

        String baseUrl = apiUrl.endsWith("/") ? apiUrl : apiUrl + "/";
        String url = baseUrl + "api/dni/" + dni + "?summary=0&plan=0";

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", apiToken);
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<PeruApiDniDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, PeruApiDniDTO.class
            );

            PeruApiDniDTO dto = response.getBody();
            if (dto != null && (dto.getNombreCompleto() == null || dto.getNombreCompleto().isBlank())) {
                String completo = String.format("%s %s %s",
                        dto.getNombres() != null ? dto.getNombres() : "",
                        dto.getApellidoPaterno() != null ? dto.getApellidoPaterno() : "",
                        dto.getApellidoMaterno() != null ? dto.getApellidoMaterno() : ""
                ).trim();
                dto.setNombreCompleto(completo);
            }
            return dto;
        } catch (Exception e) {
            log.error("Error al consultar la API externa de DNI para {}: {}", dni, e.getMessage());
            PeruApiDniDTO errorDto = new PeruApiDniDTO();
            errorDto.setNumeroDocumento(dni);
            errorDto.setNombreCompleto("");
            return errorDto;
        }
    }

    public PeruApiRucDTO consultarRuc(String ruc) {
        log.info("Consultando RUC real en API externa: {}", ruc);
        if (apiUrl == null || apiUrl.isBlank()) {
            PeruApiRucDTO errorDto = new PeruApiRucDTO();
            errorDto.setRuc(ruc);
            errorDto.setRazonSocial("");
            return errorDto;
        }

        String baseUrl = apiUrl.endsWith("/") ? apiUrl : apiUrl + "/";
        String url = baseUrl + "api/ruc/" + ruc + "?summary=0&plan=0";

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", apiToken);
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<PeruApiRucDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, PeruApiRucDTO.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error al consultar la API externa de RUC: {}", e.getMessage());
            PeruApiRucDTO errorDto = new PeruApiRucDTO();
            errorDto.setRuc(ruc);
            errorDto.setRazonSocial("");
            return errorDto;
        }
    }

    private ViajeResponseDTO mapToResponseDTO(Viaje viaje) {
        int totalAsientos = 0;
        String placa = "S/P";
        if (viaje.getVehiculo() != null) {
            totalAsientos = viaje.getVehiculo().getCapacidad();
            if (viaje.getVehiculo().getPlaca() != null) {
                placa = viaje.getVehiculo().getPlaca();
            }
        }

        long libres = totalAsientos;
        if (viaje.getAsientos() != null && !viaje.getAsientos().isEmpty()) {
            libres = viaje.getAsientos().stream()
                    .filter(a -> a != null && a.getEstado() == EstadoAsiento.LIBRE)
                    .count();
        }
        long ocupados = Math.max(0, totalAsientos - libres);

        String origen = (viaje.getCiudadOrigen() != null) ? viaje.getCiudadOrigen().getNombre() : "Sin Origen";
        String destino = (viaje.getCiudadDestino() != null) ? viaje.getCiudadDestino().getNombre() : "Sin Destino";
        String chofer = (viaje.getChofer() != null) ? viaje.getChofer().getNombreCompleto() : "Sin Chofer";
        String estadoStr = (viaje.getEstado() != null) ? viaje.getEstado().name() : "PROGRAMADO";

        return ViajeResponseDTO.builder()
                .idViaje(viaje.getIdViaje())
                .ciudadOrigen(origen)
                .ciudadDestino(destino)
                .fechaHora(viaje.getFechaHora() != null ? viaje.getFechaHora().toString() : "")
                .precio(viaje.getPrecio())
                .placa(placa)
                .choferNombre(chofer)
                .estado(estadoStr)
                .totalAsientos(totalAsientos)
                .asientosLibres(libres)
                .asientosOcupados(ocupados)
                .build();
    }
}