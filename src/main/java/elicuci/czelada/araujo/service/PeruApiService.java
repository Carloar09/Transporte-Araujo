package elicuci.czelada.araujo.service;

import elicuci.czelada.araujo.dto.PeruApiDniDTO;
import elicuci.czelada.araujo.dto.PeruApiRucDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PeruApiService {

    private String apiToken;

    private String apiUrl;
    // esto sirve para hacer las llamadas HHTTPS
    private final RestTemplate restTemplate= new RestTemplate();

    public PeruApiDniDTO consultarDni(String dni) {
        String url=apiUrl+"/api/dni/"+dni + "?api_token="+apiToken;

        try {
            // Hacemos la petición GET
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                // Verificamos que la respuesta sea POSITIVASO
                String mensaje = (String) body.get("mensaje");
                if (!"OK".equals(mensaje)) {
                    throw new RuntimeException("DNI no encontrado: " + dni);
                }

                // Ordenamos la respuesta para el DTO
                PeruApiDniDTO dto = new PeruApiDniDTO();
                dto.setNombreCompleto((String) body.get("cliente"));
                dto.setNombres((String) body.get("nombres"));
                dto.setApellidoPaterno((String) body.get("apellido_paterno"));
                dto.setApellidoMaterno((String) body.get("apellido_materno"));
                dto.setNumeroDocumento((String) body.get("dni"));
                dto.setTipoDocumento("DNI");

                return dto;
            }

            throw new RuntimeException("Error al consultar el DNI");

        } catch (Exception e) {
            throw new RuntimeException("No se pudo consultar el DNI: " + e.getMessage());
        }
    }
    public PeruApiRucDTO consultarRuc(String ruc) {

        String url = apiUrl + "/api/ruc/" + ruc + "?api_token=" + apiToken;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                String mensaje = (String) body.get("mensaje");
                if (!"OK".equals(mensaje)) {
                    throw new RuntimeException("RUC no encontrado: " + ruc);
                }

                PeruApiRucDTO dto = new PeruApiRucDTO();
                dto.setRuc((String) body.get("ruc"));
                dto.setRazonSocial((String) body.get("razon_social"));
                dto.setEstado((String) body.get("estado"));
                dto.setDireccion((String) body.get("direccion"));

                return dto;
            }

            throw new RuntimeException("Error al consultar el RUC");

        } catch (Exception e) {
            throw new RuntimeException("No se pudo consultar el RUC: " + e.getMessage());
        }
    }
}
