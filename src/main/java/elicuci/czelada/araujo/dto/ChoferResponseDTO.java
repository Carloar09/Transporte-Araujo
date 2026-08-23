package elicuci.czelada.araujo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChoferResponseDTO {

    private Long idChofer;
    private String nombreCompleto;
    private String dni;
    private String licencia;
    private String telefono;
    private String estado;
    private String vehiculoPlaca;
    private String vehiculoModelo;
}
