package elicuci.czelada.araujo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeruApiDniDTO {

    @JsonProperty("dni")
    private String numeroDocumento;

    @JsonProperty("cliente")
    private String nombreCompleto;

    private String tipoDocumento;

    private String nombres;

    @JsonProperty("apellido_paterno")
    private String apellidoPaterno;

    @JsonProperty("apellido_materno")
    private String apellidoMaterno;
}