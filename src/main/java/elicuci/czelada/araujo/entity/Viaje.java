package elicuci.czelada.araujo.entity;

import elicuci.czelada.araujo.entity.enums.EstadoViaje;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name="viajes")
@NoArgsConstructor
@AllArgsConstructor
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idViaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ciudad_origen_id", nullable = false)
    private Ciudad ciudadOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ciudad_destino_id", nullable = false)
    private Ciudad ciudadDestino;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDate fechaHora;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chofer_id", nullable = false)
    private Chofer chofer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoViaje estado=EstadoViaje.PROGRAMADO;

    @OneToMany(mappedBy = "viaje", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Asiento>asientos;

    //Encomiendas que se iran en este viaje
    @OneToMany(mappedBy = "viaje", fetch = FetchType.LAZY)
    private List<Encomienda>encomiendas;
}
