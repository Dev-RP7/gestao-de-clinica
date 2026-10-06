// Pacote dos DTOs de entrada.
package com.clinica.gestao_clinica.dto.request;

// Anotações de validação.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Dados enviados no login.
public record LoginRequestDTO(

        // Não pode ser vazio.
        @NotBlank(message = "Email é obrigatório")
        // Precisa ter formato de e-mail.
        @Email(message = "Email inválido")
        // E-mail do usuário.
        String email,

        // Não pode ser vazio.
        @NotBlank(message = "Senha é obrigatória")
        // Senha digitada (em texto puro; é comparada com a versão criptografada do banco).
        String senha

) {
}
