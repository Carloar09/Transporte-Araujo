package elicuci.czelada.araujo.controller;

import elicuci.czelada.araujo.dto.VehiculoRequestDTO;
import elicuci.czelada.araujo.dto.VehiculoResponseDTO;
import elicuci.czelada.araujo.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping
    public ResponseEntity<List<VehiculoResponseDTO>> listar() {
        return ResponseEntity.ok(vehiculoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculoService.obtenerPorId(id));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> obtenerDashboard() {
        return ResponseEntity.ok(vehiculoService.obtenerDashboard());
    }

    @PostMapping
    public ResponseEntity<VehiculoResponseDTO> crear(@Valid @RequestBody VehiculoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehiculoRequestDTO request) {
        return ResponseEntity.ok(vehiculoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}