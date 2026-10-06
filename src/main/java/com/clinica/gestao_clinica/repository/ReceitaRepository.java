// Pacote dos repositórios.
package com.clinica.gestao_clinica.repository;

// Entidade.
import com.clinica.gestao_clinica.entity.Receita;
// Paginação.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// Interface base.
import org.springframework.data.jpa.repository.JpaRepository;

// Repositório da entidade Receita.
public interface ReceitaRepository extends JpaRepository<Receita, Long> {

    // Lista as receitas de um prontuário, paginadas ("prontuario.id" vira "ProntuarioId" no nome do método).
    Page<Receita> findByProntuarioId(Long prontuarioId, Pageable pageable);

    // Verifica se o prontuário tem receitas (impede excluir um prontuário com receitas).
    boolean existsByProntuarioId(Long prontuarioId);
}
