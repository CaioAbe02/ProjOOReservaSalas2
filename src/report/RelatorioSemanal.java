package report;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Calendar;

public class RelatorioSemanal extends ServicoRelatorio {

    @Override
    protected void imprimirCabecalho(Date data) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(data);
        calendario.set(Calendar.DAY_OF_WEEK, calendario.getFirstDayOfWeek());
        Date inicioSemana = calendario.getTime();

        calendario.add(Calendar.DAY_OF_WEEK, 6);
        Date fimSemana = calendario.getTime();

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.printf( "║       RELATÓRIO SEMANAL DE RESERVAS - %s a %s║%n", FMT.format(inicioSemana), FMT.format(fimSemana));
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
            lista.forEach(r -> System.out.printf("  %s  |  %s  |  %s%n",
                FMT.format(r.getDia()),   // data da reserva
                r.getNomePessoa(),
                r.getTipoPessoa()
            ));
        });
    }

    @Override
    protected void imprimirRodape(List<ReservaDTO> reservas) {
        System.out.println();
        System.out.printf("  Total de reservas confirmadas: %d%n", reservas.size());
        System.out.println();
    }
}
