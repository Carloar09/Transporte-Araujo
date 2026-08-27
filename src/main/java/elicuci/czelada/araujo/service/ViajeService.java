package elicuci.czelada.araujo.service;

import elicuci.czelada.araujo.dto.ViajeRequestDTO;
import elicuci.czelada.araujo.dto.ViajeResponseDTO;
import elicuci.czelada.araujo.entity.Asiento;
import elicuci.czelada.araujo.entity.Chofer;
import elicuci.czelada.araujo.entity.Ciudad;
import elicuci.czelada.araujo.entity.Vehiculo;
import elicuci.czelada.araujo.entity.Viaje;
import elicuci.czelada.araujo.entity.enums.EstadoAsiento;
import elicuci.czelada.araujo.entity.enums.EstadoViaje;
import elicuci.czelada.araujo.repository.ChoferRepository;
import elicuci.czelada.araujo.repository.CiudadRepository;
import elicuci.czelada.araujo.repository.VehiculoRepository;
import elicuci.czelada.araujo.repository.ViajeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ViajeService {

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private CiudadRepository ciudadRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ChoferRepository choferRepository;

    @Transactional(readOnly = true)
    public List<ViajeResponseDTO> listarProgramados() {
        log.info("Listando todos los viajes programados");
        return viajeRepository.findViajesProgramados().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ViajeResponseDTO> buscarDisponibles(Long origenId, Long destinoId) {
        log.info("Buscando viajes desde origen ID {} hacia destino ID {}", origenId, destinoId);
        return viajeRepository.findViajesDisponibles(origenId, destinoId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
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

        // Generación automática de asientos según capacidad del vehículo
        List<Asiento> asientos = new ArrayList<>();
        for (int i = 1; i <= vehiculo.getCapacidad(); i++) {
            Asiento asiento = new Asiento();
            asiento.setViaje(viaje);
            asiento.setNumero(i);
            asiento.setEstado(EstadoAsiento.LIBRE);
            asiento.setEsChofer(false);
            asientos.add(asiento);
        }
        viaje.setAsientos(asientos);

        Viaje guardado = viajeRepository.save(viaje);
        log.info("Viaje creado exitosamente con ID: {} y {} asientos generados", guardado.getIdViaje(), asientos.size());
        return mapToResponseDTO(guardado);
    }

    @Transactional
    public ViajeResponseDTO cambiarEstado(Long id, EstadoViaje nuevoEstado) {
        Viaje viaje = viajeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado con ID: " + id));

        viaje.setEstado(nuevoEstado);
        Viaje actualizado = viajeRepository.save(viaje);
        return mapToResponseDTO(actualizado);
    }

    private ViajeResponseDTO mapToResponseDTO(Viaje viaje) {
        int totalAsientos = viaje.getAsientos() != null ? viaje.getAsientos().size() : 0;
        long libres = viaje.getAsientos() != null ?
                viaje.getAsientos().stream().filter(a -> a.getEstado() == EstadoAsiento.LIBRE).count() : 0;
        long ocupados = totalAsientos - libres;

        return ViajeResponseDTO.builder()
                .idViaje(viaje.getIdViaje())
                .ciudadOrigen(viaje.getCiudadOrigen() != null ? viaje.getCiudadOrigen().getNombre() : "")
                .ciudadDestino(viaje.getCiudadDestino() != null ? viaje.getCiudadDestino().getNombre() : "")
                .fechaHora(viaje.getFechaHora() != null ? viaje.getFechaHora().toString() : "")
                .precio(viaje.getPrecio())
                .placa(viaje.getVehiculo() != null ? viaje.getVehiculo().getPlaca() : "")
                .choferNombre(viaje.getChofer() != null ? viaje.getChofer().getNombreCompleto() : "")
                .estado(viaje.getEstado() != null ? viaje.getEstado().name() : "")
                .totalAsientos(totalAsientos)
                .asientosLibres(libres)
                .asientosOcupados(ocupados)
                .build();
    }
}