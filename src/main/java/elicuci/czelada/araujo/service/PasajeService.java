package elicuci.czelada.araujo.service;

import elicuci.czelada.araujo.dto.PasajeRequestDTO;
import elicuci.czelada.araujo.dto.PasajeResponseDTO;
import elicuci.czelada.araujo.entity.Asiento;
import elicuci.czelada.araujo.entity.Pasaje;
import elicuci.czelada.araujo.entity.Usuario;
import elicuci.czelada.araujo.entity.Viaje;
import elicuci.czelada.araujo.entity.enums.EstadoAsiento;
import elicuci.czelada.araujo.entity.enums.EstadoPasaje;
import elicuci.czelada.araujo.repository.AsientoRepository;
import elicuci.czelada.araujo.repository.PasajeRepository;
import elicuci.czelada.araujo.repository.UsuarioRepository;
import elicuci.czelada.araujo.repository.ViajeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PasajeService {

    @Autowired
    private PasajeRepository pasajeRepository;

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public PasajeResponseDTO venderPasaje(PasajeRequestDTO dto) {
        log.info("Procesando venta de pasaje para asiento N° {} en viaje ID: {}", dto.getNumeroAsiento(), dto.getViajeId());

        Viaje viaje = viajeRepository.findById(dto.getViajeId())
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado con ID: " + dto.getViajeId()));

        Asiento asiento = asientoRepository.findByViajeIdAndNumero(dto.getViajeId(), dto.getNumeroAsiento())
                .orElseThrow(() -> new RuntimeException("El asiento N° " + dto.getNumeroAsiento() + " no existe en este viaje"));

        if (asiento.isEsChofer()) {
            throw new RuntimeException("El asiento seleccionado corresponde al chofer y no se puede vender");
        }

        if (asiento.getEstado() != EstadoAsiento.LIBRE) {
            throw new RuntimeException("El asiento N° " + dto.getNumeroAsiento() + " ya se encuentra " + asiento.getEstado());
        }

        Usuario vendedor = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario vendedor no encontrado"));

        // Marcar asiento como OCUPADO
        asiento.setEstado(EstadoAsiento.OCUPADO);
        asientoRepository.save(asiento);

        // Registrar Pasaje
        Pasaje pasaje = new Pasaje();
        pasaje.setViaje(viaje);
        pasaje.setAsiento(asiento);
        pasaje.setNombrePasajero(dto.getNombrePasajero());
        pasaje.setDniPasajero(dto.getDniPasajero());
        pasaje.setPrecio(dto.getPrecio());
        pasaje.setFechaVenta(LocalDate.now());
        pasaje.setVendidoPor(vendedor);
        pasaje.setEstado(EstadoPasaje.VENDIDO);

        Pasaje guardado = pasajeRepository.save(pasaje);
        return mapToResponseDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<PasajeResponseDTO> obtenerManifiestoViaje(Long viajeId) {
        log.info("Generando manifiesto de pasajeros para viaje ID: {}", viajeId);
        return pasajeRepository.findByViajeId(viajeId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PasajeResponseDTO anularPasaje(Long pasajeId) {
        log.info("Anulando pasaje con ID: {}", pasajeId);
        Pasaje pasaje = pasajeRepository.findById(pasajeId)
                .orElseThrow(() -> new RuntimeException("Pasaje no encontrado con ID: " + pasajeId));

        if (pasaje.getEstado() == EstadoPasaje.ANULADO) {
            throw new RuntimeException("El pasaje ya fue anulado previamente");
        }

        pasaje.setEstado(EstadoPasaje.ANULADO);

        // Liberar el asiento nuevamente
        Asiento asiento = pasaje.getAsiento();
        if (asiento != null) {
            asiento.setEstado(EstadoAsiento.LIBRE);
            asientoRepository.save(asiento);
        }

        Pasaje actualizado = pasajeRepository.save(pasaje);
        return mapToResponseDTO(actualizado);
    }

    private PasajeResponseDTO mapToResponseDTO(Pasaje pasaje) {
        return PasajeResponseDTO.builder()
                .idPasaje(pasaje.getIdPasaje())
                .numeroAsiento(pasaje.getAsiento() != null ? pasaje.getAsiento().getNumero() : null)
                .nombrePasajero(pasaje.getNombrePasajero())
                .dniPasajero(pasaje.getDniPasajero())
                .ciudadOrigen(pasaje.getViaje() != null && pasaje.getViaje().getCiudadOrigen() != null ? pasaje.getViaje().getCiudadOrigen().getNombre() : "")
                .ciudadDestino(pasaje.getViaje() != null && pasaje.getViaje().getCiudadDestino() != null ? pasaje.getViaje().getCiudadDestino().getNombre() : "")
                .fechaHora(pasaje.getViaje() != null && pasaje.getViaje().getFechaHora() != null ? pasaje.getViaje().getFechaHora().toString() : "")
                .precio(pasaje.getPrecio())
                .estado(pasaje.getEstado() != null ? pasaje.getEstado().name() : "")
                .vendidoPor(pasaje.getVendidoPor() != null ? pasaje.getVendidoPor().getNombreCompleto() : "")
                .fechaVenta(pasaje.getFechaVenta() != null ? pasaje.getFechaVenta().toString() : "")
                .build();
    }
}