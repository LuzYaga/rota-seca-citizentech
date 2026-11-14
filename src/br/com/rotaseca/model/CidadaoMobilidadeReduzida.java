package br.com.rotaseca.model;

/**
 * Especialização de Usuario: Cidadão com mobilidade reduzida.
 * Foco humanitário prioritário da CitizenTech Rota Seca.
 */
public class CidadaoMobilidadeReduzida extends Usuario {
    private String necessidadeAcessibilidade;

    public CidadaoMobilidadeReduzida() {
        super();
        this.tipo = "MOBILIDADE_REDUZIDA";
    }

    public CidadaoMobilidadeReduzida(Long id, String nome, String contato, String necessidadeAcessibilidade) {
        super(id, nome, contato, "MOBILIDADE_REDUZIDA");
        this.necessidadeAcessibilidade = necessidadeAcessibilidade;
    }

    public String getNecessidadeAcessibilidade() {
        return necessidadeAcessibilidade;
    }

    public void setNecessidadeAcessibilidade(String necessidadeAcessibilidade) {
        this.necessidadeAcessibilidade = necessidadeAcessibilidade;
    }

    @Override
    public String getDescricaoAlerta() {
        return "ALERTA PRIORITÁRIO DE ACESSIBILIDADE: Risco severo de bloqueio em calçadas/rampas. Busque abrigo imediato com suporte assistido.";
    }

    @Override
    public String getInfoAdicional() {
        return necessidadeAcessibilidade != null ? necessidadeAcessibilidade : "Mobilidade reduzida";
    }
}
