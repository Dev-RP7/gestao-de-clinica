// Pacote dos mappers.
package com.clinica.gestao_clinica.mapper;

// DTOs e entidades.
import com.clinica.gestao_clinica.dto.request.ReceitaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ReceitaResponseDTO;
import com.clinica.gestao_clinica.entity.Prontuario;
import com.clinica.gestao_clinica.entity.Receita;

// Conversões de Receita.
public class ReceitaMapper {

    // Impede instanciar a classe.
    private ReceitaMapper() {
    }

    // Cria a entidade a partir do DTO e do prontuário já buscado.
    public static Receita toEntity(ReceitaRequestDTO dto, Prontuario prontuario) {
        // Cria a receita vazia.
        Receita receita = new Receita();
        // Medicamento.
        receita.setMedicamento(dto.medicamento());
        // Dosagem.
        receita.setDosagem(dto.dosagem());
        // Frequência.
        receita.setFrequencia(dto.frequencia());
        // Duração.
        receita.setDuracao(dto.duracao());
        // Observação.
        receita.setObservacao(dto.observacao());
        // Liga o prontuário (o service antigo esquecia esta linha, e o banco recusava a receita).
        receita.setProntuario(prontuario);
        // Devolve a entidade.
        return receita;
    }

    // Converte a entidade em DTO de resposta.
    public static ReceitaResponseDTO toResponse(Receita receita) {
        // Monta o record.
        return new ReceitaResponseDTO(
                // Id.
                receita.getId(),
                // Id do prontuário.
                receita.getProntuario().getId(),
                // Medicamento.
                receita.getMedicamento(),
                // Dosagem.
                receita.getDosagem(),
                // Frequência.
                receita.getFrequencia(),
                // Duração.
                receita.getDuracao(),
                // Observação.
                receita.getObservacao()
        );
    }
}
