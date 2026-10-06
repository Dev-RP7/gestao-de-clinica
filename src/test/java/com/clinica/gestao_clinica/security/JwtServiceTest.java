// Pacote dos testes de segurança.
package com.clinica.gestao_clinica.security;

// Entidade e enum.
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
// Classes testadas.
import com.clinica.gestao_clinica.security.jwt.JwtProperties;
import com.clinica.gestao_clinica.security.jwt.JwtService;
// JUnit.
import org.junit.jupiter.api.Test;

// AssertJ.
import static org.assertj.core.api.Assertions.assertThat;

// Teste unitário puro: criamos o JwtService "na mão", sem Spring.
class JwtServiceTest {

    // Serviço configurado com uma chave de teste, 1 hora de validade e emissor "teste".
    private final JwtService jwtService = new JwtService(new JwtProperties("chave-de-teste", 3600, "teste"));

    // Marca como teste.
    @Test
    // Um token gerado deve ser validado e devolver o e-mail do usuário.
    void tokenGeradoDeveSerValido() {
        // Gera o token.
        String token = jwtService.gerarToken(usuario());
        // Valida e confere o e-mail.
        assertThat(jwtService.validarToken(token)).isEqualTo("ana@teste.com");
    }

    // Marca como teste.
    @Test
    // Um token alterado deve ser recusado (a assinatura não bate mais).
    void tokenAdulteradoDeveSerRecusado() {
        // Gera um token válido.
        String token = jwtService.gerarToken(usuario());
        // Um JWT tem 3 partes separadas por ponto: cabeçalho.conteúdo.assinatura. Pegamos onde começa a assinatura.
        int inicioAssinatura = token.lastIndexOf('.') + 1;
        // Primeiro caractere da assinatura.
        char original = token.charAt(inicioAssinatura);
        // Troca esse caractere por outro, simulando uma adulteração.
        String adulterado = token.substring(0, inicioAssinatura) + (original == 'A' ? 'B' : 'A') + token.substring(inicioAssinatura + 1);
        // A validação deve devolver null.
        assertThat(jwtService.validarToken(adulterado)).isNull();
    }

    // Marca como teste.
    @Test
    // Um token assinado com OUTRA chave deve ser recusado.
    void tokenDeOutraChaveDeveSerRecusado() {
        // Serviço com chave diferente.
        JwtService outro = new JwtService(new JwtProperties("outra-chave", 3600, "teste"));
        // Gera o token com a outra chave e valida com a nossa.
        assertThat(jwtService.validarToken(outro.gerarToken(usuario()))).isNull();
    }

    // Marca como teste.
    @Test
    // Um token expirado deve ser recusado.
    void tokenExpiradoDeveSerRecusado() {
        // Serviço com validade NEGATIVA: o token já nasce expirado.
        JwtService expirado = new JwtService(new JwtProperties("chave-de-teste", -10, "teste"));
        // Gera com o serviço "expirado" e valida com o normal (mesma chave).
        assertThat(jwtService.validarToken(expirado.gerarToken(usuario()))).isNull();
    }

    // Cria um usuário de exemplo.
    private Usuario usuario() {
        // Usuário vazio.
        Usuario usuario = new Usuario();
        // E-mail.
        usuario.setEmail("ana@teste.com");
        // Perfil.
        usuario.setTipoUsuario(TipoUsuario.MEDICO);
        // Devolve.
        return usuario;
    }
}
