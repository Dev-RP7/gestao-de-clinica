// Pacote dos mappers.
package com.clinica.gestao_clinica.mapper;

// DTOs e entidades.
import com.clinica.gestao_clinica.dto.request.ProntuarioRequestDTO;
import com.clinica.gestao_clinica.dto.response.ProntuarioResponseDTO;
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Prontuario;

// Conversões de Prontuario.
public class ProntuarioMapper {

    // Impede instanciar a classe.
    private ProntuarioMapper() {
    }

    // Cria a entidade a partir do DTO e da consulta já buscada.
    public static Prontuario toEntity(ProntuarioRequestDTO dto, Consulta consulta) {
        // Cria o prontuário vazio.
        Prontuario prontuario = new Prontuario();
        // Peso.
        prontuario.setPeso(dto.peso());
        // Altura.
        prontuario.setAltura(dto.altura());
        // Pressão.
        prontuario.setPressao(dto.pressao());
        // Temperatura.
        prontuario.setTemperatura(dto.temperatura());
        // Diagnóstico.
        prontuario.setDiagnostico(dto.diagnostico());
        // Tratamento.
        prontuario.setTratamento(dto.tratamento());
        // Observação.
        prontuario.setObservacao(dto.observacao());
        // Liga a consulta.
        prontuario.setConsulta(consulta);
        // Devolve a entidade.
        return prontuario;
    }

    // Converte a entidade em DTO de resposta.
    public static ProntuarioResponseDTO toResponse(Prontuario prontuario) {
        // Monta o record.
        return new ProntuarioResponseDTO(
                // Id.
                prontuario.getId(),
                // Id da consulta.
                prontuario.getConsulta().getId(),
                // Peso.
                prontuario.getPeso(),
                // Altura.
                prontuario.getAltura(),
                // Pressão.
                prontuario.getPressao(),
                // Temperatura.
                prontuario.getTemperatura(),
                // Diagnóstico.
                prontuario.getDiagnostico(),
                // Tratamento.
                prontuario.getTratamento(),
                // Observação.
                prontuario.getObservacao()
        );
    }
}
