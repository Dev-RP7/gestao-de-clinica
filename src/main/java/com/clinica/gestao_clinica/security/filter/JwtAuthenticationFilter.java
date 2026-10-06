// Pacote dos filtros de segurança.
package com.clinica.gestao_clinica.security.filter;

// Serviço que valida o token.
import com.clinica.gestao_clinica.security.jwt.JwtService;
// Classes de servlet (requisição, resposta e cadeia de filtros).
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Lombok: cria o campo "log".
import lombok.extern.slf4j.Slf4j;
// Objeto que representa "este usuário está autenticado".
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// Guarda quem está logado durante a requisição.
import org.springframework.security.core.context.SecurityContextHolder;
// Interfaces e exceção para carregar o usuário.
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// Adiciona detalhes da requisição (IP etc.) à autenticação.
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// Classe base de filtro que garante execução uma única vez por requisição.
import org.springframework.web.filter.OncePerRequestFilter;

// Exceção de entrada/saída.
import java.io.IOException;

// Cria o "log".
@Slf4j
// Filtro que roda ANTES dos controllers: lê o token do cabeçalho e, se for válido, marca o usuário como logado.
// Não usamos @Component aqui de propósito: o filtro é criado no SecurityConfig, assim ele roda só dentro do Spring Security.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Prefixo padrão do cabeçalho: "Authorization: Bearer <token>".
    private static final String PREFIXO_BEARER = "Bearer ";

    // Serviço que valida o token.
    private final JwtService jwtService;
    // Serviço que busca o usuário no banco.
    private final UserDetailsService userDetailsService;

    // Construtor que recebe as dependências.
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        // Guarda o JwtService.
        this.jwtService = jwtService;
        // Guarda o UserDetailsService.
        this.userDetailsService = userDetailsService;
    }

    // Implementa o método principal do filtro.
    @Override
    // Executado em cada requisição HTTP.
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Lê o cabeçalho Authorization da requisição.
        String cabecalho = request.getHeader("Authorization");

        // Só tentamos autenticar se o cabeçalho existir e começar com "Bearer ".
        if (cabecalho != null && cabecalho.startsWith(PREFIXO_BEARER)) {
            // Remove o "Bearer " e fica só com o token.
            String token = cabecalho.substring(PREFIXO_BEARER.length());
            // Valida o token e pega o e-mail do dono (ou null se inválido).
            String email = jwtService.validarToken(token);

            // Se o token é válido e ainda não há ninguém autenticado nesta requisição...
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // ...autentica o usuário.
                autenticar(email, request);
            }
        }

        // Passa a requisição para o próximo filtro. Se ninguém foi autenticado,
        // o Spring Security vai responder 401 nas rotas protegidas.
        filterChain.doFilter(request, response);
    }

    // Carrega o usuário e o registra como autenticado no SecurityContext.
    private void autenticar(String email, HttpServletRequest request) {
        // Tenta carregar.
        try {
            // Busca o usuário no banco pelo e-mail que estava no token.
            UserDetails usuario = userDetailsService.loadUserByUsername(email);

            // Usuário desativado depois de gerar o token não deve continuar acessando.
            if (!usuario.isEnabled()) {
                // Registra no log.
                log.warn("Token de usuário desativado recusado: {}", email);
                // Sai sem autenticar.
                return;
            }

            // Cria o objeto de autenticação: (usuário, credenciais = null, permissões).
            UsernamePasswordAuthenticationToken autenticacao =
                    new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            // Anexa detalhes da requisição (como IP).
            autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // Guarda a autenticação: a partir daqui o Spring considera o usuário logado.
            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        // Se o usuário do token foi apagado do banco...
        } catch (UsernameNotFoundException ex) {
            // ...apenas registra e segue sem autenticar.
            log.warn("Token com usuário inexistente: {}", email);
        }
    }
}
