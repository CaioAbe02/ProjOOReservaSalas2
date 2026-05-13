package strategy;

import java.time.LocalDateTime;

import model.Reserva;

public class PrimeiroAReservar implements PoliticaDeReserva {
    public PrimeiroAReservar() {}
    
    @Override
    public Reserva verificaColisao(Reserva reservaAtual, Reserva reservaNova) {
        LocalDateTime diaDaReservaNova = reservaNova.getDiaQueOcorreuAReserva();
        if (reservaAtual.getDiaQueOcorreuAReserva().isBefore(diaDaReservaNova)) {
            return reservaAtual;
        }
        return reservaNova;
    }
}
