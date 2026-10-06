// Pacote dos repositórios.
package com.clinica.gestao_clinica.repository;

// Entidade.
import com.clinica.gestao_clinica.entity.Paciente;
// Paginação.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// Interface base e anotação de consulta.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
// Liga parâmetros.
import org.springframework.data.repository.query.Param;

// Repositório da entidade Paciente.
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Lista só pacientes ATIVOS, com filtros opcionais por parte do nome e por CPF exato.
    @Query("""
            select p from Paciente p
            where p.usuario.ativo = true
              and (:nome is null or lower(p.usuario.nome) like lower(concat('%', :nome, '%')))
              and (:cpf is null or p.cpf = :cpf)
            """)
    // Devolve uma página de pacientes.
    Page<Paciente> filtrar(
            // Parte do nome.
            @Param("nome") String nome,
            // CPF.
            @Param("cpf") String cpf,
            // Paginação.
            Pageable pageable);

    // Verifica se o CPF já está cadastrado (antes o método se chamava "existsBycpf", com "c" minúsculo).
    boolean existsByCpf(String cpf);

    // Verifica se o RG já está cadastrado.
    boolean existsByRg(String rg);

}
