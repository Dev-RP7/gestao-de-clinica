package com.clinica.gestao_clinica.repository;

import com.clinica.gestao_clinica.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    @Query("""
            select p from Paciente p
            where p.usuario.ativo = true
              and (:nome is null or lower(p.usuario.nome) like lower(concat('%', :nome, '%')))
              and (:cpf is null or p.cpf = :cpf)
            """)
    Page<Paciente> filtrar(
            @Param("nome") String nome,
            @Param("cpf") String cpf,
            Pageable pageable);

    boolean existsByCpf(String cpf);

    boolean existsByRg(String rg);

}
