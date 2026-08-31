package elicuci.czelada.araujo.entity;

import elicuci.czelada.araujo.entity.enums.EstadoChofer;
import elicuci.czelada.araujo.entity.enums.EstadoVehiculo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="choferes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chofer {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idChofer;

    @Column(nullable = false, length = 150)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(nullable = false, unique = true, length = 20)
    private String licencia;

    @Column(length = 15)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVehiculo estado;

    //asignaoms asi por si el vehiculo acutal esta en decanso entonces puede ser null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = true)
    private Vehiculo vehiculo;

}
