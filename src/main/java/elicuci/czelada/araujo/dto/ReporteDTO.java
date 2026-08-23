package elicuci.czelada.araujo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReporteDTO {

    private String desde;
    private String hasta;
    private Double totalPasajes;
    private Double totalEncomiendas;
    private Double totalGeneral;
    private int cantidadPasajes;
    private int cantidadEncomiendas;
    private List<PasajeResponseDTO> detallePasajes;
    private List<EncomiendaResponseDTO> detalleEncomiendas;
}
