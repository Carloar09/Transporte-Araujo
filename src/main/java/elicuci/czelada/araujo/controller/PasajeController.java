package elicuci.czelada.araujo.controller;

import elicuci.czelada.araujo.dto.PasajeRequestDTO;
import elicuci.czelada.araujo.dto.PasajeResponseDTO;
import elicuci.czelada.araujo.service.PasajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pasajes")
public class PasajeController {

    @Autowired
    private PasajeService pasajeService;

    @PostMapping
    public ResponseEntity<PasajeResponseDTO> venderPasaje(@Valid @RequestBody PasajeRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pasajeService.venderPasaje(request));
    }

    @GetMapping("/manifiesto/{viajeId}")
    public ResponseEntity<List<PasajeResponseDTO>> obtenerManifiestoViaje(@PathVariable Long viajeId) {
        return ResponseEntity.ok(pasajeService.obtenerManifiestoViaje(viajeId));
    }

    @PatchMapping("/{id}/anular") //no se si por comodidad ponemos al reves xd: /anular/{id}
    public ResponseEntity<PasajeResponseDTO> anularPasaje(@PathVariable Long id) {
        return ResponseEntity.ok(pasajeService.anularPasaje(id));
    }
}