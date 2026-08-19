package elicuci.czelada.araujo.entity;

import elicuci.czelada.araujo.entity.enums.EstadoPasaje;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="pasajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pasaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPasaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asiento_id", nullable = false)
    private Asiento asiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

    @Column(name = "nombre_pasajero", nullable = false, length = 150)
    private String nombrePasajero;

    @Column(name = "dni_pasajero", nullable = false, length = 8)
    private String dniPasajero;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDate fechaVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendido_por_id", nullable = false)
    private Usuario vendidoPor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPasaje estado=EstadoPasaje.VENDIDO;
}
