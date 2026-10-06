// Pacote dos DTOs de saída.
package com.clinica.gestao_clinica.dto.response;

// Enums.
import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.TipoSanguineo;

// Data.
import java.time.LocalDate;

// Dados de um paciente devolvidos pela API (junta Paciente e Usuario). A senha nunca é devolvida.
public record PacienteResponseDTO(

        // Id do paciente.
        Long id,
        // Nome.
        String nome,
        // E-mail.
        String email,
        // Telefone.
        String telefone,
        // CPF.
        String cpf,
        // RG.
        String rg,
        // Data de nascimento (antes estava escrito "dataNacimento").
        LocalDate dataNascimento,
        // Sexo.
        Sexo sexo,
        // Tipo sanguíneo.
        TipoSanguineo tipoSanguineo,
        // Endereço.
        String endereco,
        // Alergias.
        String alergias,
        // Observação.
        String observacao

) {
}
