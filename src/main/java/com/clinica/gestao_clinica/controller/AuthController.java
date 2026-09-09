package com.clinica.gestao_clinica.controller;

import com.clinica.gestao_clinica.dto.request.LoginRequestDTO;
import com.clinica.gestao_clinica.dto.response.LoginResponseDTO;
import com.clinica.gestao_clinica.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO dto
    ) {

        return ResponseEntity.ok(authService.login(dto));
    }
}
