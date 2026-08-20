package elicuci.czelada.araujo.repository;

import elicuci.czelada.araujo.entity.Encomienda;
import elicuci.czelada.araujo.entity.Viaje;
import elicuci.czelada.araujo.entity.enums.EstadoEncomienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EncomiendaRepository extends JpaRepository<Encomienda, Long> {

    // Buscar por código de seguimiento para el cliente
    Optional<Encomienda> findByCodigoSeguimiento(String codigoSeguimiento);

    // Encomiendas de un cliente por su DNI para el portal del cliente
    @Query("SELECT e FROM Encomienda e WHERE e.remitenteDni = :dni " +
            "OR e.destinatarioDni = :dni ORDER BY e.fechaRegistro DESC")
    List<Encomienda> findByClienteDni(@Param("dni") String dni);

    // Encomiendas por estado
    List<Encomienda> findByEstado(EstadoEncomienda estado);

    // Encomiendas por estado o conteo
    long countByEstado(EstadoEncomienda estado);

    // Encomiendas registradas en un rango de fechas PARA reportes
    @Query("SELECT e FROM Encomienda e WHERE e.fechaRegistro BETWEEN :inicio AND :fin " +
            "ORDER BY e.fechaRegistro DESC")
    List<Encomienda> findByFechaBetween(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    // Total recaudado por encomiendas en rango de fechas
    @Query("SELECT SUM(e.precio) FROM Encomienda e WHERE e.fechaRegistro BETWEEN :inicio AND :fin")
    Double sumTotalEncomiendas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    // Último número de encomienda para generar el código RX-2026-XXXXX
    @Query("SELECT COUNT(e) FROM Encomienda e")
    long countTotal();
}
