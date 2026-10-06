// Pacote dos enums.
package com.clinica.gestao_clinica.enums;

// Os possíveis estados de uma consulta. O ciclo normal é: AGENDADA -> CONFIRMADA -> REALIZADA.
public enum StatusConsulta {

    // A consulta foi marcada, mas ainda não foi confirmada.
    AGENDADA,
    // A consulta foi confirmada (pela clínica ou pelo paciente).
    CONFIRMADA,
    // A consulta foi cancelada e não vai acontecer.
    CANCELADA,
    // A consulta já aconteceu.
    REALIZADA;

    // Método que diz se a consulta ainda pode mudar de estado (não terminou nem foi cancelada).
    public boolean estaEmAberto() {
        // Retorna true só para AGENDADA ou CONFIRMADA.
        return this == AGENDADA || this == CONFIRMADA;
    }

}
