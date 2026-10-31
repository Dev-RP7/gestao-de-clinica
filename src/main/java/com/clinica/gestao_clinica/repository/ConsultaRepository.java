package com.clinica.gestao_clinica.repository;

import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.enums.StatusConsulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Query("""
            select c from Consulta c
            where (:medicoId is null or c.medico.id = :medicoId)
              and (:pacienteId is null or c.paciente.id = :pacienteId)
              and (:status is null or c.statusConsulta = :status)
              and (:inicio is null or c.dataConsulta >= :inicio)
              and (:fim is null or c.dataConsulta <= :fim)
            """)
    Page<Consulta> filtrar(
            @Param("medicoId") Long medicoId,
            @Param("pacienteId") Long pacienteId,
            @Param("status") StatusConsulta status,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            Pageable pageable);

    boolean existsByMedicoIdAndDataConsultaAndStatusConsultaIn(
            Long medicoId,
            LocalDateTime dataConsulta,
            Collection<StatusConsulta> status);

    boolean existsByMedicoIdAndDataConsultaAndStatusConsultaInAndIdNot(
            Long medicoId,
            LocalDateTime dataConsulta,
            Collection<StatusConsulta> status,
            Long id);

    long countByStatusConsulta(StatusConsulta status);

}
