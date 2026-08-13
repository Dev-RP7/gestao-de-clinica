package com.clinica.gestao_clinica.mapper;

import com.clinica.gestao_clinica.dto.request.ProntuarioRequestDTO;
import com.clinica.gestao_clinica.dto.response.ProntuarioResponseDTO;
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Prontuario;

public class ProntuarioMapper {

    private ProntuarioMapper() {
    }

    public static Prontuario toEntity(
            ProntuarioRequestDTO dto,
            Consulta consulta
    ) {

        Prontuario prontuario = new Prontuario();

        prontuario.setPeso(dto.peso());
        prontuario.setAltura(dto.altura());
        prontuario.setPressao(dto.pressao());
        prontuario.setTemperatura(dto.temperatura());
        prontuario.setDiagnostico(dto.diagnostico());
        prontuario.setTratamento(dto.tratamento());
        prontuario.setObservacao(dto.observacao());
        prontuario.setConsulta(consulta);

        return prontuario;
    }

    public static ProntuarioResponseDTO toResponse(Prontuario prontuario) {

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
}
