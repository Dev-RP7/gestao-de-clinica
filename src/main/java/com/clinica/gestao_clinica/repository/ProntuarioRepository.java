package com.clinica.gestao_clinica.repository;

import com.clinica.gestao_clinica.entity.Prontuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    @Query("""
            select p from Prontuario p
            where (:pacienteId is null or p.consulta.paciente.id = :pacienteId)
              and (:medicoId is null or p.consulta.medico.id = :medicoId)
            """)
    Page<Prontuario> filtrar(
            @Param("pacienteId") Long pacienteId,
            @Param("medicoId") Long medicoId,
            Pageable pageable);

    boolean existsByConsultaId(Long consultaId);
}
