package elicuci.czelada.araujo.repository;

import elicuci.czelada.araujo.entity.Vehiculo;
import elicuci.czelada.araujo.entity.enums.EstadoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    // BUSCA VEHICULO POR PLACA
    Optional<Vehiculo> findByPlaca(String placa);
    // PREGUNTA SI ESA PLACA EXISTE REU O FALSE
    boolean existsByPlaca(String placa);
    // LSITA TODOS LOS VEHICULOS SEGUN LOS ESTADOS QUE ESTA EN EL ENUM
    List<Vehiculo> findByEstado(EstadoVehiculo estado);
    // CUENTA LOS VEHICULOS DE UN ESTADO
    long countByEstado(EstadoVehiculo estado);
}
