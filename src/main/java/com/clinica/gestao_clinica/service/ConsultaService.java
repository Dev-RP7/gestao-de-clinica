// Pacote dos services.
package com.clinica.gestao_clinica.service;

// DTOs, entidades e enum.
import com.clinica.gestao_clinica.dto.request.ConsultaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ConsultaResponseDTO;
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.enums.StatusConsulta;
// Nossas exceções.
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
import com.clinica.gestao_clinica.exception.RegraNegocioException;
// Mapper.
import com.clinica.gestao_clinica.mapper.ConsultaMapper;
// Repositórios.
import com.clinica.gestao_clinica.repository.ConsultaRepository;
import com.clinica.gestao_clinica.repository.MedicoRepository;
import com.clinica.gestao_clinica.repository.PacienteRepository;
// Lombok: cria o campo "log".
import lombok.extern.slf4j.Slf4j;
// Paginação.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// Marca a classe como service.
import org.springframework.stereotype.Service;
// Controle de transações.
import org.springframework.transaction.annotation.Transactional;

// Datas e lista.
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

// Cria o "log".
@Slf4j
// O Spring gerencia esta classe.
@Service
// Regras de negócio das consultas.
public class ConsultaService {

    // Status que "ocupam" o horário do médico (consultas canceladas ou realizadas liberam o horário).
    private static final List<StatusConsulta> STATUS_QUE_OCUPAM_HORARIO =
            // Lista imutável com AGENDADA e CONFIRMADA.
            List.of(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA);

    // Repositório de consultas.
    private final ConsultaRepository consultaRepository;
    // Repositório de médicos.
    private final MedicoRepository medicoRepository;
    // Repositório de pacientes.
    private final PacienteRepository pacienteRepository;

    // Injeção de dependências pelo construtor.
    public ConsultaService(ConsultaRepository consultaRepository,
                           MedicoRepository medicoRepository,
                           PacienteRepository pacienteRepository) {
        // Guarda o repositório de consultas.
        this.consultaRepository = consultaRepository;
        // Guarda o repositório de médicos.
        this.medicoRepository = medicoRepository;
        // Guarda o repositório de pacientes.
        this.pacienteRepository = pacienteRepository;
    }

    // @Transactional: tudo dentro do método acontece numa única transação (ou tudo dá certo, ou nada é salvo).
    @Transactional
    // Agenda uma nova consulta.
    public ConsultaResponseDTO agendar(ConsultaRequestDTO dto) {
        // Busca o médico e garante que está ativo.
        Medico medico = buscarMedicoAtivo(dto.medicoId());
        // Busca o paciente e garante que está ativo.
        Paciente paciente = buscarPacienteAtivo(dto.pacienteId());

        // Regra: o médico não pode ter duas consultas no mesmo horário.
        if (consultaRepository.existsByMedicoIdAndDataConsultaAndStatusConsultaIn(
                // Médico, data e status considerados.
                medico.getId(), dto.dataConsulta(), STATUS_QUE_OCUPAM_HORARIO)) {
            // Se já existe, lança conflito (HTTP 409).
            throw new ConflitoException("O médico já possui consulta marcada neste horário");
        }

        // Converte o DTO em entidade (com status AGENDADA).
        Consulta consulta = ConsultaMapper.toEntity(dto, medico, paciente);
        // Salva no banco. O "save" devolve a entidade com o id preenchido.
        consulta = consultaRepository.save(consulta);

        // Registra no log.
        log.info("Consulta {} agendada: médico {} / paciente {} em {}",
                // Valores que substituem os "{}" na mensagem.
                consulta.getId(), medico.getId(), paciente.getId(), consulta.getDataConsulta());
        // Converte para DTO e devolve.
        return ConsultaMapper.toResponse(consulta);
    }

    // readOnly = true: transação só de leitura (o Hibernate pode otimizar).
    @Transactional(readOnly = true)
    // Lista consultas com filtros opcionais e paginação.
    public Page<ConsultaResponseDTO> listar(Long medicoId, Long pacienteId, StatusConsulta status,
                                            LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        // Regra: o período precisa fazer sentido.
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            // Data inicial depois da final é erro do cliente.
            throw new RegraNegocioException("A data inicial não pode ser posterior à data final");
        }

        // Converte a data inicial para o primeiro instante do dia (00:00), se ela foi enviada.
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        // Converte a data final para o último instante do dia (23:59:59.999...), se ela foi enviada.
        LocalDateTime fim = dataFim != null ? dataFim.atTime(LocalTime.MAX) : null;

        // Busca a página no banco.
        return consultaRepository.filtrar(medicoId, pacienteId, status, inicio, fim, pageable)
                // Converte cada Consulta da página em ConsultaResponseDTO (mantendo os dados da paginação).
                .map(ConsultaMapper::toResponse);
    }

    // Transação só de leitura.
    @Transactional(readOnly = true)
    // Busca uma consulta pelo id.
    public ConsultaResponseDTO buscarPorId(Long id) {
        // Busca a entidade e converte para DTO.
        return ConsultaMapper.toResponse(buscarEntidade(id));
    }

    // Transação de escrita.
    @Transactional
    // Remarca uma consulta (pode trocar data, motivo, médico e paciente).
    public ConsultaResponseDTO atualizar(Long id, ConsultaRequestDTO dto) {
        // Busca a consulta.
        Consulta consulta = buscarEntidade(id);
        // Regra: consultas canceladas ou realizadas não podem ser alteradas.
        exigirEmAberto(consulta, "remarcar");

        // Busca o médico (pode ser o mesmo ou outro).
        Medico medico = buscarMedicoAtivo(dto.medicoId());
        // Busca o paciente.
        Paciente paciente = buscarPacienteAtivo(dto.pacienteId());

        // Regra: o novo horário precisa estar livre (ignorando a própria consulta).
        if (consultaRepository.existsByMedicoIdAndDataConsultaAndStatusConsultaInAndIdNot(
                // Médico, data, status e id a ignorar.
                medico.getId(), dto.dataConsulta(), STATUS_QUE_OCUPAM_HORARIO, id)) {
            // Horário ocupado.
            throw new ConflitoException("O médico já possui consulta marcada neste horário");
        }

        // Atualiza a data.
        consulta.setDataConsulta(dto.dataConsulta());
        // Atualiza o motivo.
        consulta.setMotivo(dto.motivo());
        // Atualiza o médico.
        consulta.setMedico(medico);
        // Atualiza o paciente.
        consulta.setPaciente(paciente);
        // Remarcar volta a consulta para AGENDADA (precisa ser confirmada de novo).
        consulta.setStatusConsulta(StatusConsulta.AGENDADA);

        // Registra no log. Não precisamos chamar save(): dentro de @Transactional,
        // o Hibernate detecta as mudanças na entidade e grava sozinho no final ("dirty checking").
        log.info("Consulta {} remarcada para {}", id, dto.dataConsulta());
        // Devolve o DTO atualizado.
        return ConsultaMapper.toResponse(consulta);
    }

    // Transação de escrita.
    @Transactional
    // Confirma uma consulta agendada.
    public ConsultaResponseDTO confirmar(Long id) {
        // Busca a consulta.
        Consulta consulta = buscarEntidade(id);
        // Regra: só consultas AGENDADAS podem ser confirmadas.
        if (consulta.getStatusConsulta() != StatusConsulta.AGENDADA) {
            // Lança erro de regra de negócio (HTTP 422).
            throw new RegraNegocioException("Só é possível confirmar consultas AGENDADAS. Status atual: "
                    // Concatena o status atual na mensagem.
                    + consulta.getStatusConsulta());
        }
        // Muda o status.
        consulta.setStatusConsulta(StatusConsulta.CONFIRMADA);
        // Registra no log.
        log.info("Consulta {} confirmada", id);
        // Devolve o DTO.
        return ConsultaMapper.toResponse(consulta);
    }

    // Transação de escrita.
    @Transactional
    // Marca a consulta como realizada.
    public ConsultaResponseDTO concluir(Long id) {
        // Busca a consulta.
        Consulta consulta = buscarEntidade(id);
        // Regra: só consultas em aberto podem ser concluídas.
        exigirEmAberto(consulta, "concluir");
        // Muda o status.
        consulta.setStatusConsulta(StatusConsulta.REALIZADA);
        // Registra no log.
        log.info("Consulta {} concluída", id);
        // Devolve o DTO.
        return ConsultaMapper.toResponse(consulta);
    }

    // Transação de escrita.
    @Transactional
    // Cancela uma consulta. Não apagamos do banco para manter o histórico.
    public ConsultaResponseDTO cancelar(Long id) {
        // Busca a consulta.
        Consulta consulta = buscarEntidade(id);
        // Regra: só consultas em aberto podem ser canceladas.
        exigirEmAberto(consulta, "cancelar");
        // Muda o status.
        consulta.setStatusConsulta(StatusConsulta.CANCELADA);
        // Registra no log.
        log.info("Consulta {} cancelada", id);
        // Devolve o DTO.
        return ConsultaMapper.toResponse(consulta);
    }

    // ===== Métodos auxiliares (privados: só usados dentro desta classe) =====

    // Busca a entidade Consulta ou lança 404.
    private Consulta buscarEntidade(Long id) {
        // findById devolve um Optional.
        return consultaRepository.findById(id)
                // Se estiver vazio, lança a exceção.
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta", id));
    }

    // Busca o médico e verifica se está ativo.
    private Medico buscarMedicoAtivo(Long medicoId) {
        // Busca ou lança 404.
        Medico medico = medicoRepository.findById(medicoId)
                // Exceção caso não exista.
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico", medicoId));
        // Médico desativado não pode receber consultas.
        if (!medico.getUsuario().isEnabled()) {
            // Regra de negócio violada.
            throw new RegraNegocioException("O médico está desativado");
        }
        // Devolve o médico.
        return medico;
    }

    // Busca o paciente e verifica se está ativo.
    private Paciente buscarPacienteAtivo(Long pacienteId) {
        // Busca ou lança 404.
        Paciente paciente = pacienteRepository.findById(pacienteId)
                // Exceção caso não exista.
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente", pacienteId));
        // Paciente desativado não pode marcar consultas.
        if (!paciente.getUsuario().isEnabled()) {
            // Regra de negócio violada.
            throw new RegraNegocioException("O paciente está desativado");
        }
        // Devolve o paciente.
        return paciente;
    }

    // Garante que a consulta ainda está AGENDADA ou CONFIRMADA.
    private void exigirEmAberto(Consulta consulta, String acao) {
        // Se a consulta já terminou ou foi cancelada...
        if (!consulta.getStatusConsulta().estaEmAberto()) {
            // ...lança erro explicando o motivo.
            throw new RegraNegocioException("Não é possível " + acao + " uma consulta com status "
                    // Inclui o status atual.
                    + consulta.getStatusConsulta());
        }
    }
}
