package elicuci.czelada.araujo.controller;

import elicuci.czelada.araujo.dto.ChoferRequestDTO;
import elicuci.czelada.araujo.dto.ChoferResponseDTO;
import elicuci.czelada.araujo.service.ChoferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/choferes")
@RequiredArgsConstructor
public class ChoferController {

    private final ChoferService choferService;

    @GetMapping
    public ResponseEntity<List<ChoferResponseDTO>> listar() {
        return ResponseEntity.ok(choferService.listar());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(choferService.getStats());
    }

    @PostMapping
    public ResponseEntity<ChoferResponseDTO> crear(
            @Valid @RequestBody ChoferRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(choferService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChoferResponseDTO> editar(
            @PathVariable Long id,
            @Valid @RequestBody ChoferRequestDTO request) {
        return ResponseEntity.ok(choferService.editar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        choferService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}