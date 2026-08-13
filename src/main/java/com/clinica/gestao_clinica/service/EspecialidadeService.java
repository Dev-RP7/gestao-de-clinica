package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.EspecialidadeCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.EspecialidadeResponseDTO;
import com.clinica.gestao_clinica.entity.Especialidade;
import com.clinica.gestao_clinica.mapper.EspecialidadeMapper;
import com.clinica.gestao_clinica.repository.EspecialidadeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;

    public EspecialidadeService(EspecialidadeRepository especialidadeRepository) {
        this.especialidadeRepository = especialidadeRepository;
    }

    public EspecialidadeResponseDTO cadastrar(EspecialidadeCadastroRequestDTO dto) {

        Especialidade especialidade = new Especialidade();
        especialidade.setNome(dto.nome());

        especialidade = especialidadeRepository.save(especialidade);

        return new EspecialidadeResponseDTO(
                especialidade.getId(),
                especialidade.getNome()
        );
    }

    public List<EspecialidadeResponseDTO> listar() {

        return especialidadeRepository.findAll()
                .stream()
                .map(especialidade -> new EspecialidadeResponseDTO(
                        especialidade.getId(),
                        especialidade.getNome()
                ))
                .toList();
    }

    public List<EspecialidadeResponseDTO> buscarPorNome(String nome) {

        List<Especialidade> especialidades = especialidadeRepository
                .findByNome(nome);

        return especialidades.stream()
                .map(EspecialidadeMapper::toResponse)
                .toList();
    }
/*
    public List<MedicoResponseDTO> buscarMedicoEspecialista(Long id) {

        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceAccessException("Especialidade não encontrada"));

        return especialidade.getMedicos()

    }*/
}
