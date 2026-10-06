// Pacote das configurações gerais.
package com.clinica.gestao_clinica.config;

// Classes de servlet.
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Lombok: cria o campo "log".
import lombok.extern.slf4j.Slf4j;
// MDC: "etiquetas" que aparecem em todas as linhas de log da mesma requisição.
import org.slf4j.MDC;
// Controle da ordem dos filtros.
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
// Marca a classe como componente do Spring.
import org.springframework.stereotype.Component;
// Classe base de filtro executado uma vez por requisição.
import org.springframework.web.filter.OncePerRequestFilter;

// Exceção de entrada/saída e gerador de ids aleatórios.
import java.io.IOException;
import java.util.UUID;

// Cria o "log".
@Slf4j
// O Spring registra este filtro automaticamente.
@Component
// HIGHEST_PRECEDENCE: roda ANTES de todos os outros (inclusive do Spring Security), para medir tudo, até os erros 401.
@Order(Ordered.HIGHEST_PRECEDENCE)
// Filtro que registra no log cada requisição: método, caminho, status da resposta e tempo gasto.
public class RequisicaoLogFilter extends OncePerRequestFilter {

    // Implementa o método do filtro.
    @Override
    // Executado em cada requisição.
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Gera um id curto (8 caracteres) para identificar esta requisição nos logs.
        String idRequisicao = UUID.randomUUID().toString().substring(0, 8);
        // Coloca o id no MDC: o padrão de log no application.properties o imprime em cada linha.
        MDC.put("requestId", idRequisicao);
        // Marca o horário de início em milissegundos.
        long inicio = System.currentTimeMillis();

        // try/finally garante que o log é escrito mesmo se der erro.
        try {
            // Deixa a requisição seguir para os próximos filtros e para o controller.
            filterChain.doFilter(request, response);
        // Executa sempre no final.
        } finally {
            // Calcula quanto tempo levou.
            long duracao = System.currentTimeMillis() - inicio;
            // Escreve uma linha como: "GET /consultas -> 200 (35 ms)".
            log.info("{} {} -> {} ({} ms)", request.getMethod(), request.getRequestURI(), response.getStatus(), duracao);
            // Limpa o MDC para não "vazar" o id para a próxima requisição que usar esta mesma thread.
            MDC.remove("requestId");
        }
    }

    // Implementa um método opcional do filtro.
    @Override
    // Retorna true para as requisições que NÃO devem ser registradas.
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Ignora os arquivos do Swagger, que gerariam muitas linhas inúteis no log.
        return request.getRequestURI().startsWith("/swagger-ui") || request.getRequestURI().startsWith("/v3/api-docs");
    }
}
