package elicuci.czelada.araujo.controller;

import elicuci.czelada.araujo.dto.ViajeRequestDTO;
import elicuci.czelada.araujo.dto.ViajeResponseDTO;
import elicuci.czelada.araujo.entity.enums.EstadoViaje;
import elicuci.czelada.araujo.service.ViajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/viajes")
public class ViajeController {

    @Autowired
    private ViajeService viajeService;

    @GetMapping
    public ResponseEntity<List<ViajeResponseDTO>> listarProgramados() {
        return ResponseEntity.ok(viajeService.listarProgramados());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ViajeResponseDTO>> buscarDisponibles(
            @RequestParam Long origenId,
            @RequestParam Long destinoId) {
        return ResponseEntity.ok(viajeService.buscarDisponibles(origenId, destinoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViajeResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(viajeService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ViajeResponseDTO> crear(@Valid @RequestBody ViajeRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(viajeService.crear(request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ViajeResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoViaje estado) {
        return ResponseEntity.ok(viajeService.cambiarEstado(id, estado));
    }
}