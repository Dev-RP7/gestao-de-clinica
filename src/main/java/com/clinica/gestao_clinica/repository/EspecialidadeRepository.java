// Pacote dos repositórios.
package com.clinica.gestao_clinica.repository;

// Entidade.
import com.clinica.gestao_clinica.entity.Especialidade;
// Paginação.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// Interface base.
import org.springframework.data.jpa.repository.JpaRepository;

// Repositório da entidade Especialidade.
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {

    // "ContainingIgnoreCase" = o nome CONTÉM o texto, sem diferenciar maiúsculas/minúsculas (LIKE '%texto%').
    Page<Especialidade> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // Verifica se já existe uma especialidade com este nome (ignorando maiúsculas/minúsculas).
    boolean existsByNomeIgnoreCase(String nome);
}
