package com.clinica.gestao_clinica.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.clinica.gestao_clinica.entity.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(properties.secret());
    }

    public String gerarToken(Usuario usuario) {
        return JWT.create()
                .withIssuer(properties.emissor())
                .withSubject(usuario.getEmail())
                .withClaim("tipo", usuario.getTipoUsuario().name())
                .withExpiresAt(Instant.now().plusSeconds(properties.expiracaoSegundos()))
                .sign(getAlgorithm());
    }

    public String validarToken(String token) {
        try {
            return JWT.require(getAlgorithm())
                    .withIssuer(properties.emissor())
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            log.debug("Token JWT inválido: {}", exception.getMessage());
            return null;
        }
    }

    public long getExpiracaoSegundos() {
        return properties.expiracaoSegundos();
    }
}
