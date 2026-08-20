package elicuci.czelada.araujo.repository;

import elicuci.czelada.araujo.entity.Viaje;
import elicuci.czelada.araujo.entity.enums.EstadoViaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje,Long> {

    //trae los viajes programados de origen a destino y se ordena por fecha y hora de menor a mayor
    @Query("SELECT v FROM Viaje v WHERE v.ciudadOrigen.idCiudad = :origenId " +
            "AND v.ciudadDestino.idCiudad = :destinoId " +
            "AND v.estado = 'PROGRAMADO' " +
            "ORDER BY v.fechaHora ASC")
    List<Viaje> findViajesDisponibles(
            @Param("origenId") Long origenId,
            @Param("destinoId") Long destinoId);
    //te lista y ordena los viajes programamos para la ventanilla
    @Query("SELECT v FROM Viaje v WHERE v.estado = 'PROGRAMADO' " +
            "ORDER BY v.fechaHora ASC")
    List<Viaje> findViajesProgramados();
    // viajes por fecha
    @Query("SELECT v FROM Viaje v WHERE v.fechaHora BETWEEN :inicio AND :fin " +
            "ORDER BY v.fechaHora ASC")
    List<Viaje> findByFechaBetween(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    // Viajes por estado
    List<Viaje> findByEstado(EstadoViaje estado);

    // Para reportes de caja  de los viajes que se dan al dia
    @Query("SELECT v FROM Viaje v WHERE DATE(v.fechaHora) = CURRENT_DATE")
    List<Viaje> findViajesHoy();
}
