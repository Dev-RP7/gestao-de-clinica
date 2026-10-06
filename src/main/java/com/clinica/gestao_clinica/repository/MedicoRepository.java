// Pacote dos repositórios.
package com.clinica.gestao_clinica.repository;

// Entidade.
import com.clinica.gestao_clinica.entity.Medico;
// Paginação.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// Interface base e anotação de consulta.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
// Liga parâmetros.
import org.springframework.data.repository.query.Param;

// Repositório da entidade Medico.
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    // Lista só médicos ATIVOS, com filtros opcionais por parte do nome e por especialidade.
    // lower(...) like lower(concat('%', :nome, '%')) = "o nome contém o texto", sem diferenciar maiúsculas.
    @Query("""
            select m from Medico m
            where m.usuario.ativo = true
              and (:nome is null or lower(m.usuario.nome) like lower(concat('%', :nome, '%')))
              and (:especialidadeId is null or m.especialidade.id = :especialidadeId)
            """)
    // Devolve uma página de médicos.
    Page<Medico> filtrar(
            // Parte do nome.
            @Param("nome") String nome,
            // Id da especialidade.
            @Param("especialidadeId") Long especialidadeId,
            // Paginação.
            Pageable pageable);

    // Verifica se o CRM já está em uso.
    boolean existsByCrm(String crm);

    // Verifica se o CRM está em uso por OUTRO médico (usado na atualização).
    boolean existsByCrmAndIdNot(String crm, Long id);

    // Verifica se algum médico usa a especialidade (impede excluir uma especialidade em uso).
    boolean existsByEspecialidadeId(Long especialidadeId);
}
