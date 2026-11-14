package br.com.rotaseca.model;

/**
 * Entidade Abrigo: Ponto seguro cadastrado na rede de resiliência climática.
 */
public class Abrigo extends EntidadeBase {
    private String nome;
    private String endereco;
    private int capacidade;

    public Abrigo() {
        super();
    }

    public Abrigo(Long id, String nome, String endereco, int capacidade) {
        super(id);
        this.nome = nome;
        this.endereco = endereco;
        this.capacidade = capacidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    @Override
    public String toString() {
        return "Abrigo [id=" + id + ", nome=" + nome + ", capacidade=" + capacidade + "]";
    }
}
