package elicuci.czelada.araujo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoDTO {

    private Long idAsiento;
    private Integer numero;
    private String estado;
    private boolean esChofer;

    // 👈 Nuevos campos para enviar los datos del pasaje ocupado al Frontend
    private String dniPasajero;
    private String nombrePasajero;
    private Double precio;
}