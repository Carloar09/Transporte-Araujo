package elicuci.czelada.araujo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PasajeRequestDTO {

    @NotNull
    private Long viajeId;

    @NotNull
    private Integer numeroAsiento;

    @NotBlank(message = "El nombre del pasajero es obligatorio")
    private String nombrePasajero;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
    private String dniPasajero;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal precio;

    @NotNull
    private Long usuarioId; // Usuario/Cajero que realiza la venta
}