// Pacote dos enums.
package com.clinica.gestao_clinica.enums;

// Tipos sanguíneos. Cada valor guarda também o texto que vai para o banco (ex.: "A+").
public enum TipoSanguineo {

    // Cada constante chama o construtor abaixo passando o texto que será salvo no banco.
    A_POSITIVO("A+"),
    // Tipo A negativo.
    A_NEGATIVO("A-"),
    // Tipo B positivo.
    B_POSITIVO("B+"),
    // Tipo B negativo.
    B_NEGATIVO("B-"),
    // Tipo AB positivo.
    AB_POSITIVO("AB+"),
    // Tipo AB negativo.
    AB_NEGATIVO("AB-"),
    // Tipo O positivo.
    O_POSITIVO("O+"),
    // Tipo O negativo (o ";" encerra a lista de constantes).
    O_NEGATIVO("O-");

    // Campo que guarda o texto do banco. "final" = não muda depois de criado.
    private final String valorBanco;

    // Construtor do enum: recebe o texto e guarda no campo acima.
    TipoSanguineo(String valorBanco) {
        // "this.valorBanco" é o campo; "valorBanco" é o parâmetro.
        this.valorBanco = valorBanco;
    }

    // Getter: devolve o texto que vai para o banco (ex.: "O-").
    public String getValorBanco() {
        // Retorna o valor guardado.
        return valorBanco;
    }

    // Método estático: transforma o texto do banco ("A+") de volta no enum (A_POSITIVO).
    public static TipoSanguineo fromValorBanco(String valor) {
        // Percorre todas as constantes do enum.
        for (TipoSanguineo tipo : values()) {
            // Compara o texto da constante com o texto recebido.
            if (tipo.getValorBanco().equals(valor)) {
                // Achou: devolve a constante.
                return tipo;
            }
        }
        // Se nenhuma bateu, o valor do banco é inválido: lançamos um erro.
        throw new IllegalArgumentException("Tipo sanguíneo inválido: " + valor);
    }

}
