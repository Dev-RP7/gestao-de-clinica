// Pacote das exceções do sistema.
package com.clinica.gestao_clinica.exception;

// Exceção para quando a requisição é válida no formato, mas viola uma regra do negócio
// (ex.: cancelar uma consulta já realizada). O GlobalExceptionHandler a transforma em HTTP 422.
public class RegraNegocioException extends RuntimeException {

    // Construtor que recebe a explicação da regra violada.
    public RegraNegocioException(String mensagem) {
        // Guarda a mensagem na classe pai.
        super(mensagem);
    }
}
