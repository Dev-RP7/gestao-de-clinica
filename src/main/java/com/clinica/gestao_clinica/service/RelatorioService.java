package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.response.RelatorioConsultaResponseDTO;
import com.clinica.gestao_clinica.enums.StatusConsulta;
import com.clinica.gestao_clinica.repository.ConsultaRepository;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {

    private final ConsultaRepository  consultaRepository;

    public RelatorioService(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    public RelatorioConsultaResponseDTO gerarRelatorio() {

        long agendadas =
                consultaRepository.countByStatusConsulta(StatusConsulta.AGENDADA);

        long confirmadas =
                consultaRepository.countByStatusConsulta(StatusConsulta.CONFIRMADA);

        long concluidas =
                consultaRepository.countByStatusConsulta(StatusConsulta.REALIZADA);

        long canceladas =
                consultaRepository.countByStatusConsulta(StatusConsulta.CANCELADA);

        long total =
                agendadas +
                confirmadas +
                concluidas +
                canceladas;

        return new RelatorioConsultaResponseDTO(
                total,
                agendadas,
                confirmadas,
                concluidas,
                canceladas
        );
    }
}
