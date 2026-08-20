package elicuci.czelada.araujo.repository;

import elicuci.czelada.araujo.entity.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CiudadRepository extends JpaRepository<Ciudad,Long> {
   //BUSCA UNA CIDUDAD POR NOMBRE
    Optional<Ciudad> findByNombre(String nombre);
    // LISTA TODAS LAS CIUDADES ACTIVAS
    List<Ciudad> findByActivaTrue();
}
