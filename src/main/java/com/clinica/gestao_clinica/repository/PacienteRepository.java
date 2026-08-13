package com.clinica.gestao_clinica.repository;

import com.clinica.gestao_clinica.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente,Long> {

    boolean existsBycpf(String cpf);

}
