package singleton;

import strategy.PoliticaDeReserva;
import strategy.PrimeiroAReservar;

public class Configuracao {

    private static volatile Configuracao instancia;

    private PoliticaDeReserva politicaDeReserva;

    private Configuracao() {
        politicaDeReserva = new PrimeiroAReservar();
    }

    public static Configuracao getInstance() {
        if (instancia == null) {
            synchronized (Configuracao.class) {
                if (instancia == null) {
                    instancia = new Configuracao();
                }
            }
        }
        return instancia;
    }

    public PoliticaDeReserva getPoliticaDeReserva() {
        return politicaDeReserva;
    }

    public void setPoliticaDeReserva(PoliticaDeReserva politicaDeReserva) {
        this.politicaDeReserva = politicaDeReserva;
    }
}
