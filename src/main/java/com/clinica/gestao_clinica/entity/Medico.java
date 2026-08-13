package com.clinica.gestao_clinica.entity;

import com.clinica.gestao_clinica.entity.Especialidade;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, unique = true)
    private String crm;

    @ManyToOne
    @JoinColumn(name = "especialidade_id", nullable = false)
    private Especialidade  especialidade;

    @OneToMany(mappedBy = "medico")
    @ToString.Exclude
    private List<Consulta> consultas = new ArrayList<>();

}
