package elicuci.czelada.araujo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {

    private String token;
    private String nombreCompleto;
    private String dni;
    private String rol;
    private String mensaje;
}

