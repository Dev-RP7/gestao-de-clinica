// Pacote dos repositórios.
package com.clinica.gestao_clinica.repository;

// Entidade.
import com.clinica.gestao_clinica.entity.Prontuario;
// Paginação.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// Interface base e anotação de consulta.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
// Liga parâmetros.
import org.springframework.data.repository.query.Param;

// Repositório da entidade Prontuario.
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    // Lista prontuários com filtros opcionais por paciente e por médico (navegando pela consulta).
    @Query("""
            select p from Prontuario p
            where (:pacienteId is null or p.consulta.paciente.id = :pacienteId)
              and (:medicoId is null or p.consulta.medico.id = :medicoId)
            """)
    // Devolve uma página de prontuários.
    Page<Prontuario> filtrar(
            // Id do paciente.
            @Param("pacienteId") Long pacienteId,
            // Id do médico.
            @Param("medicoId") Long medicoId,
            // Paginação.
            Pageable pageable);

    // Verifica se a consulta já tem prontuário (regra: um prontuário por consulta).
    boolean existsByConsultaId(Long consultaId);
}
