package elicuci.czelada.araujo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EncomiendaRequestDTO {
    @NotBlank
    private String remitenteNombre;
    @NotBlank
    private String remitenteDni;
    @NotBlank
    private String remitenteTelefono;

    @NotBlank
    private String destinatarioNombre;
    @NotBlank
    private String destinatarioDni;
    @NotBlank
    private String destinatarioTelefono;

    @NotNull
    private Long ciudadOrigenId;
    @NotNull
    private Long ciudadDestinoId;

    @NotNull
    @DecimalMin("0.01")
    private Double peso;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal precio;

}
