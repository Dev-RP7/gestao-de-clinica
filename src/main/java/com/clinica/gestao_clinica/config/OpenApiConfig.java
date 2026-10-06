// Pacote das configurações gerais.
package com.clinica.gestao_clinica.config;

// Classes do OpenAPI (Swagger) para descrever a API.
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
// Anotações de configuração.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Esta classe define beans.
@Configuration
// Configura a documentação Swagger, disponível em http://localhost:8080/swagger-ui.html.
public class OpenApiConfig {

    // Nome do esquema de segurança (usado em dois lugares abaixo).
    private static final String ESQUEMA_JWT = "bearerAuth";

    // Disponibiliza a descrição da API para o springdoc.
    @Bean
    // Monta o objeto OpenAPI.
    public OpenAPI customOpenAPI() {
        // Cria a descrição.
        return new OpenAPI()
                // Informações gerais exibidas no topo da página.
                .info(new Info()
                        // Título.
                        .title("API Gestão de Clínica")
                        // Versão.
                        .version("1.0")
                        // Descrição.
                        .description("API RESTful para gestão de clínicas. Faça login em /auth/login, "
                                + "copie o token e clique em 'Authorize' para testar as rotas protegidas."))
                // Registra o esquema "Bearer JWT", que cria o botão "Authorize" no Swagger.
                .components(new Components()
                        // Adiciona o esquema com o nome definido acima.
                        .addSecuritySchemes(ESQUEMA_JWT, new SecurityScheme()
                                // Tipo HTTP.
                                .type(SecurityScheme.Type.HTTP)
                                // Esquema "bearer" (cabeçalho Authorization: Bearer <token>).
                                .scheme("bearer")
                                // Formato do token.
                                .bearerFormat("JWT")))
                // Aplica o esquema a todas as rotas (o Swagger envia o token automaticamente).
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_JWT));
    }
}
