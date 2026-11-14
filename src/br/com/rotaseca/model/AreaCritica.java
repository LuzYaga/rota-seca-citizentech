package br.com.rotaseca.model;

import java.time.LocalDateTime;

/**
 * Entidade AreaCritica: Registro de locais com recorrência crítica de alagamento.
 */
public class AreaCritica extends EntidadeBase {
    private Bairro bairro;
    private Rua rua;
    private int totalReportes;
    private LocalDateTime dataMarcacao;
    private String nivelAlerta;

    public AreaCritica() {
        super();
        this.dataMarcacao = LocalDateTime.now();
    }

    public AreaCritica(Long id, Bairro bairro, Rua rua, int totalReportes, LocalDateTime dataMarcacao, String nivelAlerta) {
        super(id);
        this.bairro = bairro;
        this.rua = rua;
        this.totalReportes = totalReportes;
        this.dataMarcacao = dataMarcacao != null ? dataMarcacao : LocalDateTime.now();
        this.nivelAlerta = nivelAlerta;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Rua getRua() {
        return rua;
    }

    public void setRua(Rua rua) {
        this.rua = rua;
    }

    public int getTotalReportes() {
        return totalReportes;
    }

    public void setTotalReportes(int totalReportes) {
        this.totalReportes = totalReportes;
    }

    public LocalDateTime getDataMarcacao() {
        return dataMarcacao;
    }

    public void setDataMarcacao(LocalDateTime dataMarcacao) {
        this.dataMarcacao = dataMarcacao;
    }

    public String getNivelAlerta() {
        return nivelAlerta;
    }

    public void setNivelAlerta(String nivelAlerta) {
        this.nivelAlerta = nivelAlerta;
    }

    @Override
    public String toString() {
        return "AreaCritica [id=" + id + ", rua=" + (rua != null ? rua.getNome() : "N/D") + 
               ", totalReportes=" + totalReportes + ", alerta=" + nivelAlerta + "]";
    }
}
