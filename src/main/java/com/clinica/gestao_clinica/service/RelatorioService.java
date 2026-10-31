package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.response.RelatorioConsultaResponseDTO;
import com.clinica.gestao_clinica.enums.StatusConsulta;
import com.clinica.gestao_clinica.repository.ConsultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RelatorioService {

    private final ConsultaRepository consultaRepository;

    public RelatorioService(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    @Transactional(readOnly = true)
    public RelatorioConsultaResponseDTO gerarRelatorio() {
        return new RelatorioConsultaResponseDTO(
                consultaRepository.count(),
                consultaRepository.countByStatusConsulta(StatusConsulta.AGENDADA),
                consultaRepository.countByStatusConsulta(StatusConsulta.CONFIRMADA),
                consultaRepository.countByStatusConsulta(StatusConsulta.REALIZADA),
                consultaRepository.countByStatusConsulta(StatusConsulta.CANCELADA)
        );
    }
}
