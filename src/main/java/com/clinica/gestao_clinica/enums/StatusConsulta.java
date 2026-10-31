package com.clinica.gestao_clinica.enums;

public enum StatusConsulta {

    AGENDADA,
    CONFIRMADA,
    CANCELADA,
    REALIZADA;

    public boolean estaEmAberto() {
        return this == AGENDADA || this == CONFIRMADA;
    }

}
