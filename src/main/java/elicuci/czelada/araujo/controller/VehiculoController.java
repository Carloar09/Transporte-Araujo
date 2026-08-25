package elicuci.czelada.araujo.controller;

import elicuci.czelada.araujo.entity.Vehiculo;
import elicuci.czelada.araujo.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {
    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping
    public List<Vehiculo> listar() {
        return vehiculoService.listarTodos();
    }
}