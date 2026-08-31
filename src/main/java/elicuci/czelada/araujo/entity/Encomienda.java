package elicuci.czelada.araujo.entity;

import elicuci.czelada.araujo.entity.enums.EstadoEncomienda;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="encomiendas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Encomienda {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idEncomienda;

    @Column(name = "codigo_seguimiento", nullable = false, unique = true, length = 20)
    private String codigoSeguimiento;

    @Column(name = "remitente_nombre", nullable = false, length = 150)
    private String remitenteNombre;

    @Column(name = "remitente_dni", length = 11)
    private String remitenteDni;

    @Column(name = "remitente_telefono", length = 15)
    private String remitenteTelefono;

    @Column(name = "destinatario_nombre", nullable = false, length = 150)
    private String destinatarioNombre;

    @Column(name = "destinatario_dni", length = 11)
    private String destinatarioDni;

    @Column(name = "destinatario_telefono", length = 15)
    private String destinatarioTelefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ciudad_origen_id", nullable = false)
    private Ciudad ciudadOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ciudad_destino_id", nullable = false)
    private Ciudad ciudadDestino;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Double peso;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEncomienda estado=EstadoEncomienda.RECEPCIONADO;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por_id", nullable = false)
    private Usuario registradoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viaje_id", nullable = true)
    private Viaje viaje;

    //seguimiento para el cliente, historial
    @OneToMany(mappedBy = "encomienda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistorialEncomienda> historial;
}
