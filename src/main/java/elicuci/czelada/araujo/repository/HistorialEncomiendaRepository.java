package elicuci.czelada.araujo.repository;

import elicuci.czelada.araujo.entity.HistorialEncomienda;
import elicuci.czelada.araujo.entity.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialEncomiendaRepository extends JpaRepository<HistorialEncomienda, Long> {

    @Query("SELECT h FROM HistorialEncomienda h WHERE h.encomienda.idEncomienda = :encomiendaId " +
            "ORDER BY h.fecha ASC")
    List<HistorialEncomienda> findByEncomiendaId(@Param("encomiendaId") Long encomiendaId);
}
