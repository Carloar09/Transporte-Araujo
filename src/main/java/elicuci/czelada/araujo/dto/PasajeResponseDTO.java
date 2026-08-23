package elicuci.czelada.araujo.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PasajeResponseDTO {

    private Long idPasaje;
    private Integer numeroAsiento;
    private String nombrePasajero;
    private String dniPasajero;
    private String ciudadOrigen;
    private String ciudadDestino;
    private String fechaHora;
    private BigDecimal precio;
    private String estado;
    private String vendidoPor;
    private String fechaVenta;

}
