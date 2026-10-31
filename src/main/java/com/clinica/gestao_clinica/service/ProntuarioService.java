package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.ProntuarioAtualizadoRequestDTO;
import com.clinica.gestao_clinica.dto.request.ProntuarioRequestDTO;
import com.clinica.gestao_clinica.dto.response.ProntuarioResponseDTO;
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Prontuario;
import com.clinica.gestao_clinica.enums.StatusConsulta;
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
import com.clinica.gestao_clinica.exception.RegraNegocioException;
import com.clinica.gestao_clinica.mapper.ProntuarioMapper;
import com.clinica.gestao_clinica.repository.ConsultaRepository;
import com.clinica.gestao_clinica.repository.ProntuarioRepository;
import com.clinica.gestao_clinica.repository.ReceitaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final ReceitaRepository receitaRepository;

    public ProntuarioService(ProntuarioRepository prontuarioRepository,
                             ConsultaRepository consultaRepository,
                             ReceitaRepository receitaRepository) {
        this.prontuarioRepository = prontuarioRepository;
        this.consultaRepository = consultaRepository;
        this.receitaRepository = receitaRepository;
    }

    @Transactional
    public ProntuarioResponseDTO criar(ProntuarioRequestDTO dto) {
        Consulta consulta = consultaRepository.findById(dto.consultaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta", dto.consultaId()));
        if (consulta.getStatusConsulta() == StatusConsulta.CANCELADA) {
            throw new RegraNegocioException("Não é possível criar prontuário para uma consulta cancelada");
        }
        if (prontuarioRepository.existsByConsultaId(consulta.getId())) {
            throw new ConflitoException("Esta consulta já possui prontuário");
        }
        Prontuario prontuario = prontuarioRepository.save(ProntuarioMapper.toEntity(dto, consulta));
        log.info("Prontuário {} criado para a consulta {}", prontuario.getId(), consulta.getId());
        return ProntuarioMapper.toResponse(prontuario);
    }

    @Transactional(readOnly = true)
    public Page<ProntuarioResponseDTO> listar(Long pacienteId, Long medicoId, Pageable pageable) {
        return prontuarioRepository.filtrar(pacienteId, medicoId, pageable).map(ProntuarioMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProntuarioResponseDTO buscarPorId(Long id) {
        return ProntuarioMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProntuarioResponseDTO atualizar(Long id, ProntuarioAtualizadoRequestDTO dto) {
        Prontuario prontuario = buscarEntidade(id);
        prontuario.setPeso(dto.peso());
        prontuario.setAltura(dto.altura());
        prontuario.setPressao(dto.pressao());
        prontuario.setTemperatura(dto.temperatura());
        prontuario.setDiagnostico(dto.diagnostico());
        prontuario.setTratamento(dto.tratamento());
        prontuario.setObservacao(dto.observacao());
        log.info("Prontuário {} atualizado", id);
        return ProntuarioMapper.toResponse(prontuario);
    }

    @Transactional
    public void excluir(Long id) {
        Prontuario prontuario = buscarEntidade(id);
        if (receitaRepository.existsByProntuarioId(id)) {
            throw new ConflitoException("O prontuário possui receitas; exclua-as antes");
        }
        prontuarioRepository.delete(prontuario);
        log.info("Prontuário {} excluído", id);
    }

    private Prontuario buscarEntidade(Long id) {
        return prontuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Prontuário", id));
    }
}
