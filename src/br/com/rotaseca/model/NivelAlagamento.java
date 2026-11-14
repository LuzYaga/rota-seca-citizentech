package br.com.rotaseca.model;

/**
 * Níveis de gravidade informados nos reportes de alagamento pelos cidadãos.
 * Cada nível possui um peso para a ponderação matemática do risco.
 */
public enum NivelAlagamento {
    LEVE(1, "Água rasa / poça acumulada"),
    MEDIO(2, "Água na altura do meio-fio / tráfego difícil"),
    GRAVE(3, "Via totalmente submersa / perigo iminente");

    private final int peso;
    private final String descricao;

    NivelAlagamento(int peso, String descricao) {
        this.peso = peso;
        this.descricao = descricao;
    }

    public int getPeso() {
        return peso;
    }

    public String getDescricao() {
        return descricao;
    }

    public static NivelAlagamento fromString(String valor) {
        if (valor == null) return LEVE;
        try {
            return NivelAlagamento.valueOf(valor.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return LEVE;
        }
    }
}
