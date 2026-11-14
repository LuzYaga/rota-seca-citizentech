package br.com.rotaseca.model;

/**
 * Classe abstrata base para entidades do domínio Rota Seca.
 * Garante padronização do identificador primário.
 */
public abstract class EntidadeBase {
    protected Long id;

    public EntidadeBase() {}

    public EntidadeBase(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
