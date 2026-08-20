package elicuci.czelada.araujo.repository;

import elicuci.czelada.araujo.entity.Pasaje;
import elicuci.czelada.araujo.entity.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PasajeRepository extends JpaRepository<Pasaje, Long> {

    // Pasajes de un viaje que sirve para el manifiesto
    @Query("SELECT p FROM Pasaje p WHERE p.viaje.idViaje = :viajeId " +
            "AND p.estado = 'VENDIDO' ORDER BY p.asiento.numero ASC")
    List<Pasaje> findByViajeId(@Param("viajeId") Long viajeId);

    // Pasajes vendidos por un cajero en un rango de fechas para sis reportes de caja
    @Query("SELECT p FROM Pasaje p WHERE p.vendidoPor.idUsuario = :usuarioId " +
            "AND p.fechaVenta BETWEEN :inicio AND :fin AND p.estado = 'VENDIDO'")
    List<Pasaje> findByVendidoPorAndFecha(
            @Param("usuarioId") Long usuarioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    // Total recaudado por pasajes en un rango de fechas
    @Query("SELECT SUM(p.precio) FROM Pasaje p WHERE p.fechaVenta BETWEEN :inicio AND :fin " +
            "AND p.estado = 'VENDIDO'")
    Double sumTotalPasajesByFecha(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);


    // Todos los pasajes en rango de fechas para reporte general
    @Query("SELECT p FROM Pasaje p WHERE p.fechaVenta BETWEEN :inicio AND :fin " +
            "ORDER BY p.fechaVenta DESC")
    List<Pasaje> findByFechaBetween(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

}
