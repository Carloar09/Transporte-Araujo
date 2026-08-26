package elicuci.czelada.araujo.dto;

import elicuci.czelada.araujo.entity.enums.RolUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos")
    private String dni;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "Mínimo 6 caracteres")
    private String password;

    private String telefono;

    @NotNull(message = "El rol es obligatorio")
    private RolUsuario rol;
}
