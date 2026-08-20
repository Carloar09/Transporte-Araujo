package elicuci.czelada.araujo.repository;

import elicuci.czelada.araujo.entity.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {

    // Todos los asientos de un viaje
    @Query("SELECT a FROM Asiento a WHERE a.viaje.idViaje = :viajeId ORDER BY a.numero ASC")
    List<Asiento> findByViajeId(@Param("viajeId") Long viajeId);

    // mira la entidad asiento  y verifica prdena y lsita los asietnos
    @Query("SELECT a FROM Asiento a WHERE a.viaje.idViaje = :viajeId " +
            "AND a.estado = 'LIBRE' AND a.esChofer = false ORDER BY a.numero ASC")
    List<Asiento> findAsientosLibres(@Param("viajeId") Long viajeId);

    // Cuenta los asientos libres del viaje
    @Query("SELECT COUNT(a) FROM Asiento a WHERE a.viaje.idViaje = :viajeId " +
            "AND a.estado = 'LIBRE' AND a.esChofer = false")
    long countAsientosLibres(@Param("viajeId") Long viajeId);

    // Cuenta los asientos ocupados
    @Query("SELECT COUNT(a) FROM Asiento a WHERE a.viaje.idViaje = :viajeId " +
            "AND a.estado = 'OCUPADO' AND a.esChofer = false")
    long countAsientosOcupados(@Param("viajeId") Long viajeId);

    // Buscar asiento en especifico  de un viaje
    @Query("SELECT a FROM Asiento a WHERE a.viaje.idViaje = :viajeId AND a.numero = :numero")
    Optional<Asiento> findByViajeIdAndNumero(
            @Param("viajeId") Long viajeId,
            @Param("numero") Integer numero);
}
