package elicuci.czelada.araujo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ViajeRequestDTO {
    @NotNull
    private Long ciudadOrigenId;
    @NotNull
    private Long ciudadDestinoId;
    @NotNull
    private LocalDateTime fechaHora;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal precio;
    @NotNull
    private Long vehiculoId;
    @NotNull
    private Long choferId;
}
