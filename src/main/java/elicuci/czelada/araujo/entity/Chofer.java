package elicuci.czelada.araujo.entity;

import elicuci.czelada.araujo.entity.enums.EstadoVehiculo;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="choferes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chofer {

    private Long idChofer;

    private String nombreCompleto;

    private String dni;

    private String licencia;

    private String telefono;

    private EstadoVehiculo estado;

    private Vehiculo vehiculo;
}
