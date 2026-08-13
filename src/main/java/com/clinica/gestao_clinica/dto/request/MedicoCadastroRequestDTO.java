package com.clinica.gestao_clinica.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MedicoCadastroRequestDTO(

        @NotBlank(message = "CRM é obrigatório")
        String crm,

        @NotNull(message = "Especialidade é obrigatório")
        Long especialidadeId,

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha,

        @NotBlank(message = "Telefone é obrigatório")
        String telefone

) {
}
