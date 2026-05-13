package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import model.Pessoa;
import model.Reserva;
import model.Sala;
import model.StatusReserva;
import singleton.Configuracao;
import strategy.PoliticaDeReserva;

public class SistemaDeReservas {

    private static SistemaDeReservas instancia;

    private final List<Sala> salas = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();

    private SistemaDeReservas() {}

    public static synchronized SistemaDeReservas getInstance() {
        if (instancia == null) {
            instancia = new SistemaDeReservas();
        }
        return instancia;
    }

    public void adicionarSala(Sala sala) {
        salas.add(sala);
    }

    public List<Sala> getSalas() {
        return Collections.unmodifiableList(salas);
    }

    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    public List<Sala> listarSalasDisponiveis(Date dataInicio, Date dataFinal) {
        List<Sala> disponiveis = new ArrayList<>();
        for (Sala sala : salas) {
            boolean ocupada = false;
            for (Reserva reserva : reservas) {
                boolean mesmaSala = reserva.getSala().equals(sala);
                boolean dentroIntervalo = !reserva.getDia().before(dataInicio)
                        && !reserva.getDia().after(dataFinal);
                if (mesmaSala && dentroIntervalo) {
                    ocupada = true;
                    break;
                }
            }
            if (!ocupada) disponiveis.add(sala);
        }
        return disponiveis;
    }

    public Resultado reservarSala(Sala sala, Pessoa pessoa, Date data) {
        Reserva novaReserva = new Reserva(LocalDateTime.now(), data, StatusReserva.CONFIRMADO, sala, pessoa);
        Reserva jaExiste = achaReserva(sala, data);

        if (jaExiste != null) {
            PoliticaDeReserva politica = Configuracao.getInstance().getPoliticaDeReserva();
            if (politica.verificaColisao(jaExiste, novaReserva) == jaExiste) {
                return Resultado.JA_EXISTE;
            }
            jaExiste.notificarTodos(jaExiste);
            reservas.remove(jaExiste);
        }

        novaReserva.adicionarObservador(pessoa);
        reservas.add(novaReserva);
        return Resultado.SUCESSO;
    }

    public Resultado cancelarReserva(Sala sala, Pessoa pessoa, Date data) {
        Reserva reserva = achaReserva(sala, data);
        if (reserva == null) return Resultado.NAO_ENCONTRADO;
        if (reserva.getPessoa() != pessoa) return Resultado.NAO_AUTORIZADO;

        reserva.notificarTodos(reserva);
        reservas.remove(reserva);
        return Resultado.SUCESSO;
    }

    public Resultado editarReserva(Sala sala, Pessoa pessoa, Date data, Sala novaSala, Date novaData) {
        Reserva aEditar = achaReserva(sala, data);
        if (aEditar == null) return Resultado.NAO_ENCONTRADO;
        if (aEditar.getPessoa() != pessoa) return Resultado.NAO_AUTORIZADO;

        reservas.remove(aEditar);
        Resultado res = reservarSala(novaSala, pessoa, novaData);
        if (res == Resultado.JA_EXISTE) {
            reservas.add(aEditar);
        }
        return res;
    }

    public List<Reserva> relatorioDiario(Date data) {
        List<Reserva> relatorio = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getDia().equals(data)) {
                relatorio.add(reserva);
            }
        }
        relatorio.sort(Comparator.comparing(r -> r.getSala().getNumero()));
        return relatorio;
    }

    private Reserva achaReserva(Sala sala, Date data) {
        for (Reserva reserva : reservas) {
            if (reserva.getSala().equals(sala) && reserva.getDia().equals(data)) {
                return reserva;
            }
        }
        return null;
    }
}
