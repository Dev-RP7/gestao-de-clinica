package com.clinica.gestao_clinica.security;

import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
import com.clinica.gestao_clinica.security.jwt.JwtProperties;
import com.clinica.gestao_clinica.security.jwt.JwtService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(new JwtProperties("chave-de-teste", 3600, "teste"));

    @Test
    void tokenGeradoDeveSerValido() {
        String token = jwtService.gerarToken(usuario());
        assertThat(jwtService.validarToken(token)).isEqualTo("ana@teste.com");
    }

    @Test
    void tokenAdulteradoDeveSerRecusado() {
        String token = jwtService.gerarToken(usuario());
        int inicioAssinatura = token.lastIndexOf('.') + 1;
        char original = token.charAt(inicioAssinatura);
        String adulterado = token.substring(0, inicioAssinatura) + (original == 'A' ? 'B' : 'A') + token.substring(inicioAssinatura + 1);
        assertThat(jwtService.validarToken(adulterado)).isNull();
    }

    @Test
    void tokenDeOutraChaveDeveSerRecusado() {
        JwtService outro = new JwtService(new JwtProperties("outra-chave", 3600, "teste"));
        assertThat(jwtService.validarToken(outro.gerarToken(usuario()))).isNull();
    }

    @Test
    void tokenExpiradoDeveSerRecusado() {
        JwtService expirado = new JwtService(new JwtProperties("chave-de-teste", -10, "teste"));
        assertThat(jwtService.validarToken(expirado.gerarToken(usuario()))).isNull();
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setEmail("ana@teste.com");
        usuario.setTipoUsuario(TipoUsuario.MEDICO);
        return usuario;
    }
}
