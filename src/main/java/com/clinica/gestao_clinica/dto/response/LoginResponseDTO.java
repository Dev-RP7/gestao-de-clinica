// Pacote dos DTOs de saída.
package com.clinica.gestao_clinica.dto.response;

// Enum de perfil.
import com.clinica.gestao_clinica.enums.TipoUsuario;

// Resposta do login: o token e informações úteis para o frontend montar a tela certa.
public record LoginResponseDTO(

        // O token JWT, que deve ser enviado no cabeçalho "Authorization: Bearer <token>".
        String token,
        // Tipo do token (sempre "Bearer").
        String tipo,
        // Em quantos segundos o token expira.
        long expiraEmSegundos,
        // Nome do usuário logado.
        String nome,
        // Perfil do usuário logado (o frontend usa para mostrar/esconder menus).
        TipoUsuario tipoUsuario

) {
}
