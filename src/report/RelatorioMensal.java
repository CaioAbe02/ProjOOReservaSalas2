package report;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class RelatorioMensal extends ServicoRelatorio {

    @Override
    protected void imprimirCabecalho(Date data) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(data);

        String mes = new SimpleDateFormat("MMMM/yyyy",
            new Locale("pt", "BR")).format(data);

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.printf( "║       RELATÓRIO MENSAL DE RESERVAS - %-24s║%n", mes.toUpperCase());
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }

    @Override
    protected void imprimirVazio() {
        System.out.println("  Nenhuma reserva confirmada para este dia.");
        System.out.println();
    }

    @Override
    protected void imprimirCorpo(List<ReservaDTO> reservas) {
        Map<String, List<ReservaDTO>> porSala = reservas.stream()
            .collect(Collectors.groupingBy(ReservaDTO::getNumeroSala));

        porSala.forEach((numeroSala, lista) -> {
            String tipoSala = lista.get(0).getTipoSala();
            System.out.printf("%n  > Sala %s - %s (%d reserva(s))%n", numeroSala, tipoSala, lista.size());
            System.out.println("  " + "─".repeat(60));
            lista.forEach(r -> System.out.println(r));
        });
    }

    @Override
    protected void imprimirRodape(List<ReservaDTO> reservas) {
        System.out.println();
        System.out.printf("  Total de reservas confirmadas: %d%n", reservas.size());
        System.out.println();
    }
}
