package elicuci.czelada.araujo.service;

import elicuci.czelada.araujo.dto.VehiculoRequestDTO;
import elicuci.czelada.araujo.dto.VehiculoResponseDTO;
import elicuci.czelada.araujo.entity.Vehiculo;
import elicuci.czelada.araujo.entity.enums.EstadoVehiculo;
import elicuci.czelada.araujo.repository.VehiculoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Transactional(readOnly = true)
    public List<VehiculoResponseDTO> listarTodos() {
        log.info("Consultando listado completo de vehículos");
        return vehiculoRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VehiculoResponseDTO obtenerPorId(Long id) {
        log.info("Buscando vehículo con ID: {}", id);
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + id));
        return mapToResponseDTO(vehiculo);
    }

    @Transactional
    public VehiculoResponseDTO crear(VehiculoRequestDTO dto) {
        log.info("Registrando nuevo vehículo con placa: {}", dto.getPlaca());
        if (vehiculoRepository.existsByPlaca(dto.getPlaca())) {
            throw new RuntimeException("Ya existe un vehículo registrado con la placa: " + dto.getPlaca());
        }

        Vehiculo vehiculo = new Vehiculo();
        mapDtoToEntity(dto, vehiculo);

        Vehiculo guardado = vehiculoRepository.save(vehiculo);
        return mapToResponseDTO(guardado);
    }

    @Transactional
    public VehiculoResponseDTO actualizar(Long id, VehiculoRequestDTO dto) {
        log.info("Actualizando vehículo con ID: {}", id);
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + id));

        if (!vehiculo.getPlaca().equalsIgnoreCase(dto.getPlaca()) && vehiculoRepository.existsByPlaca(dto.getPlaca())) {
            throw new RuntimeException("La placa " + dto.getPlaca() + " ya está registrada en otro vehículo");
        }

        mapDtoToEntity(dto, vehiculo);
        Vehiculo actualizado = vehiculoRepository.save(vehiculo);
        return mapToResponseDTO(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando vehículo con ID: {}", id);
        if (!vehiculoRepository.existsById(id)) {
            throw new RuntimeException("Vehículo no encontrado con ID: " + id);
        }
        vehiculoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerDashboard() {
        log.info("Generando métricas del dashboard de flota");
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalVehiculos", vehiculoRepository.count());
        metrics.put("operativos", vehiculoRepository.countByEstado(EstadoVehiculo.OPERATIVO));
        metrics.put("mantenimiento", vehiculoRepository.countByEstado(EstadoVehiculo.MANTENIMIENTO));
        metrics.put("taller", vehiculoRepository.countByEstado(EstadoVehiculo.TALLER));
        metrics.put("descanso", vehiculoRepository.countByEstado(EstadoVehiculo.DESCANSO));
        return metrics;
    }

    private void mapDtoToEntity(VehiculoRequestDTO dto, Vehiculo vehiculo) {
        vehiculo.setPlaca(dto.getPlaca());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setAnio(dto.getAnio());
        vehiculo.setCapacidad(dto.getCapacidad());
        vehiculo.setEstado(dto.getEstado());
        vehiculo.setUltimaRevision(dto.getUltimaRevision());
    }

    private VehiculoResponseDTO mapToResponseDTO(Vehiculo vehiculo) {
        return VehiculoResponseDTO.builder()
                .idVehiculo(vehiculo.getIdVehiculo())
                .placa(vehiculo.getPlaca())
                .marca(vehiculo.getMarca())
                .modelo(vehiculo.getModelo())
                .anio(vehiculo.getAnio())
                .capacidad(vehiculo.getCapacidad())
                .estado(vehiculo.getEstado() != null ? vehiculo.getEstado().name() : null)
                .ultimaRevision(vehiculo.getUltimaRevision())
                .choferAsignado("Por asignar")
                .build();
    }
}