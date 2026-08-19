package elicuci.czelada.araujo.entity;

import elicuci.czelada.araujo.entity.enums.EstadoEncomienda;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="historial_encomiendas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEncomienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encomienda_id", nullable = false)
    private Encomienda encomienda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEncomienda estado;

    @Column(nullable = false, length = 300)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha;
}
