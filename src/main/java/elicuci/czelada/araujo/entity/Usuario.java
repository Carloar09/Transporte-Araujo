package elicuci.czelada.araujo.entity;

import elicuci.czelada.araujo.entity.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable=false,unique=true,length=8)
    private String dni;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false,length=50)
    private String nombreCompleto;

    @Column(length=11)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private RolUsuario rol;

    @Column(nullable=false)
    private boolean activo=true;

}
