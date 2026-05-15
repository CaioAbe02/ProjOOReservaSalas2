package report;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RelatorioDiario extends ServicoRelatorio {

    @Override
    protected void imprimirCabecalho(Date data) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.printf( "║       RELATÓRIO DIÁRIO DE RESERVAS - %s           ║%n", FMT.format(data));
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
