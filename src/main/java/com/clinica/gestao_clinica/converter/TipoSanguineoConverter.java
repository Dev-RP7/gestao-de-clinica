// Pacote dos conversores do JPA.
package com.clinica.gestao_clinica.converter;

// Importa o enum que vamos converter.
import com.clinica.gestao_clinica.enums.TipoSanguineo;
// Interface do JPA para converter um tipo Java em uma coluna do banco (e vice-versa).
import jakarta.persistence.AttributeConverter;
// Anotação que registra a classe como conversor.
import jakarta.persistence.Converter;

// autoApply = false: só é usado onde colocarmos @Convert (na entidade Paciente).
@Converter(autoApply = false)
// AttributeConverter<TipoSanguineo, String>: no Java é TipoSanguineo, no banco é String.
public class TipoSanguineoConverter implements AttributeConverter<TipoSanguineo, String> {

    // @Override indica que estamos implementando um método da interface.
    @Override
    // Chamado ao SALVAR: transforma o enum no texto que vai para a coluna.
    public String convertToDatabaseColumn(TipoSanguineo tipo) {

        // Se o paciente não informou tipo sanguíneo...
        if (tipo == null) {
            // ...a coluna fica vazia (NULL).
            return null;
        }

        // Caso contrário, grava o texto (ex.: "A+").
        return tipo.getValorBanco();
    }

    // Implementa o método da interface.
    @Override
    // Chamado ao LER do banco: transforma o texto da coluna de volta no enum.
    public TipoSanguineo convertToEntityAttribute(String valor) {

        // Se a coluna está vazia...
        if (valor == null) {
            // ...o atributo também fica null.
            return null;
        }

        // Converte "A+" em TipoSanguineo.A_POSITIVO.
        return TipoSanguineo.fromValorBanco(valor);
    }

}
