package br.com.rotaseca.model;

/**
 * Entidade Bairro: Região geográfica monitorada pela CitizenTech Rota Seca.
 */
public class Bairro extends EntidadeBase {
    private String nome;
    private String zona;
    private NivelRisco nivelRisco;

    public Bairro() {
        super();
        this.nivelRisco = NivelRisco.BAIXO;
    }

    public Bairro(Long id, String nome, String zona, NivelRisco nivelRisco) {
        super(id);
        this.nome = nome;
        this.zona = zona;
        this.nivelRisco = nivelRisco != null ? nivelRisco : NivelRisco.BAIXO;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public NivelRisco getNivelRisco() {
        return nivelRisco;
    }

    public void setNivelRisco(NivelRisco nivelRisco) {
        this.nivelRisco = nivelRisco;
    }

    @Override
    public String toString() {
        return "Bairro [id=" + id + ", nome=" + nome + ", zona=" + zona + ", risco=" + nivelRisco + "]";
    }
}
