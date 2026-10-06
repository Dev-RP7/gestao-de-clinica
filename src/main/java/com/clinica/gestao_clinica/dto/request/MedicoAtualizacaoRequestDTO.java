// Pacote dos DTOs de entrada (este arquivo estava por engano no pacote "response").
package com.clinica.gestao_clinica.dto.request;

// Anotações de validação.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

// Dados para atualizar um médico. Todos os campos são opcionais: só o que vier preenchido é alterado.
public record MedicoAtualizacaoRequestDTO(

        // Novo nome (opcional).
        String nome,

        // Se vier, precisa ter formato de e-mail.
        @Email(message = "Email inválido")
        // Novo e-mail (opcional).
        String email,

        // Máximo de 15 caracteres.
        @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
        // Novo telefone (opcional).
        String telefone,

        // Novo CRM (opcional).
        String crm,

        // Nova especialidade (opcional).
        Long especialidadeId

) {
}
