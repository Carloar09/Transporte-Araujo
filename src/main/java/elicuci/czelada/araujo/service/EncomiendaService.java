package elicuci.czelada.araujo.service;

import elicuci.czelada.araujo.dto.EncomiendaRequestDTO;
import elicuci.czelada.araujo.dto.EncomiendaResponseDTO;
import elicuci.czelada.araujo.entity.Ciudad;
import elicuci.czelada.araujo.entity.Encomienda;
import elicuci.czelada.araujo.entity.HistorialEncomienda;
import elicuci.czelada.araujo.entity.Usuario;
import elicuci.czelada.araujo.entity.enums.EstadoEncomienda;
import elicuci.czelada.araujo.entity.Viaje;
import elicuci.czelada.araujo.repository.CiudadRepository;
import elicuci.czelada.araujo.repository.EncomiendaRepository;
import elicuci.czelada.araujo.repository.HistorialEncomiendaRepository;
import elicuci.czelada.araujo.repository.UsuarioRepository;
import elicuci.czelada.araujo.repository.ViajeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EncomiendaService {

    private final EncomiendaRepository encomiendaRepository;
    private final CiudadRepository ciudadRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialEncomiendaRepository historialRepository;
    private final ViajeRepository viajeRepository;

    // Registrar encomienda nueva
    @Transactional
    public EncomiendaResponseDTO registrar(EncomiendaRequestDTO request, String dniRegistrador) {

        Ciudad origen = ciudadRepository.findById(request.getCiudadOrigenId())
                .orElseThrow(() -> new RuntimeException("Ciudad origen no encontrada"));

        Ciudad destino = ciudadRepository.findById(request.getCiudadDestinoId())
                .orElseThrow(() -> new RuntimeException("Ciudad destino no encontrada"));

        Usuario registrador = null;
        if (dniRegistrador != null && !dniRegistrador.isBlank() && !"anonymousUser".equals(dniRegistrador)) {
            registrador = usuarioRepository.findByDni(dniRegistrador).orElse(null);
        }
        if (registrador == null) {
            registrador = usuarioRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }

        // Generar código único: RX-2026-000001
        String codigo = generarCodigo();

        Encomienda e = new Encomienda();
        e.setCodigoSeguimiento(codigo);
        e.setRemitenteNombre(request.getRemitenteNombre());
        e.setRemitenteDni(request.getRemitenteDni());
        e.setRemitenteTelefono(request.getRemitenteTelefono());
        e.setDestinatarioNombre(request.getDestinatarioNombre());
        e.setDestinatarioDni(request.getDestinatarioDni());
        e.setDestinatarioTelefono(request.getDestinatarioTelefono());
        e.setCiudadOrigen(origen);
        e.setCiudadDestino(destino);
        e.setDescripcion(
                request.getDescripcion() != null && !request.getDescripcion().isBlank() ? request.getDescripcion()
                        : "Paquete/Encomienda general");
        e.setPeso(request.getPeso() != null ? request.getPeso() : 1.0);
        e.setPrecio(request.getPrecio());
        e.setPagoEstado(request.getPagoEstado() != null && !request.getPagoEstado().isBlank() ? request.getPagoEstado()
                : "PAGADO");
        e.setEstado(EstadoEncomienda.RECEPCIONADO);
        e.setFechaRegistro(LocalDateTime.now());
        e.setRegistradoPor(registrador);

        if (request.getViajeId() != null) {
            viajeRepository.findById(request.getViajeId()).ifPresent(v -> {
                e.setViaje(v);
                e.setEstado(EstadoEncomienda.EN_TRANSITO);
            });
        }

        Encomienda guardada = encomiendaRepository.save(e);

        // Registrar primer historial automáticamente
        HistorialEncomienda historial = new HistorialEncomienda();
        historial.setEncomienda(guardada);
        historial.setEstado(guardada.getEstado());
        historial.setDescripcion(
                "Encomienda recepcionada en agencia " + origen.getNombre() + " (" + e.getPagoEstado() + ")");
        historial.setFecha(LocalDateTime.now());
        historialRepository.save(historial);

        return mapToDTO(guardada);
    }

    // Asignar encomienda a un viaje/combi
    @Transactional
    public EncomiendaResponseDTO asignarViaje(Long idEncomienda, Long idViaje) {
        Encomienda e = encomiendaRepository.findById(idEncomienda)
                .orElseThrow(() -> new RuntimeException("Encomienda no encontrada"));
        Viaje v = viajeRepository.findById(idViaje)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));

        e.setViaje(v);
        e.setEstado(EstadoEncomienda.EN_TRANSITO);
        encomiendaRepository.save(e);

        HistorialEncomienda historial = new HistorialEncomienda();
        historial.setEncomienda(e);
        historial.setEstado(EstadoEncomienda.EN_TRANSITO);
        String placa = v.getVehiculo() != null ? v.getVehiculo().getPlaca() : "Combi asignada";
        historial.setDescripcion("Asignado a combi " + placa + " en ruta a " + e.getCiudadDestino().getNombre());
        historial.setFecha(LocalDateTime.now());
        historialRepository.save(historial);

        return mapToDTO(e);
    }

    // Seguimiento por código (para el cliente)
    public EncomiendaResponseDTO getSeguimiento(String codigo) {
        Encomienda e = encomiendaRepository.findByCodigoSeguimiento(codigo)
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró encomienda con código: " + codigo));
        return mapToDTO(e);
    }

    // Encomiendas del cliente por DNI
    public List<EncomiendaResponseDTO> getMisEncomiendas(String dni) {
        return encomiendaRepository.findByClienteDni(dni)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Actualizar estado (EN_TRANSITO, EN_AGENCIA_DESTINO, ENTREGADO)
    @Transactional
    public EncomiendaResponseDTO actualizarEstado(Long id, EstadoEncomienda nuevoEstado, String descripcion) {
        Encomienda e = encomiendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encomienda no encontrada"));

        e.setEstado(nuevoEstado);
        encomiendaRepository.save(e);

        // Registrar en historial
        HistorialEncomienda historial = new HistorialEncomienda();
        historial.setEncomienda(e);
        historial.setEstado(nuevoEstado);
        historial.setDescripcion(descripcion);
        historial.setFecha(LocalDateTime.now());
        historialRepository.save(historial);

        return mapToDTO(e);
    }

    // Listar todas (para ventanilla/admin)
    public List<EncomiendaResponseDTO> listarTodas() {
        return encomiendaRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Generar código RX-AÑO-NÚMERO
    private String generarCodigo() {
        long total = encomiendaRepository.countTotal() + 1;
        int anio = LocalDateTime.now().getYear();
        return String.format("RX-%d-%06d", anio, total);
    }

    private EncomiendaResponseDTO mapToDTO(Encomienda e) {
        List<EncomiendaResponseDTO.HistorialDTO> historialDTO = new ArrayList<>();

        List<HistorialEncomienda> historial = historialRepository
                .findByEncomiendaId(e.getIdEncomienda());

        historialDTO = historial.stream()
                .map(h -> EncomiendaResponseDTO.HistorialDTO.builder()
                        .estado(h.getEstado().name())
                        .descripcion(h.getDescripcion())
                        .fecha(h.getFecha().toString())
                        .build())
                .collect(Collectors.toList());

        Long viajeId = e.getViaje() != null ? e.getViaje().getIdViaje() : null;
        String viajeInfo = null;
        if (e.getViaje() != null) {
            String placa = (e.getViaje().getVehiculo() != null && e.getViaje().getVehiculo().getPlaca() != null)
                    ? e.getViaje().getVehiculo().getPlaca()
                    : "Combi asignada";
            viajeInfo = "Bus: " + placa;
        }

        String nomRegistrador = e.getRegistradoPor() != null ? e.getRegistradoPor().getNombreCompleto() : "Ventanilla";

        return EncomiendaResponseDTO.builder()
                .idEncomienda(e.getIdEncomienda())
                .codigoSeguimiento(e.getCodigoSeguimiento())
                .remitenteNombre(e.getRemitenteNombre())
                .remitenteDni(e.getRemitenteDni())
                .remitenteTelefono(e.getRemitenteTelefono())
                .destinatarioNombre(e.getDestinatarioNombre())
                .destinatarioDni(e.getDestinatarioDni())
                .destinatarioTelefono(e.getDestinatarioTelefono())
                .ciudadOrigen(e.getCiudadOrigen() != null ? e.getCiudadOrigen().getNombre() : "Cajamarca")
                .ciudadDestino(e.getCiudadDestino() != null ? e.getCiudadDestino().getNombre() : "Celendín")
                .descripcion(e.getDescripcion())
                .peso(e.getPeso())
                .precio(e.getPrecio())
                .pagoEstado(e.getPagoEstado() != null ? e.getPagoEstado() : "PAGADO")
                .viajeId(viajeId)
                .viajeInfo(viajeInfo)
                .estado(e.getEstado() != null ? e.getEstado().name() : "RECEPCIONADO")
                .fechaRegistro(e.getFechaRegistro() != null ? e.getFechaRegistro().toString() : "")
                .registradoPor(nomRegistrador)
                .historial(historialDTO)
                .build();
    }
}