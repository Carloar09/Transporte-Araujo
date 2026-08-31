package elicuci.czelada.araujo.dto;

import elicuci.czelada.araujo.entity.enums.EstadoVehiculo;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ChoferRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos")
    private String dni;

    @NotBlank(message = "La licencia es obligatoria")
    private String licencia;

    private String telefono;

    @NotNull(message = "El estado es obligatorio")
    private EstadoVehiculo estado;

    // Vehículo a asignar (opcional)
    private Long vehiculoId;
}
