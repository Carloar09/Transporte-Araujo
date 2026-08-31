package elicuci.czelada.araujo.service;

import elicuci.czelada.araujo.dto.ChoferRequestDTO;
import elicuci.czelada.araujo.dto.ChoferResponseDTO;
import elicuci.czelada.araujo.entity.Chofer;
import elicuci.czelada.araujo.entity.Vehiculo;
import elicuci.czelada.araujo.entity.enums.EstadoVehiculo;
import elicuci.czelada.araujo.repository.ChoferRepository;
import elicuci.czelada.araujo.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChoferService {

    private final ChoferRepository choferRepository;
    private final VehiculoRepository vehiculoRepository;

    // Lista todos los choferes
    public List<ChoferResponseDTO> listar() {
        return choferRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    // Crear chofer nuevo
    @Transactional
    public ChoferResponseDTO crear(ChoferRequestDTO request) {

        if (choferRepository.existsByDni(request.getDni())) {
            throw new RuntimeException("Ya existe un chofer con DNI: " + request.getDni());
        }
        if (choferRepository.existsByLicencia(request.getLicencia())) {
            throw new RuntimeException("Ya existe un chofer con licencia: " + request.getLicencia());
        }

        Chofer c = new Chofer();
        c.setNombreCompleto(request.getNombreCompleto());
        c.setDni(request.getDni());
        c.setLicencia(request.getLicencia());
        c.setTelefono(request.getTelefono());
        c.setEstado(request.getEstado());

        // Asignar vehículo si viene en el request
        if (request.getVehiculoId() != null) {
            Vehiculo v = vehiculoRepository.findById(request.getVehiculoId())
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
            c.setVehiculo(v);
        }

        return mapToDTO(choferRepository.save(c));
    }
    // Editar chofer
    @Transactional
    public ChoferResponseDTO editar(Long id, ChoferRequestDTO request) {

        Chofer c = choferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chofer no encontrado"));

        c.setNombreCompleto(request.getNombreCompleto());
        c.setTelefono(request.getTelefono());
        c.setEstado(request.getEstado());
        c.setLicencia(request.getLicencia());

        if (request.getVehiculoId() != null) {
            Vehiculo v = vehiculoRepository.findById(request.getVehiculoId())
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
            c.setVehiculo(v);
        } else {
            c.setVehiculo(null);
        }

        return mapToDTO(choferRepository.save(c));
    }
    // Eliminar chofer
    public void eliminar(Long id) {
        choferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chofer no encontrado"));
        choferRepository.deleteById(id);
    }

    // datos para el dashboard de flota
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", choferRepository.count());
        stats.put("operativos", choferRepository.countByEstado(EstadoVehiculo.OPERATIVO));
        stats.put("descanso", choferRepository.countByEstado(EstadoVehiculo.DESCANSO));
        stats.put("mantenimiento", choferRepository.countByEstado(EstadoVehiculo.MANTENIMIENTO));
        stats.put("taller", choferRepository.countByEstado(EstadoVehiculo.TALLER));
        return stats;
    }
    // Mapper privado
    private ChoferResponseDTO mapToDTO(Chofer c) {
        return ChoferResponseDTO.builder()
                .idChofer(c.getIdChofer())
                .nombreCompleto(c.getNombreCompleto())
                .dni(c.getDni())
                .licencia(c.getLicencia())
                .telefono(c.getTelefono())
                .estado(c.getEstado().name())
                .vehiculoPlaca(c.getVehiculo() != null
                        ? c.getVehiculo().getPlaca() : null)
                .vehiculoModelo(c.getVehiculo() != null
                        ? c.getVehiculo().getModelo() : null)
                .build();
    }
}