// Pacote dos DTOs de saída.
package com.clinica.gestao_clinica.dto.response;

// Dados de um médico devolvidos pela API (junta informações de Medico e de Usuario).
public record MedicoResponseDTO(

        // Id do médico.
        Long id,
        // Nome.
        String nome,
        // E-mail.
        String email,
        // Telefone.
        String telefone,
        // CRM.
        String crm,
        // Nome da especialidade.
        String especialidade

) {
}
