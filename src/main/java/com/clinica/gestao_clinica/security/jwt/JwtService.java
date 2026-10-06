// Pacote das classes de JWT.
package com.clinica.gestao_clinica.security.jwt;

// Classes da biblioteca java-jwt (Auth0).
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
// Nossa entidade de usuário.
import com.clinica.gestao_clinica.entity.Usuario;
// Lombok: cria o campo "log".
import lombok.extern.slf4j.Slf4j;
// Marca a classe como serviço gerenciado pelo Spring.
import org.springframework.stereotype.Service;

// Representa um instante no tempo (usado na expiração).
import java.time.Instant;

// Cria o "log".
@Slf4j
// O Spring cria uma instância desta classe e injeta onde for pedida.
@Service
// Serviço responsável por GERAR e VALIDAR tokens JWT.
public class JwtService {

    // Configurações do JWT (segredo, expiração, emissor).
    private final JwtProperties properties;

    // Injeção de dependência pelo construtor: o Spring passa o JwtProperties automaticamente.
    public JwtService(JwtProperties properties) {
        // Guarda as configurações no campo.
        this.properties = properties;
    }

    // Cria o algoritmo de assinatura HMAC-SHA256 usando a chave secreta.
    private Algorithm getAlgorithm() {
        // Quem não tem a chave não consegue gerar um token válido.
        return Algorithm.HMAC256(properties.secret());
    }

    // Gera um token JWT para o usuário que acabou de fazer login.
    public String gerarToken(Usuario usuario) {
        // Começa a montar o token.
        return JWT.create()
                // "iss" (issuer): quem emitiu o token.
                .withIssuer(properties.emissor())
                // "sub" (subject): de quem é o token. Usamos o e-mail.
                .withSubject(usuario.getEmail())
                // Informação extra (claim): o perfil do usuário.
                .withClaim("tipo", usuario.getTipoUsuario().name())
                // "exp": a partir de quando o token deixa de valer.
                .withExpiresAt(Instant.now().plusSeconds(properties.expiracaoSegundos()))
                // Assina o token com a chave secreta e transforma em texto.
                .sign(getAlgorithm());
    }

    // Valida o token e devolve o e-mail do dono. Se o token for inválido ou expirado, devolve null.
    public String validarToken(String token) {
        // Tenta validar.
        try {
            // Cria um verificador com o mesmo algoritmo e emissor.
            return JWT.require(getAlgorithm())
                    // Exige que o emissor seja o nosso.
                    .withIssuer(properties.emissor())
                    // Monta o verificador.
                    .build()
                    // Confere assinatura e expiração (lança exceção se algo estiver errado).
                    .verify(token)
                    // Pega o "sub", que é o e-mail do usuário.
                    .getSubject();
        // Se a verificação falhar...
        } catch (JWTVerificationException exception) {
            // ...registra em nível debug (não é erro do servidor, é token ruim do cliente).
            log.debug("Token JWT inválido: {}", exception.getMessage());
            // ...e devolve null para indicar "token inválido".
            return null;
        }
    }

    // Informa por quantos segundos o token vale (enviado ao frontend na resposta do login).
    public long getExpiracaoSegundos() {
        // Devolve o valor configurado.
        return properties.expiracaoSegundos();
    }
}
