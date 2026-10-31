package com.clinica.gestao_clinica.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EspecialidadeCadastroRequestDTO(

        @NotBlank(message = "Nome da especialidade é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nome

) {
}
