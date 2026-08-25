package elicuci.czelada.araujo.controller;

import elicuci.czelada.araujo.dto.PeruApiDniDTO;
import elicuci.czelada.araujo.dto.PeruApiRucDTO;
import elicuci.czelada.araujo.service.PeruApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/consulta")
@RequiredArgsConstructor
public class PeruApiController {

    private final PeruApiService peruApiService;

    @GetMapping("/dni/{dni}")
    public ResponseEntity<PeruApiDniDTO> consultarDni(@PathVariable String dni) {
        return ResponseEntity.ok(peruApiService.consultarDni(dni));
    }

    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<PeruApiRucDTO> consultarRuc(@PathVariable String ruc) {
        return ResponseEntity.ok(peruApiService.consultarRuc(ruc));
    }
}
