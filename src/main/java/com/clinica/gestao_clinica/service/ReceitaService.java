package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.ReceitaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ReceitaResponseDTO;
import com.clinica.gestao_clinica.entity.Prontuario;
import com.clinica.gestao_clinica.entity.Receita;
import com.clinica.gestao_clinica.mapper.ReceitaMapper;
import com.clinica.gestao_clinica.mapper.UsuarioMapper;
import com.clinica.gestao_clinica.repository.ProntuarioRepository;
import com.clinica.gestao_clinica.repository.ReceitaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceitaService {

    private final ReceitaRepository receitaRepository;
    private final ProntuarioRepository prontuarioRepository;

    public ReceitaService(
        ReceitaRepository receitaRepository,
        ProntuarioRepository prontuarioRepository
    ) {
        this.receitaRepository = receitaRepository;
        this.prontuarioRepository = prontuarioRepository;
    }

    public ReceitaResponseDTO criarReceita(ReceitaRequestDTO dto) {

        Prontuario prontuario = prontuarioRepository.findById(dto.prontuarioId())
                .orElseThrow(() -> new RuntimeException("Prontuario não encontrado"));

        Receita receita = new Receita();
        receita.setMedicamento(dto.medicamento());
        receita.setDosagem(dto.dosagem());
        receita.setFrequencia(dto.frequencia());
        receita.setDuracao(dto.duracao());
        receita.setObservacao(dto.observacao());

        receita =  receitaRepository.save(receita);

        return ReceitaMapper.toResponse(receita);
    }

    public List<ReceitaResponseDTO> listarTodasReceitas() {

        return receitaRepository.findAll()
                .stream()
                .map(ReceitaMapper::toResponse)
                .toList();
    }

    public ReceitaResponseDTO buscarPorId(Long id) {

        Receita receita = receitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receita não encontrada"));

        return ReceitaMapper.toResponse(receita);
    }

    public ReceitaResponseDTO alterarReceita(Long id, @Valid ReceitaRequestDTO dto) {

        Receita receita = receitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receita não encontrada!"));

        receita.setMedicamento(dto.medicamento());
        receita.setDosagem(dto.dosagem());
        receita.setFrequencia(dto.frequencia());
        receita.setDuracao(dto.duracao());
        receita.setObservacao(dto.observacao());

        Receita receitaAlterada = receitaRepository.save(receita);

        return ReceitaMapper.toResponse(receitaAlterada);

    }

    public void deletarReceita(Long id) {
        Receita receita = receitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receita não encontrada ou já foi deletada"));

        receitaRepository.delete(receita);
    }
}
