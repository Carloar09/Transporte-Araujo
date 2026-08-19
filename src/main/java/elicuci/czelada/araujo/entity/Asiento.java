package elicuci.czelada.araujo.entity;

import elicuci.czelada.araujo.entity.enums.EstadoAsiento;
import elicuci.czelada.araujo.entity.enums.EstadoPasaje;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="asientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAsieto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="viaje_id", nullable=false)
    private Viaje viaje;

    //capacidad del vehiculo
    @Column(nullable = false)
    private Integer numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAsiento estado=EstadoAsiento.LIBRE;

    //ponemos en true para poder ver el asiento del chofer
    @Column(name = "es_chofer", nullable = false)
    private boolean esChofer=false;
}
