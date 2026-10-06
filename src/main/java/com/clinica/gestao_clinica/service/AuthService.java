// Pacote dos services (regras de negócio).
package com.clinica.gestao_clinica.service;

// DTOs, entidade e serviço de JWT.
import com.clinica.gestao_clinica.dto.request.LoginRequestDTO;
import com.clinica.gestao_clinica.dto.response.LoginResponseDTO;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.security.jwt.JwtService;
// Lombok: cria o campo "log".
import lombok.extern.slf4j.Slf4j;
// Classes de autenticação do Spring Security.
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
// Marca a classe como service.
import org.springframework.stereotype.Service;

// Cria o "log".
@Slf4j
// O Spring cria e gerencia esta classe.
@Service
// Service responsável pelo login.
public class AuthService {

    // Gerenciador que confere e-mail e senha.
    private final AuthenticationManager authenticationManager;
    // Serviço que gera o token.
    private final JwtService jwtService;

    // Injeção de dependências pelo construtor.
    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        // Guarda o gerenciador.
        this.authenticationManager = authenticationManager;
        // Guarda o serviço de JWT.
        this.jwtService = jwtService;
    }

    // Faz o login e devolve o token.
    public LoginResponseDTO login(LoginRequestDTO dto) {
        // Empacota e-mail e senha no formato que o Spring Security entende.
        UsernamePasswordAuthenticationToken credenciais =
                new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());

        // O Spring busca o usuário (CustomUserDetailsService) e compara a senha com BCrypt.
        // Se estiver errado, lança BadCredentialsException (o GlobalExceptionHandler devolve 401).
        Authentication autenticacao = authenticationManager.authenticate(credenciais);

        // O "principal" é o usuário autenticado. Como Usuario implementa UserDetails, fazemos o cast.
        Usuario usuario = (Usuario) autenticacao.getPrincipal();

        // Gera o token JWT para este usuário.
        String token = jwtService.gerarToken(usuario);

        // Registra o login no log.
        log.info("Login realizado: {} ({})", usuario.getEmail(), usuario.getTipoUsuario());

        // Devolve o token e os dados úteis para o frontend.
        return new LoginResponseDTO(
                // O token.
                token,
                // O tipo do token.
                "Bearer",
                // Validade em segundos.
                jwtService.getExpiracaoSegundos(),
                // Nome do usuário.
                usuario.getNome(),
                // Perfil do usuário.
                usuario.getTipoUsuario()
        );
    }
}
