package report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Reserva;
import observer.Observador;

public class ServicoRelatorio implements Observador {

    private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");

    // push: dados chegam prontos via atualizar()
    @Override
    public void atualizar(Reserva reserva) {
        System.out.printf("[RELATÓRIO] Reserva na sala %s (%s) foi alterada para %s.%n",
            reserva.getSala().getNumero(),
            FMT.format(reserva.getDia()),
            reserva.getStatus());
    }

    // pull: recebe lista pré-filtrada e formata o relatório agrupado por sala
    public void imprimirRelatorioDiario(List<ReservaDTO> reservas, Date data) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.printf( "║       RELATÓRIO DIÁRIO DE RESERVAS — %s           ║%n", FMT.format(data));
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        if (reservas.isEmpty()) {
            System.out.println("  Nenhuma reserva confirmada para este dia.");
            System.out.println();
            return;
        }

        Map<String, List<ReservaDTO>> porSala = reservas.stream()
            .collect(Collectors.groupingBy(ReservaDTO::getNumeroSala));

        porSala.forEach((numeroSala, lista) -> {
            String tipoSala = lista.get(0).getTipoSala();
            System.out.printf("%n  ► Sala %s — %s (%d reserva(s))%n", numeroSala, tipoSala, lista.size());
            System.out.println("  " + "─".repeat(60));
            lista.forEach(r -> System.out.println(r));
        });

        System.out.println();
        System.out.printf("  Total de reservas confirmadas: %d%n", reservas.size());
        System.out.println();
    }
}
