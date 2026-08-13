package com.clinica.gestao_clinica.mapper;

import com.clinica.gestao_clinica.dto.response.EspecialidadeResponseDTO;
import com.clinica.gestao_clinica.entity.Especialidade;

public class EspecialidadeMapper {

    private EspecialidadeMapper() {}

    public static Especialidade toEntity(
            EspecialidadeResponseDTO dto
    ) {

        Especialidade especialidade = new  Especialidade();

        especialidade.setNome(dto.nome());

        return especialidade;
    }

    public static EspecialidadeResponseDTO toResponse(Especialidade especialidade) {

        return new EspecialidadeResponseDTO(
                especialidade.getId(),
                especialidade.getNome()
        );
    }
}
