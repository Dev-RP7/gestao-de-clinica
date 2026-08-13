package com.clinica.gestao_clinica.mapper;

import com.clinica.gestao_clinica.dto.request.ReceitaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ReceitaResponseDTO;
import com.clinica.gestao_clinica.entity.Prontuario;
import com.clinica.gestao_clinica.entity.Receita;

public class ReceitaMapper {

    private ReceitaMapper() {
    }

    public static Receita toEntity(
            ReceitaRequestDTO dto,
            Prontuario prontuario
    ) {

        Receita receita = new Receita();

        receita.setMedicamento(dto.medicamento());
        receita.setDosagem(dto.dosagem());
        receita.setFrequencia(dto.frequencia());
        receita.setDuracao(dto.duracao());
        receita.setObservacao(dto.observacao());
        receita.setProntuario(prontuario);

        return receita;
    }

    public static ReceitaResponseDTO toResponse(Receita receita) {

        return new ReceitaResponseDTO(
                receita.getId(),
                receita.getProntuario().getId(),
                receita.getMedicamento(),
                receita.getDosagem(),
                receita.getFrequencia(),
                receita.getDuracao(),
                receita.getObservacao()

        );
    }
}
