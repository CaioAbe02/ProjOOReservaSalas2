package strategy;

import model.Reserva;

public interface PoliticaDeReserva {
    Reserva verificaColisao(Reserva reservaAtual, Reserva reservaNova);
}
