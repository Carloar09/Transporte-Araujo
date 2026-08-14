package elicuci.czelada.araujo.entity;

import elicuci.czelada.araujo.entity.enums.EstadoVehiculo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="vehiculos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idVehiculo;

    @Column(nullable=false,unique=true,length=10)
    private String placa;

    @Column(nullable = false, length = 100)
    private String marca;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVehiculo estado;

    @Column(name = "ultima_revision")
    private LocalDate ultimaRevision;

    @OneToMany(mappedBy = "vehiculo",fetch = FetchType.LAZY)
    private List<Viaje>viajes;
}
