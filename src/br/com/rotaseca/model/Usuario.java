package br.com.rotaseca.model;

/**
 * Entidade base Usuario (abstrata): Demonstra POO com herança e polimorfismo.
 */
public abstract class Usuario extends EntidadeBase {
    protected String nome;
    protected String contato;
    protected String tipo;

    public Usuario() {
        super();
    }

    public Usuario(Long id, String nome, String contato, String tipo) {
        super(id);
        this.nome = nome;
        this.contato = contato;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Método polimórfico que define como o alerta deve ser customizado
     * para a categoria de cidadão atendida.
     */
    public abstract String getDescricaoAlerta();
    
    /**
     * Informação complementar específica de cada subclasse para persistência.
     */
    public abstract String getInfoAdicional();

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", nome=" + nome + ", tipo=" + tipo + ", contato=" + contato + "]";
    }
}
