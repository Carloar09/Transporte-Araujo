package elicuci.czelada.araujo.dto;

import elicuci.czelada.araujo.entity.enums.EstadoVehiculo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VehiculoRequestDTO {

    @NotBlank
    private String placa;

    @NotBlank
    private String marca;

    @NotBlank
    private String modelo;

    @NotNull
    private Integer anio;

    @NotNull
    @Min(1)
    private Integer capacidad;

    @NotNull
    private EstadoVehiculo estado;

    private LocalDate ultimaRevision;
}
