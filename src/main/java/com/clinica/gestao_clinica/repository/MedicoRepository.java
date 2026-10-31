package com.clinica.gestao_clinica.repository;

import com.clinica.gestao_clinica.entity.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    @Query("""
            select m from Medico m
            where m.usuario.ativo = true
              and (:nome is null or lower(m.usuario.nome) like lower(concat('%', :nome, '%')))
              and (:especialidadeId is null or m.especialidade.id = :especialidadeId)
            """)
    Page<Medico> filtrar(
            @Param("nome") String nome,
            @Param("especialidadeId") Long especialidadeId,
            Pageable pageable);

    boolean existsByCrm(String crm);

    boolean existsByCrmAndIdNot(String crm, Long id);

    boolean existsByEspecialidadeId(Long especialidadeId);
}
