package br.com.rotaseca.model;

/**
 * Especialização de Usuario: Cidadão morador local.
 */
public class CidadaoMorador extends Usuario {
    private String observacaoResidencia;

    public CidadaoMorador() {
        super();
        this.tipo = "MORADOR";
    }

    public CidadaoMorador(Long id, String nome, String contato, String observacaoResidencia) {
        super(id, nome, contato, "MORADOR");
        this.observacaoResidencia = observacaoResidencia;
    }

    public String getObservacaoResidencia() {
        return observacaoResidencia;
    }

    public void setObservacaoResidencia(String observacaoResidencia) {
        this.observacaoResidencia = observacaoResidencia;
    }

    @Override
    public String getDescricaoAlerta() {
        return "Alerta para Residência: Monitore o nível de bueiros da sua rua e verifique rotas de saída seguras.";
    }

    @Override
    public String getInfoAdicional() {
        return observacaoResidencia != null ? observacaoResidencia : "Morador local";
    }
}
