package elicuci.czelada.araujo.repository;

import elicuci.czelada.araujo.entity.Chofer;
import elicuci.czelada.araujo.entity.enums.EstadoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChoferRepository extends JpaRepository<Chofer, Long> {

 boolean existsByLicencia(String licencia);

 // Busca un chofer por su DNI
 Optional<Chofer> findByDni(String dni);

 // Verifica si existe un chofer con ese DNI
 boolean existsByDni(String dni);

 // Verifica si existe un chofer cuyo vehículo tenga esa placa
 boolean existsByVehiculo_Placa(String placa);

 // Lista todos los choferes según su estado
 List<Chofer> findByEstado(EstadoVehiculo estado);

 // Cuenta todos los choferes según su estado
 long countByEstado(EstadoVehiculo estado);

 // Verifica choferes sin vehículos asignados
 //@Query("SELECT c FROM Chofer c WHERE c.vehiculo IS NULL AND c.estado = 'ACTIVO'")
 //List<Chofer> findChoferesDisponibles();

 // Cambia 'ACTIVO' por 'OPERATIVO'
 @Query("SELECT c FROM Chofer c WHERE c.vehiculo IS NULL AND c.estado = elicuci.czelada.araujo.entity.enums.EstadoVehiculo.OPERATIVO")
 List<Chofer> findChoferesDisponibles();

 // Busca el chofer asignado a un vehículo
 @Query("SELECT c FROM Chofer c WHERE c.vehiculo.idVehiculo = :vehiculoId")
 Optional<Chofer> findByVehiculoId(@Param("vehiculoId") Long vehiculoId);
}