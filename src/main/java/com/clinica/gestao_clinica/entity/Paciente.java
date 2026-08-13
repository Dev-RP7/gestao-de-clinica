package com.clinica.gestao_clinica.entity;


import com.clinica.gestao_clinica.converter.TipoSanguineoConverter;
import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.TipoSanguineo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, unique = true, length = 20)
    private String rg;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexo sexo;

    @Column(nullable = false)
    private String endereco;

    @Column(name = "tipo_sanguineo")
    @Convert(converter = TipoSanguineoConverter.class)
    private TipoSanguineo tipoSanguineo;

    @Column(columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @OneToMany(mappedBy = "paciente")
    @ToString.Exclude
    private List<Consulta> consultas = new ArrayList<>();
}
