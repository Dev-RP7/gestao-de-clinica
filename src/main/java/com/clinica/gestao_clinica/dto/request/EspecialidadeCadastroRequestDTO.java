package com.clinica.gestao_clinica.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EspecialidadeCadastroRequestDTO(


        @NotBlank(message = "Nome da especialidade é obrigatório")
        String nome

) {
}
