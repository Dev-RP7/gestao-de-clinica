// Pacote dos controllers (camada que recebe as requisições HTTP).
package com.clinica.gestao_clinica.controller;

// DTOs e service.
import com.clinica.gestao_clinica.dto.request.LoginRequestDTO;
import com.clinica.gestao_clinica.dto.response.LoginResponseDTO;
import com.clinica.gestao_clinica.service.AuthService;
// Ativa a validação do DTO.
import jakarta.validation.Valid;
// Resposta HTTP.
import org.springframework.http.ResponseEntity;
// Anotações de rota.
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController: classe que responde requisições HTTP com JSON.
@RestController
// Todas as rotas desta classe começam com /auth.
@RequestMapping("/auth")
// Controller de autenticação.
public class AuthController {

    // Service de autenticação.
    private final AuthService authService;

    // Injeção de dependências.
    public AuthController(AuthService authService) {
        // Guarda o service.
        this.authService = authService;
    }

    // POST /auth/login (rota pública).
    @PostMapping("/login")
    // @RequestBody: lê o JSON do corpo. @Valid: aplica as validações do DTO (se falhar, devolve 400).
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        // Faz o login e devolve 200 OK com o token.
        return ResponseEntity.ok(authService.login(dto));
    }
}
