package br.com.rotaseca.model;

/**
 * Níveis de risco consolidado atribuídos a um bairro.
 */
public enum NivelRisco {
    BAIXO("Baixo Risco"),
    MEDIO("Risco Moderado"),
    ALTO("Risco Alto / Crítico");

    private final String descricao;

    NivelRisco(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static NivelRisco fromString(String valor) {
        if (valor == null) return BAIXO;
        try {
            return NivelRisco.valueOf(valor.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return BAIXO;
        }
    }
}
