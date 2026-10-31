package com.clinica.gestao_clinica.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "api.security.token")
public record JwtProperties(

        String secret,

        @DefaultValue("7200") long expiracaoSegundos,

        @DefaultValue("gestao-clinica") String emissor

) {
}
