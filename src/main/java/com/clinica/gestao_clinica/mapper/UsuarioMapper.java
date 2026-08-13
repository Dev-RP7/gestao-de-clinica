package com.clinica.gestao_clinica.mapper;

import com.clinica.gestao_clinica.dto.response.UsuarioResponseDTO;
import com.clinica.gestao_clinica.entity.Usuario;

public class UsuarioMapper {

    private UsuarioMapper() {}

    public static Usuario toEntity(
            UsuarioResponseDTO dto
    ) {

        Usuario usuario = new Usuario();

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setTelefone(dto.telefone());
        usuario.setTipoUsuario(dto.tipoUsuario());
        usuario.setAtivo(dto.ativo());

        return usuario;
    }

    public static UsuarioResponseDTO toResponse(Usuario usuario) {

        return new  UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getTipoUsuario(),
                usuario.getAtivo()
        );

    }
}
