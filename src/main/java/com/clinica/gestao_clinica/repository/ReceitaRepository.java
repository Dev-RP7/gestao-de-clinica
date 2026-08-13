package com.clinica.gestao_clinica.repository;

import com.clinica.gestao_clinica.entity.Receita;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceitaRepository extends JpaRepository<Receita,Long> {
}
