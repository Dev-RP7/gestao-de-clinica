package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.LoginRequestDTO;
import com.clinica.gestao_clinica.dto.response.LoginResponseDTO;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService  jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        UsernamePasswordAuthenticationToken usernamePassword =
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.senha()
                );

        Authentication auth =
                authenticationManager.authenticate(usernamePassword);

        Usuario usuario = (Usuario) auth.getPrincipal();

        String token = jwtService.gerarToken(usuario);

        return new LoginResponseDTO(token);
    }
}
