// Pacote dos DTOs de resposta.
package com.clinica.gestao_clinica.dto.response;

// Anotação do Jackson (biblioteca que converte objetos Java em JSON).
import com.fasterxml.jackson.annotation.JsonInclude;

// Data e hora.
import java.time.LocalDateTime;
// Lista.
import java.util.List;

// NON_NULL: campos nulos (ex.: "campos" quando não é erro de validação) não aparecem no JSON.
@JsonInclude(JsonInclude.Include.NON_NULL)
// Formato padrão de TODAS as respostas de erro da API. Assim o frontend sempre sabe o que esperar.
// "record" é uma classe imutável: o Java gera construtor, getters (status(), mensagem()...), equals e toString.
public record ErroResponseDTO(

        // Momento em que o erro aconteceu.
        LocalDateTime timestamp,
        // Código HTTP (400, 401, 403, 404, 409, 422, 500).
        int status,
        // Nome curto do erro (ex.: "Not Found").
        String erro,
        // Mensagem explicando o problema em português.
        String mensagem,
        // Caminho da requisição que falhou (ex.: /consultas/10).
        String caminho,
        // Lista de erros por campo (só aparece em erros de validação).
        List<CampoErro> campos

) {

    // Record aninhado: representa o erro de um campo específico do JSON enviado.
    public record CampoErro(
            // Nome do campo (ex.: "email").
            String campo,
            // O que está errado nele (ex.: "Email inválido").
            String mensagem
    ) {
    }
}
