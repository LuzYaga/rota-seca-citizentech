package br.com.rotaseca.model;

import java.time.LocalDateTime;

/**
 * Entidade ReporteAlagamento: Evento emitido colaborativamente por cidadãos.
 */
public class ReporteAlagamento extends EntidadeBase {
    private Rua rua;
    private Usuario usuario;
    private NivelAlagamento nivel;
    private LocalDateTime dataHora;
    private String descricao;

    public ReporteAlagamento() {
        super();
        this.dataHora = LocalDateTime.now();
        this.nivel = NivelAlagamento.LEVE;
    }

    public ReporteAlagamento(Long id, Rua rua, Usuario usuario, NivelAlagamento nivel, LocalDateTime dataHora, String descricao) {
        super(id);
        this.rua = rua;
        this.usuario = usuario;
        this.nivel = nivel != null ? nivel : NivelAlagamento.LEVE;
        this.dataHora = dataHora != null ? dataHora : LocalDateTime.now();
        this.descricao = descricao;
    }

    public Rua getRua() {
        return rua;
    }

    public void setRua(Rua rua) {
        this.rua = rua;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public NivelAlagamento getNivel() {
        return nivel;
    }

    public void setNivel(NivelAlagamento nivel) {
        this.nivel = nivel;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getPesoGravidade() {
        return nivel != null ? nivel.getPeso() : 1;
    }

    @Override
    public String toString() {
        return "Reporte [id=" + id + ", rua=" + (rua != null ? rua.getNome() : "N/D") + 
               ", nível=" + nivel + ", cidadão=" + (usuario != null ? usuario.getNome() : "N/D") + "]";
    }
}
