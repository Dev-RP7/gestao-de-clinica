package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.LoginRequestDTO;
import com.clinica.gestao_clinica.dto.response.LoginResponseDTO;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.security.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        UsernamePasswordAuthenticationToken credenciais =
                new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());

        Authentication autenticacao = authenticationManager.authenticate(credenciais);

        Usuario usuario = (Usuario) autenticacao.getPrincipal();

        String token = jwtService.gerarToken(usuario);

        log.info("Login realizado: {} ({})", usuario.getEmail(), usuario.getTipoUsuario());

        return new LoginResponseDTO(
                token,
                "Bearer",
                jwtService.getExpiracaoSegundos(),
                usuario.getNome(),
                usuario.getTipoUsuario()
        );
    }
}
