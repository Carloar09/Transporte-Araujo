package elicuci.czelada.araujo.repository;

import elicuci.czelada.araujo.entity.Usuario;
import elicuci.czelada.araujo.entity.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    // inisio de secion por dni
    Optional<Usuario> findByDni(String dni);
    // se va a verificar si existe
    boolean existsByDni(String dni);
    // se va a listar por rolcitos
    List<Usuario> findAllByRol(RolUsuario rol);
}
