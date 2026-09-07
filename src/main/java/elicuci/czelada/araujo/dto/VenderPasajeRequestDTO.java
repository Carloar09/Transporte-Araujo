package elicuci.czelada.araujo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VenderPasajeRequestDTO {

    @NotNull
    private Long viajeId;

    @NotNull(message = "El número de asiento es obligatorio")
    private Integer numeroAsiento;

    @NotBlank
    private String nombrePasajero;

    @NotBlank
    @Size(min = 8, max = 8)
    private String dniPasajero;
    @NotNull(message = "El precio es obligatorio")
    private BigDecimal precio;
}
