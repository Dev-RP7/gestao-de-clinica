package com.clinica.gestao_clinica.repository;

import com.clinica.gestao_clinica.entity.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProntuarioRepository extends JpaRepository<Prontuario,Long> {
}
