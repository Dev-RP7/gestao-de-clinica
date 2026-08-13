package com.clinica.gestao_clinica.repository;

import com.clinica.gestao_clinica.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicoRepository extends JpaRepository<Medico,Long> {

    List<Medico> findByUsuarioAtivoTrue();

}
