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
@Table(name = "prontuario")
// Gera os getters.
@Getter
// Gera os setters.
@Setter
// Gera o construtor vazio.
@NoArgsConstructor
// Gera o construtor completo.
@AllArgsConstructor
// O prontuário registra o que aconteceu em UMA consulta (medidas, diagnóstico, tratamento).
public class Prontuario {

    // Chave primária.
    @Id
    // Gerada pelo banco.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Identificador do prontuário.
    private Long id;

    // Cada consulta tem no máximo um prontuário.
    @OneToOne
    // Chave estrangeira consulta_id. unique garante "um prontuário por consulta".
    @JoinColumn(name = "consulta_id", nullable = false, unique = true)
    // Consulta à qual o prontuário pertence.
    private Consulta consulta;

    // Obrigatório.
    @Column(nullable = false)
    // Peso em kg.
    private double peso;

    // Obrigatório.
    @Column(nullable = false)
    // Altura em metros.
    private double altura;

    // Obrigatório.
    @Column(nullable = false)
    // Pressão arterial (valor numérico).
    private double pressao;

    // Obrigatório.
    @Column(nullable = false)
    // Temperatura em °C.
    private double temperatura;

    // Texto longo e obrigatório.
    @Column(columnDefinition = "TEXT", nullable = false)
    // Diagnóstico médico.
    private String diagnostico;

    // Texto longo e obrigatório.
    @Column(columnDefinition = "TEXT", nullable = false)
    // Tratamento indicado.
    private String tratamento;

    // Texto longo e obrigatório.
    @Column(columnDefinition = "TEXT", nullable = false)
    // Observações do médico.
    private String observacao;

}
