package elicuci.czelada.araujo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="ciudades")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ciudad {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idCiudad;

    @Column(nullable=false,length=50)
    private String nombre;

    @Column(length=50)
    private String provincia;

    @Column(nullable = false)
    private boolean activa=true;
}
