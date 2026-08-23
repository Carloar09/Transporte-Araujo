package elicuci.czelada.araujo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class EncomiendaResponseDTO {

    private Long idEncomienda;
    private String codigoSeguimiento;

    private String remitenteNombre;
    private String remitenteDni;
    private String remitenteTelefono;


    private String destinatarioNombre;
    private String destinatarioDni;
    private String destinatarioTelefono;


    private String ciudadOrigen;
    private String ciudadDestino;
    private String descripcion;
    private Double peso;
    private BigDecimal precio;
    private String estado;
    private String fechaRegistro;
    private String registradoPor;
    // lista para el historial de seguimiento
    private List<HistorialDTO> historial;

    @Data
    @Builder
    public static class HistorialDTO {
        private String estado;
        private String descripcion;
        private String fecha;
    }
}
