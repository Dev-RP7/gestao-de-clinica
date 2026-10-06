// Pacote das exceções do sistema.
package com.clinica.gestao_clinica.exception;

// Exceção para dados duplicados ou conflitantes (ex.: e-mail já cadastrado, horário já ocupado).
// O GlobalExceptionHandler a transforma em HTTP 409 (Conflict).
public class ConflitoException extends RuntimeException {

    // Construtor que recebe a descrição do conflito.
    public ConflitoException(String mensagem) {
        // Guarda a mensagem na classe pai.
        super(mensagem);
    }
}
