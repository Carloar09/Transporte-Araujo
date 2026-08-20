package elicuci.czelada.araujo.repository;

import elicuci.czelada.araujo.entity.Chofer;
import elicuci.czelada.araujo.entity.enums.EstadoChofer;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChoferRepository extends JpaRepository<Chofer, Long> {
    // busca a un chofer por su nombre
    Optional<Chofer> findByDni(String dni);
   // verifica si su dni existe
    boolean existsByDni(String dni);
    // verifica si su licencia existe
    boolean existsByLicencia(String licencia);
    // verifica si su palca existe
    boolean existsByPlaca(String placa);
    // lsita a todos los choferes por su estado
    List<Chofer> findByChofer(EstadoChofer chofer);
    // cuenta a todos los choferes segun su estado
    long countByChofer(EstadoChofer chofer);
    //Verificamos choferes sin vehiculos asignados
    @Query("SELECT c FROM Chofer c WHERE c.vehiculo IS NULL AND c.estado= 'ACTIVO'")
    List<Chofer> findChoferesDisponibles();
    //verificamos choferes asignados a una combi
    @Query("SELECT c FROM Chofer c WHERE c.vehiculo.idVehiculo = :vehiculoId")
    Optional<Chofer> findByVehiculoId(@Param("vehiculoId") Long vehiculoId);
}
