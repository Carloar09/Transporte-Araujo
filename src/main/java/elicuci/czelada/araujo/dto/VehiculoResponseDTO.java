package elicuci.czelada.araujo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class VehiculoResponseDTO {

    private Long idVehiculo;
    private String placa;
    private String marca;
    private String modelo;
    private Integer anio;
    private Integer capacidad;
    private String estado;
    private LocalDate ultimaRevision;
    private String choferAsignado;
}
