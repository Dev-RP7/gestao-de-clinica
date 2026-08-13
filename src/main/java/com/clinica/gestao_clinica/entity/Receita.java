package com.clinica.gestao_clinica.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "receita")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Receita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "prontuario_id", nullable = false)
    private Prontuario prontuario;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String medicamento;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String dosagem;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String frequencia;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String duracao;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String observacao;

}
