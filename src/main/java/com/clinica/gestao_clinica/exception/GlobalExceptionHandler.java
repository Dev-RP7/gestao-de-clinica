package com.clinica.gestao_clinica.exception;

import com.clinica.gestao_clinica.dto.response.ErroResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDTO> tratarValidacao(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErroResponseDTO.CampoErro> campos = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> new ErroResponseDTO.CampoErro(erro.getField(), erro.getDefaultMessage()))
                .toList();

        log.warn("Validação falhou em {}: {}", request.getRequestURI(), campos);
        return montarResposta(HttpStatus.BAD_REQUEST, "Dados inválidos. Verifique os campos.", request, campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDTO> tratarJsonInvalido(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("JSON inválido em {}: {}", request.getRequestURI(), ex.getMessage());
        return montarResposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou malformado.", request, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponseDTO> tratarTipoInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String mensagem = "Valor '" + ex.getValue() + "' inválido para o parâmetro '" + ex.getName() + "'.";
        log.warn("{} em {}", mensagem, request.getRequestURI());
        return montarResposta(HttpStatus.BAD_REQUEST, mensagem, request, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponseDTO> tratarParametroAusente(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String mensagem = "Parâmetro obrigatório '" + ex.getParameterName() + "' não informado.";
        return montarResposta(HttpStatus.BAD_REQUEST, mensagem, request, null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResponseDTO> tratarCredenciaisInvalidas(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Tentativa de login com credenciais inválidas em {}", request.getRequestURI());
        return montarResposta(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.", request, null);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErroResponseDTO> tratarUsuarioDesativado(DisabledException ex, HttpServletRequest request) {
        return montarResposta(HttpStatus.UNAUTHORIZED, "Usuário desativado.", request, null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroResponseDTO> tratarNaoAutenticado(AuthenticationException ex, HttpServletRequest request) {
        return montarResposta(HttpStatus.UNAUTHORIZED, "Autenticação necessária. Envie um token JWT válido.", request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponseDTO> tratarAcessoNegado(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Acesso negado em {}", request.getRequestURI());
        return montarResposta(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este recurso.", request, null);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> tratarNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest request) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        return montarResposta(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponseDTO> tratarRotaInexistente(NoResourceFoundException ex, HttpServletRequest request) {
        return montarResposta(HttpStatus.NOT_FOUND, "Rota não encontrada.", request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponseDTO> tratarMetodoNaoSuportado(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String mensagem = "Método " + ex.getMethod() + " não é suportado nesta rota.";
        return montarResposta(HttpStatus.METHOD_NOT_ALLOWED, mensagem, request, null);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResponseDTO> tratarConflito(ConflitoException ex, HttpServletRequest request) {
        log.warn("Conflito: {}", ex.getMessage());
        return montarResposta(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponseDTO> tratarIntegridade(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Violação de integridade em {}: {}", request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return montarResposta(HttpStatus.CONFLICT, "Operação viola uma restrição do banco de dados (registro duplicado ou em uso).", request, null);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponseDTO> tratarRegraNegocio(RegraNegocioException ex, HttpServletRequest request) {
        log.warn("Regra de negócio violada: {}", ex.getMessage());
        return montarResposta(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDTO> tratarErroInesperado(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado em {} {}", request.getMethod(), request.getRequestURI(), ex);
        return montarResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor. Tente novamente mais tarde.", request, null);
    }

    private ResponseEntity<ErroResponseDTO> montarResposta(HttpStatus status, String mensagem,
                                                          HttpServletRequest request, List<ErroResponseDTO.CampoErro> campos) {
        ErroResponseDTO corpo = new ErroResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                request.getRequestURI(),
                campos
        );
        return ResponseEntity.status(status).body(corpo);
    }

}
