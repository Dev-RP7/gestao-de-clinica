// Pacote das exceções.
package com.clinica.gestao_clinica.exception;

// DTO com o formato padrão de erro.
import com.clinica.gestao_clinica.dto.response.ErroResponseDTO;
// Representa a requisição HTTP (usamos para pegar o caminho).
import jakarta.servlet.http.HttpServletRequest;
// Lombok: cria o "log" para escrever mensagens no console/arquivo.
import lombok.extern.slf4j.Slf4j;
// Exceção lançada pelo banco quando uma restrição é violada (ex.: valor único repetido).
import org.springframework.dao.DataIntegrityViolationException;
// Status HTTP e corpo da resposta.
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Exceção lançada quando o JSON enviado está malformado.
import org.springframework.http.converter.HttpMessageNotReadableException;
// Exceções do Spring Security.
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
// Exceção lançada quando um DTO com @Valid tem campos inválidos.
import org.springframework.web.bind.MethodArgumentNotValidException;
// Exceção lançada quando falta um parâmetro obrigatório na URL.
import org.springframework.web.bind.MissingServletRequestParameterException;
// Anotações que registram esta classe como "tratador global de erros".
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
// Exceção lançada quando um parâmetro da URL tem tipo errado (ex.: /consultas/abc).
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
// Exceções para método HTTP não suportado e rota inexistente.
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

// Data/hora e lista.
import java.time.LocalDateTime;
import java.util.List;

// Lombok: cria o campo "log" (private static final Logger log = ...).
@Slf4j
// Esta classe "escuta" exceções lançadas em qualquer controller e devolve uma resposta JSON.
@RestControllerAdvice
// Tratador global de exceções.
public class GlobalExceptionHandler {

    // ===== 400 - Bad Request: os dados enviados estão errados =====

    // Este método trata erros de validação dos DTOs (@NotBlank, @Email...).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    // Recebe a exceção e a requisição; devolve a resposta com status 400.
    public ResponseEntity<ErroResponseDTO> tratarValidacao(MethodArgumentNotValidException ex, HttpServletRequest request) {
        // Pega todos os erros de campo e transforma cada um em um CampoErro.
        List<ErroResponseDTO.CampoErro> campos = ex.getBindingResult()
                // Lista de erros por campo.
                .getFieldErrors()
                // Transforma a lista em um "stream" para processar item a item.
                .stream()
                // Para cada erro, cria um CampoErro com o nome do campo e a mensagem.
                .map(erro -> new ErroResponseDTO.CampoErro(erro.getField(), erro.getDefaultMessage()))
                // Converte o stream de volta em lista.
                .toList();

        // Registra um aviso no log com o caminho e os campos inválidos.
        log.warn("Validação falhou em {}: {}", request.getRequestURI(), campos);
        // Monta e devolve a resposta padrão com a lista de campos.
        return montarResposta(HttpStatus.BAD_REQUEST, "Dados inválidos. Verifique os campos.", request, campos);
    }

    // Trata JSON malformado ou com valores impossíveis de converter (ex.: data "31/02", enum inexistente).
    @ExceptionHandler(HttpMessageNotReadableException.class)
    // Devolve 400.
    public ResponseEntity<ErroResponseDTO> tratarJsonInvalido(HttpMessageNotReadableException ex, HttpServletRequest request) {
        // Registra um aviso no log.
        log.warn("JSON inválido em {}: {}", request.getRequestURI(), ex.getMessage());
        // Responde com uma mensagem amigável (a mensagem original é técnica demais).
        return montarResposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou malformado.", request, null);
    }

    // Trata parâmetros da URL com tipo errado (ex.: /medicos/abc, status=XYZ).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    // Devolve 400.
    public ResponseEntity<ErroResponseDTO> tratarTipoInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        // Monta uma mensagem dizendo qual parâmetro e qual valor estão errados.
        String mensagem = "Valor '" + ex.getValue() + "' inválido para o parâmetro '" + ex.getName() + "'.";
        // Registra um aviso no log.
        log.warn("{} em {}", mensagem, request.getRequestURI());
        // Devolve a resposta padrão.
        return montarResposta(HttpStatus.BAD_REQUEST, mensagem, request, null);
    }

    // Trata parâmetros obrigatórios que não foram enviados.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    // Devolve 400.
    public ResponseEntity<ErroResponseDTO> tratarParametroAusente(MissingServletRequestParameterException ex, HttpServletRequest request) {
        // Monta a mensagem com o nome do parâmetro.
        String mensagem = "Parâmetro obrigatório '" + ex.getParameterName() + "' não informado.";
        // Devolve a resposta padrão.
        return montarResposta(HttpStatus.BAD_REQUEST, mensagem, request, null);
    }

    // ===== 401 - Unauthorized: não está logado ou credenciais erradas =====

    // Trata e-mail/senha incorretos no login.
    @ExceptionHandler(BadCredentialsException.class)
    // Devolve 401.
    public ResponseEntity<ErroResponseDTO> tratarCredenciaisInvalidas(BadCredentialsException ex, HttpServletRequest request) {
        // Registra no log (sem mostrar a senha, claro).
        log.warn("Tentativa de login com credenciais inválidas em {}", request.getRequestURI());
        // Mensagem genérica de propósito: não revelamos se o e-mail existe.
        return montarResposta(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.", request, null);
    }

    // Trata login de usuário desativado.
    @ExceptionHandler(DisabledException.class)
    // Devolve 401.
    public ResponseEntity<ErroResponseDTO> tratarUsuarioDesativado(DisabledException ex, HttpServletRequest request) {
        // Devolve a resposta padrão.
        return montarResposta(HttpStatus.UNAUTHORIZED, "Usuário desativado.", request, null);
    }

    // Trata qualquer outra falha de autenticação (ex.: token ausente ou inválido).
    @ExceptionHandler(AuthenticationException.class)
    // Devolve 401.
    public ResponseEntity<ErroResponseDTO> tratarNaoAutenticado(AuthenticationException ex, HttpServletRequest request) {
        // Devolve a resposta padrão.
        return montarResposta(HttpStatus.UNAUTHORIZED, "Autenticação necessária. Envie um token JWT válido.", request, null);
    }

    // ===== 403 - Forbidden: está logado, mas não tem permissão =====

    // Trata acesso negado (ex.: paciente tentando acessar /usuarios).
    @ExceptionHandler(AccessDeniedException.class)
    // Devolve 403.
    public ResponseEntity<ErroResponseDTO> tratarAcessoNegado(AccessDeniedException ex, HttpServletRequest request) {
        // Registra o aviso no log.
        log.warn("Acesso negado em {}", request.getRequestURI());
        // Devolve a resposta padrão.
        return montarResposta(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este recurso.", request, null);
    }

    // ===== 404 - Not Found =====

    // Trata nossa exceção de recurso não encontrado.
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    // Devolve 404.
    public ResponseEntity<ErroResponseDTO> tratarNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest request) {
        // Registra no log.
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        // Devolve a resposta padrão usando a mensagem da exceção.
        return montarResposta(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // Trata rotas que não existem (ex.: GET /rota-inexistente).
    @ExceptionHandler(NoResourceFoundException.class)
    // Devolve 404.
    public ResponseEntity<ErroResponseDTO> tratarRotaInexistente(NoResourceFoundException ex, HttpServletRequest request) {
        // Devolve a resposta padrão.
        return montarResposta(HttpStatus.NOT_FOUND, "Rota não encontrada.", request, null);
    }

    // ===== 405 - Method Not Allowed =====

    // Trata método HTTP errado (ex.: PATCH em uma rota que só aceita GET).
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    // Devolve 405.
    public ResponseEntity<ErroResponseDTO> tratarMetodoNaoSuportado(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        // Monta a mensagem com o método usado.
        String mensagem = "Método " + ex.getMethod() + " não é suportado nesta rota.";
        // Devolve a resposta padrão.
        return montarResposta(HttpStatus.METHOD_NOT_ALLOWED, mensagem, request, null);
    }

    // ===== 409 - Conflict: dados duplicados =====

    // Trata nossa exceção de conflito.
    @ExceptionHandler(ConflitoException.class)
    // Devolve 409.
    public ResponseEntity<ErroResponseDTO> tratarConflito(ConflitoException ex, HttpServletRequest request) {
        // Registra no log.
        log.warn("Conflito: {}", ex.getMessage());
        // Devolve a resposta padrão.
        return montarResposta(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    // Trata violações de restrição do banco que escaparam das nossas validações (ex.: excluir especialidade em uso).
    @ExceptionHandler(DataIntegrityViolationException.class)
    // Devolve 409.
    public ResponseEntity<ErroResponseDTO> tratarIntegridade(DataIntegrityViolationException ex, HttpServletRequest request) {
        // Registra no log a causa técnica (útil para quem mantém o sistema).
        log.warn("Violação de integridade em {}: {}", request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        // Para o usuário, uma mensagem simples.
        return montarResposta(HttpStatus.CONFLICT, "Operação viola uma restrição do banco de dados (registro duplicado ou em uso).", request, null);
    }

    // ===== 422 - Unprocessable Entity: regra de negócio violada =====

    // Trata nossa exceção de regra de negócio.
    @ExceptionHandler(RegraNegocioException.class)
    // Devolve 422.
    public ResponseEntity<ErroResponseDTO> tratarRegraNegocio(RegraNegocioException ex, HttpServletRequest request) {
        // Registra no log.
        log.warn("Regra de negócio violada: {}", ex.getMessage());
        // Devolve a resposta padrão.
        return montarResposta(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), request, null);
    }

    // ===== 500 - Internal Server Error: qualquer erro inesperado =====

    // Último recurso: pega qualquer exceção que não foi tratada acima.
    @ExceptionHandler(Exception.class)
    // Devolve 500.
    public ResponseEntity<ErroResponseDTO> tratarErroInesperado(Exception ex, HttpServletRequest request) {
        // log.error com a exceção no final imprime o stack trace completo (para investigar o bug).
        log.error("Erro inesperado em {} {}", request.getMethod(), request.getRequestURI(), ex);
        // Para o cliente, não mostramos detalhes internos (questão de segurança).
        return montarResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor. Tente novamente mais tarde.", request, null);
    }

    // Método auxiliar que monta o ErroResponseDTO e o ResponseEntity, para não repetir código em cada handler.
    private ResponseEntity<ErroResponseDTO> montarResposta(HttpStatus status, String mensagem,
                                                          HttpServletRequest request, List<ErroResponseDTO.CampoErro> campos) {
        // Cria o corpo da resposta com todos os dados.
        ErroResponseDTO corpo = new ErroResponseDTO(
                // Data/hora atual.
                LocalDateTime.now(),
                // Código numérico (ex.: 404).
                status.value(),
                // Texto do status (ex.: "Not Found").
                status.getReasonPhrase(),
                // Mensagem explicativa.
                mensagem,
                // Caminho da requisição.
                request.getRequestURI(),
                // Erros por campo (ou null).
                campos
        );
        // Devolve a resposta com o status e o corpo.
        return ResponseEntity.status(status).body(corpo);
    }

}
