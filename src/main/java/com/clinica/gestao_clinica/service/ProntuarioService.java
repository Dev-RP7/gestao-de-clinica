package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.ProntuarioRequestDTO;
import com.clinica.gestao_clinica.dto.response.ProntuarioResponseDTO;
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Prontuario;
import com.clinica.gestao_clinica.mapper.ProntuarioMapper;
import com.clinica.gestao_clinica.repository.ConsultaRepository;
import com.clinica.gestao_clinica.repository.ProntuarioRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final ConsultaRepository consultaRepository;

    public ProntuarioService(
            ProntuarioRepository prontuarioRepository,
            ConsultaRepository consultaRepository
    ) {
        this.prontuarioRepository = prontuarioRepository;
        this.consultaRepository = consultaRepository;
    }

    public ProntuarioResponseDTO criar(ProntuarioRequestDTO dto) {

        Consulta consulta = consultaRepository.findById(dto.consultaId())
                .orElseThrow(() -> new RuntimeException("Consulta inexistente"));

        Prontuario prontuario = new Prontuario();
        prontuario.setPeso(dto.peso());
        prontuario.setAltura(dto.altura());
        prontuario.setPressao(dto.pressao());
        prontuario.setTemperatura(dto.temperatura());
        prontuario.setDiagnostico(dto.diagnostico());
        prontuario.setTratamento(dto.tratamento());
        prontuario.setObservacao(dto.observacao());
        prontuario.setConsulta(consulta);

        prontuario = prontuarioRepository.save(prontuario);

        return ProntuarioMapper.toResponse(prontuario);
    }

    public List<ProntuarioResponseDTO> buscarTodos() {

        return prontuarioRepository.findAll()
                .stream()
                .map(prontuario -> new ProntuarioResponseDTO(
                        prontuario.getId(),
                        prontuario.getConsulta().getId(),
                        prontuario.getPeso(),
                        prontuario.getAltura(),
                        prontuario.getPressao(),
                        prontuario.getTemperatura(),
                        prontuario.getDiagnostico(),
                        prontuario.getTratamento(),
                        prontuario.getObservacao()
                ))
                .toList();
    }

    public ProntuarioResponseDTO  buscarPorId(Long id) {

        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prontuario inexistente"));

        return new ProntuarioResponseDTO(
                prontuario.getId(),
                prontuario.getConsulta().getId(),
                prontuario.getPeso(),
                prontuario.getAltura(),
                prontuario.getPressao(),
                prontuario.getTemperatura(),
                prontuario.getDiagnostico(),
                prontuario.getTratamento(),
                prontuario.getObservacao()
        );
    }

    public ProntuarioResponseDTO atualizar(Long id, @Valid ProntuarioRequestDTO dto) {

        Prontuario prontuario = prontuarioRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Prontuario inexistente"));

        prontuario.setPeso(dto.peso());
        prontuario.setAltura(dto.altura());
        prontuario.setPressao(dto.pressao());
        prontuario.setTemperatura(dto.temperatura());
        prontuario.setDiagnostico(dto.diagnostico());
        prontuario.setTratamento(dto.tratamento());
        prontuario.setObservacao(dto.observacao());

        Prontuario prontuarioAtualizado = prontuarioRepository.save(prontuario);

        return new ProntuarioResponseDTO(
                prontuario.getId(),
                prontuario.getConsulta().getId(),
                prontuario.getPeso(),
                prontuario.getAltura(),
                prontuario.getPressao(),
                prontuario.getTemperatura(),
                prontuario.getDiagnostico(),
                prontuario.getTratamento(),
                prontuario.getObservacao()
        );
    }

    public void deletarProntuario(Long id) {

        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado."));

        prontuarioRepository.delete(prontuario);
    }

}
