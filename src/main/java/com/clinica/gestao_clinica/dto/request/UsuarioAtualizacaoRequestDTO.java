// Pacote dos DTOs de entrada.
package com.clinica.gestao_clinica.dto.request;

// Anotações de validação.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

// Dados para atualizar um usuário. Todos opcionais: só os campos preenchidos são alterados.
public record UsuarioAtualizacaoRequestDTO(

        // Novo nome.
        String nome,

        // Se vier, precisa ter formato de e-mail.
        @Email(message = "Email inválido")
        // Novo e-mail.
        String email,

        // Máximo de 15 caracteres.
        @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
        // Novo telefone.
        String telefone

) {
}
