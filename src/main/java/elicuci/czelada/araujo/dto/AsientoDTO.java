package elicuci.czelada.araujo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AsientoDTO {

    private Long idAsiento;
    private Integer numero;
    private String estado;
    private boolean esChofer;
}
