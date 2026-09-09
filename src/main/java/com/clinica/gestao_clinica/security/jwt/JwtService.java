package com.clinica.gestao_clinica.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.clinica.gestao_clinica.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class JwtService {

    @Value("${api.security.token.secret}")
    private String secret;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    public String gerarToken(Usuario usuario) {

        try {

            return JWT.create()
                    .withIssuer("gestao-clinica")
                    .withSubject(usuario.getEmail())
                    .withClaim("tipo", usuario.getTipoUsuario().name())
                    .withExpiresAt(Instant.now().plusSeconds(7200))
                    .sign(getAlgorithm());

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar JWT");
        }
    }

    public String validarToken(String token) {

        try {
            return JWT.require(getAlgorithm())
                    .withIssuer("gestao-clinica")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}
