package com.clinica.gestao_clinica.dto.response;

public record MedicoResponseDTO(

        Long id,
        String nome,
        String email,
        String telefone,
        String crm,
        String especialidade

) {
}
