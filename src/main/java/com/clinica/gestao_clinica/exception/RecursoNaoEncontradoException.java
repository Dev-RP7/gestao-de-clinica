// Pacote das exceções do sistema.
package com.clinica.gestao_clinica.exception;

// Exceção lançada quando algo procurado pelo id não existe. O GlobalExceptionHandler a transforma em HTTP 404.
// "extends RuntimeException" = exceção não verificada: não precisamos de "throws" nos métodos.
public class RecursoNaoEncontradoException extends RuntimeException {

    // Construtor que recebe a mensagem de erro (ex.: "Médico não encontrado").
    public RecursoNaoEncontradoException(String mensagem) {
        // Passa a mensagem para a classe pai (RuntimeException), que guarda em getMessage().
        super(mensagem);
    }

    // Construtor de conveniência: monta a mensagem a partir do nome do recurso e do id.
    public RecursoNaoEncontradoException(String recurso, Long id) {
        // Ex.: "Médico com id 5 não encontrado".
        super(recurso + " com id " + id + " não encontrado(a)");
    }
}
