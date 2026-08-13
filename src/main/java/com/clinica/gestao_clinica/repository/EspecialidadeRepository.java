package com.clinica.gestao_clinica.repository;

import com.clinica.gestao_clinica.entity.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EspecialidadeRepository extends JpaRepository<Especialidade,Long> {

    List<Especialidade> findByNome(String nome);
}
