package model;

import observer.Observador;

public abstract class Pessoa implements Observador {
    private String nome;

    public Pessoa(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public void atualizar(Reserva reserva) {
        System.out.println("A reserva da sala " + reserva.getSala() + " no dia " + reserva.getDia() + "foi cancelada.");
    }
}
