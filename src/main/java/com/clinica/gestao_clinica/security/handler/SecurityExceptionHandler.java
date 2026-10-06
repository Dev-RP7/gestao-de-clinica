// Pacote dos tratadores de erro de segurança.
package com.clinica.gestao_clinica.security.handler;

// Classes de servlet.
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Permite escolher qual bean injetar quando existem vários do mesmo tipo.
import org.springframework.beans.factory.annotation.Qualifier;
// Exceções do Spring Security.
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
// Interfaces chamadas pelo Spring Security quando ocorre 401 ou 403.
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
// Marca a classe como componente do Spring.
import org.springframework.stereotype.Component;
// Mecanismo do Spring MVC que encaminha exceções para o @RestControllerAdvice.
import org.springframework.web.servlet.HandlerExceptionResolver;

// O Spring cria e gerencia esta classe.
@Component
// Os erros 401/403 acontecem nos FILTROS, antes de chegar no controller, então o GlobalExceptionHandler
// não os veria sozinho. Esta classe "repassa" esses erros para ele, mantendo o mesmo formato JSON.
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    // Resolvedor de exceções do Spring MVC.
    private final HandlerExceptionResolver resolver;

    // @Qualifier escolhe o bean chamado "handlerExceptionResolver" (o que conhece o @RestControllerAdvice).
    public SecurityExceptionHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        // Guarda o resolvedor.
        this.resolver = resolver;
    }

    // Implementa AuthenticationEntryPoint.
    @Override
    // Chamado quando alguém sem login (ou com token inválido) acessa uma rota protegida -> 401.
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) {
        // Encaminha a exceção para o GlobalExceptionHandler.
        resolver.resolveException(request, response, null, ex);
    }

    // Implementa AccessDeniedHandler.
    @Override
    // Chamado quando o usuário está logado mas não tem a permissão necessária -> 403.
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) {
        // Encaminha a exceção para o GlobalExceptionHandler.
        resolver.resolveException(request, response, null, ex);
    }
}
