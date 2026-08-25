package elicuci.czelada.araujo.service;

import elicuci.czelada.araujo.entity.Vehiculo;
import elicuci.czelada.araujo.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VehiculoService {
    @Autowired
    private VehiculoRepository vehiculoRepository;

    public List<Vehiculo> listarTodos() {
        return vehiculoRepository.findAll();
    }
}