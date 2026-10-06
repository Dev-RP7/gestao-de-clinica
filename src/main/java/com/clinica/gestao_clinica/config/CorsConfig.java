// Pacote das configurações gerais.
package com.clinica.gestao_clinica.config;

// Lê um valor do application.properties.
import org.springframework.beans.factory.annotation.Value;
// Anotações de configuração.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// Classes de configuração de CORS.
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// Lista.
import java.util.List;

// Esta classe define beans.
@Configuration
// CORS: o navegador bloqueia chamadas de um site (ex.: Angular em localhost:4200) para outro endereço
// (a API em localhost:8080), a não ser que a API diga explicitamente que aceita.
public class CorsConfig {

    // Disponibiliza a configuração de CORS. O SecurityConfig a usa através de ".cors(Customizer.withDefaults())".
    @Bean
    // Recebe as origens permitidas do application.properties (separadas por vírgula). Padrão: o Angular local.
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.origens-permitidas:http://localhost:4200}") List<String> origensPermitidas) {

        // Cria o objeto de configuração.
        CorsConfiguration configuracao = new CorsConfiguration();
        // Quais sites podem chamar a API.
        configuracao.setAllowedOrigins(origensPermitidas);
        // Quais métodos HTTP são aceitos.
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Quais cabeçalhos o navegador pode enviar ("*" = todos, incluindo Authorization).
        configuracao.setAllowedHeaders(List.of("*"));
        // Por quantos segundos o navegador pode guardar esta permissão (evita repetir a checagem).
        configuracao.setMaxAge(3600L);

        // Cria a fonte que associa a configuração a caminhos de URL.
        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        // Aplica a configuração para todas as rotas ("/**").
        fonte.registerCorsConfiguration("/**", configuracao);
        // Devolve a fonte para o Spring.
        return fonte;
    }
}
