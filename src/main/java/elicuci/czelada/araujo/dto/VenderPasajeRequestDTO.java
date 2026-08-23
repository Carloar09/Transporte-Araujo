package elicuci.czelada.araujo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VenderPasajeRequestDTO {

    @NotNull
    private Long idViaje;

    @NotNull
    private Long idAsiento;

    @NotBlank
    private String nombrePasajero;

    @NotBlank
    @Size(min = 8, max = 8)
    private String dniPasajero;
}
