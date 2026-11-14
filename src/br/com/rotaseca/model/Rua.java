package br.com.rotaseca.model;

/**
 * Entidade Rua: Logradouro monitorado, vinculado a um Bairro.
 */
public class Rua extends EntidadeBase {
    private String nome;
    private String cep;
    private Bairro bairro;

    public Rua() {
        super();
    }

    public Rua(Long id, String nome, String cep, Bairro bairro) {
        super(id);
        this.nome = nome;
        this.cep = cep;
        this.bairro = bairro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    @Override
    public String toString() {
        return "Rua [id=" + id + ", nome=" + nome + ", cep=" + cep + ", bairro=" + (bairro != null ? bairro.getNome() : "N/D") + "]";
    }
}
