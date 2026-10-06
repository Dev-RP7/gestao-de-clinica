// Pacote das entidades.
package com.clinica.gestao_clinica.entity;

// Enum com os estados da consulta.
import com.clinica.gestao_clinica.enums.StatusConsulta;
// Anotações do JPA.
import jakarta.persistence.*;
// Anotações do Lombok.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Data e hora.
import java.time.LocalDateTime;

// Esta classe é uma tabela.
@Entity
// Nome da tabela.
@Table(name = "consulta")
// Gera os getters.
@Getter
// Gera os setters.
@Setter
// Gera o construtor vazio.
@NoArgsConstructor
// Gera o construtor completo.
@AllArgsConstructor
// Uma consulta liga um médico a um paciente em uma data/hora.
public class Consulta {

    // Chave primária.
    @Id
    // Gerada pelo banco.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Identificador da consulta.
    private Long id;

    // Muitas consultas para um médico.
    @ManyToOne
    // Chave estrangeira medico_id.
    @JoinColumn(name = "medico_id", nullable = false)
    // Médico que vai atender.
    private Medico medico;

    // Muitas consultas para um paciente.
    @ManyToOne
    // Chave estrangeira paciente_id.
    @JoinColumn(name = "paciente_id", nullable = false)
    // Paciente atendido.
    private Paciente paciente;

    // Coluna obrigatória.
    @Column(name = "data_consulta", nullable = false)
    // LocalDateTime guarda data e hora.
    private LocalDateTime dataConsulta;

    // Salva o enum como texto ("AGENDADA").
    @Enumerated(EnumType.STRING)
    // Coluna obrigatória.
    @Column(name = "status_consulta", nullable = false)
    // Situação atual da consulta.
    private StatusConsulta statusConsulta;

    // Texto longo e obrigatório.
    @Column(columnDefinition = "TEXT", nullable = false)
    // Motivo da consulta informado no agendamento.
    private String motivo;

}
