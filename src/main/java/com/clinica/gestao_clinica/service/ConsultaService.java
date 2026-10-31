package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.ConsultaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ConsultaResponseDTO;
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.enums.StatusConsulta;
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
import com.clinica.gestao_clinica.exception.RegraNegocioException;
import com.clinica.gestao_clinica.mapper.ConsultaMapper;
import com.clinica.gestao_clinica.repository.ConsultaRepository;
import com.clinica.gestao_clinica.repository.MedicoRepository;
import com.clinica.gestao_clinica.repository.PacienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
public class ConsultaService {

    private static final List<StatusConsulta> STATUS_QUE_OCUPAM_HORARIO =
            List.of(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA);

    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public ConsultaService(ConsultaRepository consultaRepository,
                           MedicoRepository medicoRepository,
                           PacienteRepository pacienteRepository) {
        this.consultaRepository = consultaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional
    public ConsultaResponseDTO agendar(ConsultaRequestDTO dto) {
        Medico medico = buscarMedicoAtivo(dto.medicoId());
        Paciente paciente = buscarPacienteAtivo(dto.pacienteId());

        if (consultaRepository.existsByMedicoIdAndDataConsultaAndStatusConsultaIn(
                medico.getId(), dto.dataConsulta(), STATUS_QUE_OCUPAM_HORARIO)) {
            throw new ConflitoException("O médico já possui consulta marcada neste horário");
        }

        Consulta consulta = ConsultaMapper.toEntity(dto, medico, paciente);
        consulta = consultaRepository.save(consulta);

        log.info("Consulta {} agendada: médico {} / paciente {} em {}",
                consulta.getId(), medico.getId(), paciente.getId(), consulta.getDataConsulta());
        return ConsultaMapper.toResponse(consulta);
    }

    @Transactional(readOnly = true)
    public Page<ConsultaResponseDTO> listar(Long medicoId, Long pacienteId, StatusConsulta status,
                                            LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new RegraNegocioException("A data inicial não pode ser posterior à data final");
        }

        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.atTime(LocalTime.MAX) : null;

        return consultaRepository.filtrar(medicoId, pacienteId, status, inicio, fim, pageable)
                .map(ConsultaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ConsultaResponseDTO buscarPorId(Long id) {
        return ConsultaMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ConsultaResponseDTO atualizar(Long id, ConsultaRequestDTO dto) {
        Consulta consulta = buscarEntidade(id);
        exigirEmAberto(consulta, "remarcar");

        Medico medico = buscarMedicoAtivo(dto.medicoId());
        Paciente paciente = buscarPacienteAtivo(dto.pacienteId());

        if (consultaRepository.existsByMedicoIdAndDataConsultaAndStatusConsultaInAndIdNot(
                medico.getId(), dto.dataConsulta(), STATUS_QUE_OCUPAM_HORARIO, id)) {
            throw new ConflitoException("O médico já possui consulta marcada neste horário");
        }

        consulta.setDataConsulta(dto.dataConsulta());
        consulta.setMotivo(dto.motivo());
        consulta.setMedico(medico);
        consulta.setPaciente(paciente);
        consulta.setStatusConsulta(StatusConsulta.AGENDADA);

        log.info("Consulta {} remarcada para {}", id, dto.dataConsulta());
        return ConsultaMapper.toResponse(consulta);
    }

    @Transactional
    public ConsultaResponseDTO confirmar(Long id) {
        Consulta consulta = buscarEntidade(id);
        if (consulta.getStatusConsulta() != StatusConsulta.AGENDADA) {
            throw new RegraNegocioException("Só é possível confirmar consultas AGENDADAS. Status atual: "
                    + consulta.getStatusConsulta());
        }
        consulta.setStatusConsulta(StatusConsulta.CONFIRMADA);
        log.info("Consulta {} confirmada", id);
        return ConsultaMapper.toResponse(consulta);
    }

    @Transactional
    public ConsultaResponseDTO concluir(Long id) {
        Consulta consulta = buscarEntidade(id);
        exigirEmAberto(consulta, "concluir");
        consulta.setStatusConsulta(StatusConsulta.REALIZADA);
        log.info("Consulta {} concluída", id);
        return ConsultaMapper.toResponse(consulta);
    }

    @Transactional
    public ConsultaResponseDTO cancelar(Long id) {
        Consulta consulta = buscarEntidade(id);
        exigirEmAberto(consulta, "cancelar");
        consulta.setStatusConsulta(StatusConsulta.CANCELADA);
        log.info("Consulta {} cancelada", id);
        return ConsultaMapper.toResponse(consulta);
    }

    private Consulta buscarEntidade(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta", id));
    }

    private Medico buscarMedicoAtivo(Long medicoId) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico", medicoId));
        if (!medico.getUsuario().isEnabled()) {
            throw new RegraNegocioException("O médico está desativado");
        }
        return medico;
    }

    private Paciente buscarPacienteAtivo(Long pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente", pacienteId));
        if (!paciente.getUsuario().isEnabled()) {
            throw new RegraNegocioException("O paciente está desativado");
        }
        return paciente;
    }

    private void exigirEmAberto(Consulta consulta, String acao) {
        if (!consulta.getStatusConsulta().estaEmAberto()) {
            throw new RegraNegocioException("Não é possível " + acao + " uma consulta com status "
                    + consulta.getStatusConsulta());
        }
    }
}
