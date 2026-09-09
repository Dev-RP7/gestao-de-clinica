package com.clinica.gestao_clinica.repository;

import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.enums.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByPacienteId(Long pacienteId);

    List<Consulta> findByMedico(String nome);

    List<Consulta> findByStatusConsulta(StatusConsulta status);

    List<Consulta> findByDataConsulta(LocalDateTime inicio, LocalDateTime fim);

    long countByStatusConsulta(StatusConsulta status);

    long countByDataConsultaBetween(LocalDateTime inicio, LocalDateTime fim);

}
