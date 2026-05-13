package observer;

import java.util.ArrayList;
import java.util.List;

import model.Reserva;

public abstract class Assunto {
    List<Observador> observadores = new ArrayList<>();

    public void adicionarObservador(Observador obs) {
        observadores.add(obs);
    }

    public void removeObservador(Observador obs) {
        observadores.remove(obs);
    }

    public void notificarTodos(Reserva reserva) {
        for(Observador obs: observadores) {
            obs.atualizar(reserva);
        }
    }


}
