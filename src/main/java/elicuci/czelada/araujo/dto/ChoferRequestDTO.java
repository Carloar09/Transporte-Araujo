package elicuci.czelada.araujo.dto;

import elicuci.czelada.araujo.entity.enums.EstadoChofer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChoferRequestDTO {

    @NotBlank
    private String nombreCompleto;

    @NotBlank
    @Size(min = 8, max = 8)
    private String dni;

    @NotBlank
    private String licencia;

    private String telefono;

    @NotNull
    private EstadoChofer estado;

    // para asignar su vehiculo
    private Long vehiculoId;
}
