package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.ReceitaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ReceitaResponseDTO;
import com.clinica.gestao_clinica.entity.Prontuario;
import com.clinica.gestao_clinica.entity.Receita;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
import com.clinica.gestao_clinica.mapper.ReceitaMapper;
import com.clinica.gestao_clinica.repository.ProntuarioRepository;
import com.clinica.gestao_clinica.repository.ReceitaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ReceitaService {

    private final ReceitaRepository receitaRepository;
    private final ProntuarioRepository prontuarioRepository;

    public ReceitaService(ReceitaRepository receitaRepository, ProntuarioRepository prontuarioRepository) {
        this.receitaRepository = receitaRepository;
        this.prontuarioRepository = prontuarioRepository;
    }

    @Transactional
    public ReceitaResponseDTO criar(ReceitaRequestDTO dto) {
        Prontuario prontuario = prontuarioRepository.findById(dto.prontuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Prontuário", dto.prontuarioId()));
        Receita receita = receitaRepository.save(ReceitaMapper.toEntity(dto, prontuario));
        log.info("Receita {} criada no prontuário {}", receita.getId(), prontuario.getId());
        return ReceitaMapper.toResponse(receita);
    }

    @Transactional(readOnly = true)
    public Page<ReceitaResponseDTO> listar(Long prontuarioId, Pageable pageable) {
        Page<Receita> pagina = prontuarioId == null
                ? receitaRepository.findAll(pageable)
                : receitaRepository.findByProntuarioId(prontuarioId, pageable);
        return pagina.map(ReceitaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ReceitaResponseDTO buscarPorId(Long id) {
        return ReceitaMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ReceitaResponseDTO atualizar(Long id, ReceitaRequestDTO dto) {
        Receita receita = buscarEntidade(id);
        receita.setMedicamento(dto.medicamento());
        receita.setDosagem(dto.dosagem());
        receita.setFrequencia(dto.frequencia());
        receita.setDuracao(dto.duracao());
        receita.setObservacao(dto.observacao());
        log.info("Receita {} atualizada", id);
        return ReceitaMapper.toResponse(receita);
    }

    @Transactional
    public void excluir(Long id) {
        receitaRepository.delete(buscarEntidade(id));
        log.info("Receita {} excluída", id);
    }

    private Receita buscarEntidade(Long id) {
        return receitaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Receita", id));
    }
}
