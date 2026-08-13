package com.clinica.gestao_clinica.dto.response;

public record MedicoAtualizacaoRequestDTO(

        String nome,
        String email,
        String telefone,
        String crm,
        Long especialidadeId

) {
}
