// Pacote dos DTOs de entrada.
package com.clinica.gestao_clinica.dto.request;

// Anotações de validação.
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Dados para cadastrar ou renomear uma especialidade.
public record EspecialidadeCadastroRequestDTO(

        // Não pode ser vazio.
        @NotBlank(message = "Nome da especialidade é obrigatório")
        // No máximo 100 caracteres.
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        // Nome da especialidade.
        String nome

) {
}
