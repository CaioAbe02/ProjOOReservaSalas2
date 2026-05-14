package report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.Reserva;
import observer.Observador;

public abstract class ServicoRelatorio implements Observador {
    protected static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");

    // push: dados chegam prontos via atualizar()
    @Override
    public void atualizar(Reserva reserva) {
        System.out.printf("[RELATÓRIO] Reserva na sala %s (%s) foi alterada para %s.%n",
            reserva.getSala().getNumero(),
            FMT.format(reserva.getDia()),
            reserva.getStatus());
    }

    // Template Method: define o esqueleto do relatório.
    // Subclasses preenchem cada etapa sem alterar a ordem.
    public final void imprimir(List<ReservaDTO> reservas, Date data) {
        imprimirCabecalho(data);

        if (reservas.isEmpty()) {
            imprimirVazio();
            return;
        }

        imprimirCorpo(reservas);
        imprimirRodape(reservas);
    }

    protected abstract void imprimirCabecalho(Date data);

    protected abstract void imprimirCorpo(List<ReservaDTO> reservas);

    protected abstract void imprimirRodape(List<ReservaDTO> reservas);

    // Hook: comportamento padrão para listas vazias; subclasses podem sobrescrever.
    protected void imprimirVazio() {
        System.out.println("  Nenhuma reserva confirmada para o período.");
        System.out.println();
    }
}
