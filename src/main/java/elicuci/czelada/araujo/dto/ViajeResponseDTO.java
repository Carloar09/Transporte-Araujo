package elicuci.czelada.araujo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ViajeResponseDTO {

    private Long idViaje;
    private String ciudadOrigen;
    private String ciudadDestino;
    private String fechaHora;
    private BigDecimal precio;
    private String placa;
    private String choferNombre;
    private String estado;
    private long asientosLibres;
    private long asientosOcupados;
    private int totalAsientos;
}

