// Pacote dos DTOs de saída.
package com.clinica.gestao_clinica.dto.response;

// Enum de perfil.
import com.clinica.gestao_clinica.enums.TipoUsuario;

// Dados de um usuário devolvidos pela API (sem a senha!).
public record UsuarioResponseDTO(

        // Id.
        Long id,
        // Nome.
        String nome,
        // E-mail.
        String email,
        // Telefone.
        String telefone,
        // Perfil.
        TipoUsuario tipoUsuario,
        // Se está ativo.
        Boolean ativo

) {
}
