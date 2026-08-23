package elicuci.czelada.araujo.dto;

import elicuci.czelada.araujo.entity.enums.RolUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "El DNI es obligatorio")
    private String dni;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    @NotNull(message = "El rol es obligatorio")
    private RolUsuario rol;
}
