// Pacote das classes de JWT.
package com.clinica.gestao_clinica.security.jwt;

// Anotação que liga esta classe às propriedades do application.properties.
import org.springframework.boot.context.properties.ConfigurationProperties;
// Anotação que define um valor padrão caso a propriedade não exista.
import org.springframework.boot.context.properties.bind.DefaultValue;

// Lê as propriedades que começam com "api.security.token" (ex.: api.security.token.secret).
@ConfigurationProperties(prefix = "api.security.token")
// Record com as configurações do JWT. O Spring preenche os campos com os valores do application.properties.
public record JwtProperties(

        // Chave secreta usada para assinar os tokens (api.security.token.secret). NUNCA publique a chave real.
        String secret,

        // Por quanto tempo o token vale, em segundos (api.security.token.expiracao-segundos). Padrão: 2 horas.
        @DefaultValue("7200") long expiracaoSegundos,

        // Quem emitiu o token (api.security.token.emissor). Usado para conferir a origem do token.
        @DefaultValue("gestao-clinica") String emissor

) {
}
