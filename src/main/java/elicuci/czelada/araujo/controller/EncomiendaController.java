package elicuci.czelada.araujo.controller;

import elicuci.czelada.araujo.dto.EncomiendaRequestDTO;
import elicuci.czelada.araujo.dto.EncomiendaResponseDTO;
import elicuci.czelada.araujo.entity.enums.EstadoEncomienda;
import elicuci.czelada.araujo.service.EncomiendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/encomiendas")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class EncomiendaController {

    private final EncomiendaService encomiendaService;

    // GET /api/v1/encomiendas
    @GetMapping
    public ResponseEntity<List<EncomiendaResponseDTO>> listar() {
        return ResponseEntity.ok(encomiendaService.listarTodas());
    }

    // GET /api/v1/encomiendas/seguimiento/RX-2026-000001
    @GetMapping("/seguimiento/{codigo}")
    public ResponseEntity<EncomiendaResponseDTO> getSeguimiento(
            @PathVariable String codigo) {
        return ResponseEntity.ok(encomiendaService.getSeguimiento(codigo));
    }

    // GET /api/v1/encomiendas/mis-encomiendas/12345678
    @GetMapping("/mis-encomiendas/{dni}")
    public ResponseEntity<List<EncomiendaResponseDTO>> getMisEncomiendas(
            @PathVariable String dni) {
        return ResponseEntity.ok(encomiendaService.getMisEncomiendas(dni));
    }

    // POST /api/v1/encomiendas
    @PostMapping
    public ResponseEntity<EncomiendaResponseDTO> registrar(
            @Valid @RequestBody EncomiendaRequestDTO request,
            Authentication authentication) {
        String dniRegistrador = authentication != null ? authentication.getName() : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(encomiendaService.registrar(request, dniRegistrador));
    }

    // PUT /api/v1/encomiendas/{id}/estado
    @PutMapping("/{id}/estado")
    public ResponseEntity<EncomiendaResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        EstadoEncomienda estado = EstadoEncomienda.valueOf(body.get("estado"));
        String descripcion = body.getOrDefault("descripcion", "Actualización de estado");

        return ResponseEntity.ok(
                encomiendaService.actualizarEstado(id, estado, descripcion));
    }

    // PUT /api/v1/encomiendas/{id}/asignar-viaje/{viajeId}
    @PutMapping("/{id}/asignar-viaje/{viajeId}")
    public ResponseEntity<EncomiendaResponseDTO> asignarViaje(
            @PathVariable Long id,
            @PathVariable Long viajeId) {
        return ResponseEntity.ok(encomiendaService.asignarViaje(id, viajeId));
    }
}