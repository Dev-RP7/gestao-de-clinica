// Pacote das entidades.
package com.clinica.gestao_clinica.entity;

// Anotações do JPA.
import jakarta.persistence.*;
// Anotações do Lombok.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Esta classe é uma tabela.
@Entity
// Nome da tabela.
@Table(name = "receita")
// Gera os getters.
@Getter
// Gera os setters.
@Setter
// Gera o construtor vazio.
@NoArgsConstructor
// Gera o construtor completo.
@AllArgsConstructor
// Receita de um medicamento. Um prontuário pode ter várias receitas.
public class Receita {

    // Chave primária.
    @Id
    // Gerada pelo banco.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Identificador da receita.
    private Long id;

    // Muitas receitas para um prontuário (antes era @OneToOne, o que impedia mais de um remédio).
    @ManyToOne
    // Chave estrangeira prontuario_id.
    @JoinColumn(name = "prontuario_id", nullable = false)
    // Prontuário ao qual a receita pertence.
    private Prontuario prontuario;

    // Texto obrigatório.
    @Column(columnDefinition = "TEXT", nullable = false)
    // Nome do medicamento.
    private String medicamento;

    // Texto obrigatório.
    @Column(columnDefinition = "TEXT", nullable = false)
    // Dose (ex.: 500mg).
    private String dosagem;

    // Texto obrigatório.
    @Column(columnDefinition = "TEXT", nullable = false)
    // Frequência (ex.: de 8 em 8 horas).
    private String frequencia;

    // Texto obrigatório.
    @Column(columnDefinition = "TEXT", nullable = false)
    // Duração do tratamento (ex.: 7 dias).
    private String duracao;

    // Texto obrigatório.
    @Column(columnDefinition = "TEXT", nullable = false)
    // Observações adicionais.
    private String observacao;

}
