package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.EspecialidadeCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.EspecialidadeResponseDTO;
import com.clinica.gestao_clinica.entity.Especialidade;
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
import com.clinica.gestao_clinica.mapper.EspecialidadeMapper;
import com.clinica.gestao_clinica.repository.EspecialidadeRepository;
import com.clinica.gestao_clinica.repository.MedicoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;
    private final MedicoRepository medicoRepository;

    public EspecialidadeService(EspecialidadeRepository especialidadeRepository, MedicoRepository medicoRepository) {
        this.especialidadeRepository = especialidadeRepository;
        this.medicoRepository = medicoRepository;
    }

    @Transactional
    public EspecialidadeResponseDTO cadastrar(EspecialidadeCadastroRequestDTO dto) {
        if (especialidadeRepository.existsByNomeIgnoreCase(dto.nome().trim())) {
            throw new ConflitoException("Especialidade '" + dto.nome() + "' já cadastrada");
        }
        Especialidade especialidade = especialidadeRepository.save(EspecialidadeMapper.toEntity(dto));
        log.info("Especialidade {} cadastrada: {}", especialidade.getId(), especialidade.getNome());
        return EspecialidadeMapper.toResponse(especialidade);
    }

    @Transactional(readOnly = true)
    public Page<EspecialidadeResponseDTO> listar(String nome, Pageable pageable) {
        Page<Especialidade> pagina = (nome == null || nome.isBlank())
                ? especialidadeRepository.findAll(pageable)
                : especialidadeRepository.findByNomeContainingIgnoreCase(nome.trim(), pageable);
        return pagina.map(EspecialidadeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EspecialidadeResponseDTO buscarPorId(Long id) {
        return EspecialidadeMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public EspecialidadeResponseDTO atualizar(Long id, EspecialidadeCadastroRequestDTO dto) {
        Especialidade especialidade = buscarEntidade(id);
        String novoNome = dto.nome().trim();
        if (!especialidade.getNome().equalsIgnoreCase(novoNome)
                && especialidadeRepository.existsByNomeIgnoreCase(novoNome)) {
            throw new ConflitoException("Especialidade '" + novoNome + "' já cadastrada");
        }
        especialidade.setNome(novoNome);
        log.info("Especialidade {} renomeada para {}", id, novoNome);
        return EspecialidadeMapper.toResponse(especialidade);
    }

    @Transactional
    public void excluir(Long id) {
        Especialidade especialidade = buscarEntidade(id);
        if (medicoRepository.existsByEspecialidadeId(id)) {
            throw new ConflitoException("Especialidade em uso por médicos; não pode ser excluída");
        }
        especialidadeRepository.delete(especialidade);
        log.info("Especialidade {} excluída", id);
    }

    private Especialidade buscarEntidade(Long id) {
        return especialidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade", id));
    }
}
