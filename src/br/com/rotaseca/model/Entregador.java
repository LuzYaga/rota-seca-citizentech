package br.com.rotaseca.model;

/**
 * Especialização de Usuario: Entregador / trabalhador de logística urbana exposto ao trânsito.
 */
public class Entregador extends Usuario {
    private String tipoVeiculo;

    public Entregador() {
        super();
        this.tipo = "ENTREGADOR";
    }

    public Entregador(Long id, String nome, String contato, String tipoVeiculo) {
        super(id, nome, contato, "ENTREGADOR");
        this.tipoVeiculo = tipoVeiculo;
    }

    public String getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(String tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    @Override
    public String getDescricaoAlerta() {
        return "Alerta Viário Logístico: Risco de pane mecânica e perda de carga em vias de fundo de vale. Evite rotas baixas.";
    }

    @Override
    public String getInfoAdicional() {
        return tipoVeiculo != null ? ("Veículo: " + tipoVeiculo) : "Entregador";
    }
}
