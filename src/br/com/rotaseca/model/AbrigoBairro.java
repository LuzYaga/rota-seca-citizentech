package br.com.rotaseca.model;

/**
 * Entidade Associativa N:N AbrigoBairro:
 * Conecta abrigos aos bairros com atributo de distância operacional em KM.
 */
public class AbrigoBairro {
    private Abrigo abrigo;
    private Bairro bairro;
    private double distanciaKm;

    public AbrigoBairro() {}

    public AbrigoBairro(Abrigo abrigo, Bairro bairro, double distanciaKm) {
        this.abrigo = abrigo;
        this.bairro = bairro;
        this.distanciaKm = distanciaKm;
    }

    public Abrigo getAbrigo() {
        return abrigo;
    }

    public void setAbrigo(Abrigo abrigo) {
        this.abrigo = abrigo;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    @Override
    public String toString() {
        return "AbrigoBairro [abrigo=" + (abrigo != null ? abrigo.getNome() : "N/D") + 
               ", bairro=" + (bairro != null ? bairro.getNome() : "N/D") + 
               ", distância=" + distanciaKm + " km]";
    }
}
