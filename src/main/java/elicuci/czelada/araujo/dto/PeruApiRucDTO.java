package elicuci.czelada.araujo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeruApiRucDTO {

    private String ruc;

    @JsonProperty("razon_social")
    private String razonSocial;

    private String estado;
    private String direccion;
}