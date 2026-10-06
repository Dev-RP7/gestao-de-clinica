// Pacote dos mappers.
package com.clinica.gestao_clinica.mapper;

// DTOs e entidades.
import com.clinica.gestao_clinica.dto.request.PacienteRequestDTO;
import com.clinica.gestao_clinica.dto.response.PacienteResponseDTO;
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.entity.Usuario;

// Conversões de Paciente.
public class PacienteMapper {

    // Impede instanciar a classe.
    private PacienteMapper() {
    }

    // Cria a entidade Paciente a partir do DTO de cadastro, já ligada ao usuário.
    public static Paciente toEntity(PacienteRequestDTO dto, Usuario usuario) {
        // Cria o paciente vazio.
        Paciente paciente = new Paciente();
        // Liga o usuário.
        paciente.setUsuario(usuario);
        // CPF.
        paciente.setCpf(dto.cpf());
        // RG.
        paciente.setRg(dto.rg());
        // Data de nascimento.
        paciente.setDataNascimento(dto.dataNascimento());
        // Sexo.
        paciente.setSexo(dto.sexo());
        // Endereço.
        paciente.setEndereco(dto.endereco());
        // Tipo sanguíneo.
        paciente.setTipoSanguineo(dto.tipoSanguineo());
        // Alergias.
        paciente.setAlergias(dto.alergias());
        // Observação.
        paciente.setObservacao(dto.observacao());
        // Devolve a entidade.
        return paciente;
    }

    // Converte a entidade em DTO de resposta.
    public static PacienteResponseDTO toResponse(Paciente paciente) {
        // Guarda o usuário numa variável.
        Usuario usuario = paciente.getUsuario();
        // Monta o record.
        return new PacienteResponseDTO(
                // Id do paciente.
                paciente.getId(),
                // Nome.
                usuario.getNome(),
                // E-mail.
                usuario.getEmail(),
                // Telefone.
                usuario.getTelefone(),
                // CPF.
                paciente.getCpf(),
                // RG.
                paciente.getRg(),
                // Data de nascimento.
                paciente.getDataNascimento(),
                // Sexo.
                paciente.getSexo(),
                // Tipo sanguíneo.
                paciente.getTipoSanguineo(),
                // Endereço.
                paciente.getEndereco(),
                // Alergias.
                paciente.getAlergias(),
                // Observação.
                paciente.getObservacao()
        );
    }
}
