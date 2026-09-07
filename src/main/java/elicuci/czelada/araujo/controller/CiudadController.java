package elicuci.czelada.araujo.controller;

import elicuci.czelada.araujo.entity.Ciudad;
import elicuci.czelada.araujo.repository.CiudadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ciudades")
@RequiredArgsConstructor
public class CiudadController {

    private final CiudadRepository ciudadRepository;

    @GetMapping
    public ResponseEntity<List<Ciudad>> listar() {
        return ResponseEntity.ok(ciudadRepository.findByActivaTrue());
    }

    @PostMapping
    public ResponseEntity<Ciudad> crear(@RequestBody Ciudad ciudad) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ciudadRepository.save(ciudad));
    }
}