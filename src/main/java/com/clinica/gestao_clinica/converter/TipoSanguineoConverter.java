package com.clinica.gestao_clinica.converter;

import com.clinica.gestao_clinica.enums.TipoSanguineo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TipoSanguineoConverter implements AttributeConverter<TipoSanguineo, String> {

    @Override
    public String convertToDatabaseColumn(TipoSanguineo tipo) {

        if (tipo == null) {
            return null;
        }

        return tipo.getValorBanco();
    }

    @Override
    public TipoSanguineo convertToEntityAttribute(String valor) {

        if (valor == null) {
            return null;
        }

        return TipoSanguineo.fromValorBanco(valor);
    }



}
