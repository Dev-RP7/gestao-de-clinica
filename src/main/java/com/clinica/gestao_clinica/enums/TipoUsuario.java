// Pacote dos enums.
package com.clinica.gestao_clinica.enums;

// Perfis de acesso do sistema. Cada perfil vira uma "role" no Spring Security (ex.: ROLE_MEDICO).
public enum TipoUsuario {

    // Médico: cuida de prontuários e receitas.
    MEDICO,
    // Paciente: marca e acompanha consultas.
    PACIENTE,
    // Administrador: gerencia usuários, médicos, especialidades e relatórios.
    ADMINISTRADOR
}
