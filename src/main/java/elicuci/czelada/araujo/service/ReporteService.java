package elicuci.czelada.araujo.service;

import elicuci.czelada.araujo.dto.ReporteCajaDTO;
import elicuci.czelada.araujo.repository.EncomiendaRepository;
import elicuci.czelada.araujo.repository.PasajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final PasajeRepository pasajeRepository;
    private final EncomiendaRepository encomiendaRepository;
    private final PasajeService pasajeService;
    private final EncomiendaService encomiendaService;

    public ReporteCajaDTO getReporte(LocalDate desde, LocalDate hasta) {

        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(23, 59, 59);

        // Totales
        Double totalPasajes = pasajeRepository.sumTotalPasajesByFecha(inicio, fin);
        Double totalEncomiendas = encomiendaRepository.sumTotalEncomiendas(inicio, fin);

        totalPasajes = totalPasajes != null ? totalPasajes : 0.0;
        totalEncomiendas = totalEncomiendas != null ? totalEncomiendas : 0.0;

        // Detalles
        var pasajes = pasajeRepository.findByFechaBetween(inicio, fin);
        var encomiendas = encomiendaRepository.findByFechaBetween(inicio, fin);

        return ReporteCajaDTO.builder()
                .desde(desde.toString())
                .hasta(hasta.toString())
                .totalPasajes(totalPasajes)
                .totalEncomiendas(totalEncomiendas)
                .totalGeneral(totalPasajes + totalEncomiendas)
                .cantidadPasajes(pasajes.size())
                .cantidadEncomiendas(encomiendas.size())
                .build();
    }
}