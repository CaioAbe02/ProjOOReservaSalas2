package strategy;

import model.Pessoa;
import model.Professor;
import model.Reserva;

public class PrioridadeParaDocente implements PoliticaDeReserva {
    public PrioridadeParaDocente() {}

    @Override
    public Reserva verificaColisao(Reserva reservaAtual, Reserva reservaNova) {
        Pessoa pessoaAtual = reservaAtual.getPessoa();
        Pessoa pessoaNova = reservaNova.getPessoa();
        boolean atualEhProfessor = pessoaAtual instanceof Professor;
        boolean novaEhProfessor = pessoaNova instanceof Professor;

        if (atualEhProfessor && !novaEhProfessor) {
            return reservaAtual;
        }

        if (!atualEhProfessor && novaEhProfessor) {
            return reservaNova;
        }

        return reservaAtual;
    }
    
}
