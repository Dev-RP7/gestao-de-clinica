// Pacote dos mappers.
package com.clinica.gestao_clinica.mapper;

// DTO e entidade.
import com.clinica.gestao_clinica.dto.response.UsuarioResponseDTO;
import com.clinica.gestao_clinica.entity.Usuario;

// Conversões de Usuario.
public class UsuarioMapper {

    // Impede instanciar a classe.
    private UsuarioMapper() {
    }

    // Converte a entidade em DTO de resposta (sem a senha).
    public static UsuarioResponseDTO toResponse(Usuario usuario) {
        // Monta o record.
        return new UsuarioResponseDTO(
                // Id.
                usuario.getId(),
                // Nome.
                usuario.getNome(),
                // E-mail.
                usuario.getEmail(),
                // Telefone.
                usuario.getTelefone(),
                // Perfil.
                usuario.getTipoUsuario(),
                // Ativo.
                usuario.getAtivo()
        );
    }
}
