// Pacote dos DTOs de entrada.
package com.clinica.gestao_clinica.dto.request;

// Anotações de validação.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Dados para cadastrar um novo ADMINISTRADOR. Médicos e pacientes têm rotas próprias (/medicos e /pacientes).
public record UsuarioCadastroRequestDTO(

        // Não pode ser vazio.
        @NotBlank(message = "Nome é obrigatório")
        // Nome.
        String nome,

        // Não pode ser vazio.
        @NotBlank(message = "Email é obrigatório")
        // Formato de e-mail.
        @Email(message = "Email inválido")
        // E-mail.
        String email,

        // Não pode ser vazio.
        @NotBlank(message = "Senha é obrigatória")
        // Mínimo de 6 caracteres.
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        // Senha.
        String senha,

        // Não pode ser vazio.
        @NotBlank(message = "Telefone é obrigatório")
        // Máximo de 15 caracteres.
        @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
        // Telefone.
        String telefone

) {
}
