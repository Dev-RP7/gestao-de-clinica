// Pacote dos repositórios (camada que conversa com o banco).
package com.clinica.gestao_clinica.repository;

// Entidade e enum.
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.enums.StatusConsulta;
// Classes de paginação do Spring Data.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// Interface base que já traz save, findById, findAll, delete...
import org.springframework.data.jpa.repository.JpaRepository;
// Permite escrever a consulta em JPQL.
import org.springframework.data.jpa.repository.Query;
// Liga um parâmetro do método a um ":nome" na consulta.
import org.springframework.data.repository.query.Param;

// Data/hora e coleção.
import java.time.LocalDateTime;
import java.util.Collection;

// JpaRepository<Consulta, Long>: repositório da entidade Consulta, cujo id é Long.
// O Spring cria a implementação desta interface sozinho, em tempo de execução.
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // JPQL: parecido com SQL, mas usa nomes de CLASSES e ATRIBUTOS Java, não de tabelas e colunas.
    // Cada filtro tem a forma "(:x is null or ...)": se o filtro não for enviado (null), ele é ignorado.
    @Query("""
            select c from Consulta c
            where (:medicoId is null or c.medico.id = :medicoId)
              and (:pacienteId is null or c.paciente.id = :pacienteId)
              and (:status is null or c.statusConsulta = :status)
              and (:inicio is null or c.dataConsulta >= :inicio)
              and (:fim is null or c.dataConsulta <= :fim)
            """)
    // Devolve uma Page (uma "página" de resultados + total de registros).
    Page<Consulta> filtrar(
            // Filtro por médico.
            @Param("medicoId") Long medicoId,
            // Filtro por paciente.
            @Param("pacienteId") Long pacienteId,
            // Filtro por status.
            @Param("status") StatusConsulta status,
            // Data/hora inicial do período.
            @Param("inicio") LocalDateTime inicio,
            // Data/hora final do período.
            @Param("fim") LocalDateTime fim,
            // Página, tamanho e ordenação pedidos pelo cliente.
            Pageable pageable);

    // Consulta derivada do nome do método: o Spring lê o nome e monta o SQL sozinho.
    // "existsBy...And...And...In" = existe consulta com este médico, nesta data, com status dentro da lista?
    boolean existsByMedicoIdAndDataConsultaAndStatusConsultaIn(
            // Id do médico.
            Long medicoId,
            // Data/hora exata.
            LocalDateTime dataConsulta,
            // Lista de status considerados "ocupando o horário".
            Collection<StatusConsulta> status);

    // Mesma pergunta, mas ignorando uma consulta específica (usado ao REMARCAR, para não conflitar consigo mesma).
    boolean existsByMedicoIdAndDataConsultaAndStatusConsultaInAndIdNot(
            // Id do médico.
            Long medicoId,
            // Data/hora exata.
            LocalDateTime dataConsulta,
            // Status que ocupam o horário.
            Collection<StatusConsulta> status,
            // Id da consulta a ignorar.
            Long id);

    // Conta quantas consultas existem com um determinado status (usado no relatório).
    long countByStatusConsulta(StatusConsulta status);

}
