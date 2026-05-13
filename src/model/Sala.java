package model;

public abstract class Sala {

    protected final String numero;
    protected int capacidade;

    public Sala(String numero, int capacidade) {
        this.numero = numero;
        this.capacidade = capacidade;
    }

    public String getNumero() {
        return numero;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }
}