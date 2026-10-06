// Pacote dos services.
package com.clinica.gestao_clinica.service;

// DTO, enum e repositório.
import com.clinica.gestao_clinica.dto.response.RelatorioConsultaResponseDTO;
import com.clinica.gestao_clinica.enums.StatusConsulta;
import com.clinica.gestao_clinica.repository.ConsultaRepository;
// Service e transação.
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// O Spring gerencia esta classe.
@Service
// Gera relatórios gerenciais.
public class RelatorioService {

    // Repositório de consultas.
    private final ConsultaRepository consultaRepository;

    // Injeção de dependências.
    public RelatorioService(ConsultaRepository consultaRepository) {
        // Guarda o repositório.
        this.consultaRepository = consultaRepository;
    }

    // Transação de leitura.
    @Transactional(readOnly = true)
    // Conta as consultas por status.
    public RelatorioConsultaResponseDTO gerarRelatorio() {
        // Monta o DTO com uma contagem para cada status.
        return new RelatorioConsultaResponseDTO(
                // count() conta todas as linhas da tabela.
                consultaRepository.count(),
                // Quantidade de agendadas.
                consultaRepository.countByStatusConsulta(StatusConsulta.AGENDADA),
                // Quantidade de confirmadas.
                consultaRepository.countByStatusConsulta(StatusConsulta.CONFIRMADA),
                // Quantidade de realizadas.
                consultaRepository.countByStatusConsulta(StatusConsulta.REALIZADA),
                // Quantidade de canceladas.
                consultaRepository.countByStatusConsulta(StatusConsulta.CANCELADA)
        );
    }
}
