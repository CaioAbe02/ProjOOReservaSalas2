import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import factory.FabricaGrupo;
import factory.FabricaIndividual;
import factory.FabricaLab;
import factory.FabricaSala;
import model.Aluno;
import model.Pessoa;
import model.Professor;
import model.Reserva;
import model.Sala;
import model.StatusReserva;
import report.ReservaDTO;
import report.ServicoRelatorio;
import service.Resultado;
import service.SistemaDeReservas;
import singleton.Configuracao;
import strategy.PrimeiroAReservar;
import strategy.PrioridadeParaDocente;

import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SistemaDeReservas sistema = SistemaDeReservas.getInstance();
    private static final ServicoRelatorio relatorio = new ServicoRelatorio();
    private static final List<Pessoa> pessoas = new ArrayList<>();
    private static Pessoa usuarioLogado;

    public static void main(String[] args) {
        FMT.setLenient(false);
        semearDados();
        usuarioLogado = telaDeLogin();
        menuPrincipal();
    }

    // ── dados iniciais ─────────────────────────────────────────────────────────

    private static void semearDados() {
        sistema.adicionarSala(new FabricaIndividual().criarSala("A101", 1));
        sistema.adicionarSala(new FabricaIndividual().criarSala("A102", 1));
        sistema.adicionarSala(new FabricaGrupo().criarSala("B201", 8));
        sistema.adicionarSala(new FabricaGrupo().criarSala("B202", 10));
        sistema.adicionarSala(new FabricaLab().criarSala("C301", 20));

        pessoas.add(new Aluno("Ana Lima"));
        pessoas.add(new Aluno("João Silva"));
        pessoas.add(new Professor("Prof. Souza"));
    }

    // ── login ──────────────────────────────────────────────────────────────────

    private static Pessoa telaDeLogin() {
        cabecalho("BEM-VINDO AO SISTEMA DE RESERVA DE SALAS");
        System.out.println("Quem é você?");
        System.out.println();
        listarPessoas();
        System.out.printf("  [%d] Cadastrar novo usuário%n", pessoas.size() + 1);
        System.out.println();

        int opcao = lerInt("Opção");
        if (opcao >= 1 && opcao <= pessoas.size()) {
            Pessoa p = pessoas.get(opcao - 1);
            System.out.printf("%nOlá, %s!%n", p.getNome());
            return p;
        }
        return fluxoCadastrarPessoa();
    }

    // ── menu principal ─────────────────────────────────────────────────────────

    private static void menuPrincipal() {
        while (true) {
            System.out.println();
            cabecalho("MENU PRINCIPAL  |  " + usuarioLogado.getNome()
                + " (" + usuarioLogado.getClass().getSimpleName() + ")");
            System.out.println("  [1] Listar salas disponíveis");
            System.out.println("  [2] Fazer reserva");
            System.out.println("  [3] Cancelar reserva");
            System.out.println("  [4] Editar reserva");
            System.out.println("  [5] Relatório diário");
            System.out.println("  [6] Configurações");
            System.out.println("  [7] Cadastrar pessoa");
            System.out.println("  [8] Trocar perfil");
            System.out.println("  [0] Sair");
            System.out.println();

            switch (lerInt("Opção")) {
                case 1 -> fluxoListarSalasDisponiveis();
                case 2 -> fluxoFazerReserva();
                case 3 -> fluxoCancelarReserva();
                case 4 -> fluxoEditarReserva();
                case 5 -> fluxoRelatorio();
                case 6 -> fluxoConfiguracoes();
                case 7 -> { fluxoCadastrarPessoa(); }
                case 8 -> { usuarioLogado = telaDeLogin(); }
                case 0 -> { System.out.println("Encerrando. Até logo!"); return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    // ── fluxos ────────────────────────────────────────────────────────────────

    private static void fluxoListarSalasDisponiveis() {
        cabecalho("SALAS DISPONÍVEIS");
        Date inicio = lerData("Data de início");
        Date fim    = lerData("Data de fim   ");

        List<Sala> disponiveis = sistema.listarSalasDisponiveis(inicio, fim);
        if (disponiveis.isEmpty()) {
            System.out.println("Nenhuma sala disponível nesse período.");
            return;
        }
        System.out.printf("%nSalas livres de %s a %s:%n%n", FMT.format(inicio), FMT.format(fim));
        for (Sala s : disponiveis) {
            System.out.printf("  %-6s %-24s  cap. %d%n",
                s.getNumero(), s.getClass().getSimpleName(), s.getCapacidade());
        }
    }

    private static void fluxoFazerReserva() {
        cabecalho("FAZER RESERVA");
        Date dia = lerData("Dia da reserva");

        List<Sala> todas      = sistema.getSalas();
        List<Sala> disponiveis = sistema.listarSalasDisponiveis(dia, dia);

        System.out.println("\nSalas:");
        for (int i = 0; i < todas.size(); i++) {
            Sala s = todas.get(i);
            String status = disponiveis.contains(s) ? "LIVRE  " : "OCUPADA";
            System.out.printf("  [%d] %-6s %-24s  cap. %-3d  %s%n",
                i + 1, s.getNumero(), s.getClass().getSimpleName(), s.getCapacidade(), status);
        }
        System.out.println();
        System.out.println("  (Política ativa: "
            + Configuracao.getInstance().getPoliticaDeReserva().getClass().getSimpleName()
            + " — salas OCUPADAS podem ser disputadas)");
        System.out.println();

        int idx = lerInt("Escolha a sala") - 1;
        if (idx < 0 || idx >= todas.size()) { System.out.println("Seleção inválida."); return; }

        Resultado res = sistema.reservarSala(todas.get(idx), usuarioLogado, dia);
        switch (res) {
            case SUCESSO    -> System.out.println("[SISTEMA] Reserva confirmada.");
            case JA_EXISTE  -> System.out.println("[SISTEMA] Reserva negada pela política de reserva.");
            default         -> System.out.println("[SISTEMA] Erro ao reservar: " + res);
        }
    }

    private static void fluxoCancelarReserva() {
        cabecalho("CANCELAR RESERVA");
        List<Reserva> minhas = reservasAtivas();
        if (minhas.isEmpty()) { System.out.println("Você não possui reservas ativas."); return; }

        imprimirReservas(minhas);
        int idx = lerInt("Número da reserva a cancelar") - 1;
        if (idx < 0 || idx >= minhas.size()) { System.out.println("Seleção inválida."); return; }

        Reserva r = minhas.get(idx);
        Resultado res = sistema.cancelarReserva(r.getSala(), usuarioLogado, r.getDia());
        switch (res) {
            case SUCESSO        -> System.out.println("[SISTEMA] Reserva cancelada.");
            case NAO_AUTORIZADO -> System.out.println("[SISTEMA] Você não tem permissão para cancelar esta reserva.");
            case NAO_ENCONTRADO -> System.out.println("[SISTEMA] Reserva não encontrada.");
            default             -> System.out.println("[SISTEMA] Erro: " + res);
        }
    }

    private static void fluxoEditarReserva() {
        cabecalho("EDITAR RESERVA");
        List<Reserva> minhas = reservasAtivas();
        if (minhas.isEmpty()) { System.out.println("Você não possui reservas ativas."); return; }

        imprimirReservas(minhas);
        int idx = lerInt("Número da reserva a editar") - 1;
        if (idx < 0 || idx >= minhas.size()) { System.out.println("Seleção inválida."); return; }
        Reserva reserva = minhas.get(idx);

        Date novoDia = lerData("Novo dia");

        List<Sala> todas      = sistema.getSalas();
        List<Sala> disponiveis = sistema.listarSalasDisponiveis(novoDia, novoDia);

        System.out.println("\nSalas:");
        for (int i = 0; i < todas.size(); i++) {
            Sala s = todas.get(i);
            String status = disponiveis.contains(s) ? "LIVRE  " : "OCUPADA";
            System.out.printf("  [%d] %-6s %-24s  cap. %-3d  %s%n",
                i + 1, s.getNumero(), s.getClass().getSimpleName(), s.getCapacidade(), status);
        }
        System.out.println();

        int sidx = lerInt("Escolha a nova sala") - 1;
        if (sidx < 0 || sidx >= todas.size()) { System.out.println("Seleção inválida."); return; }

        Resultado res = sistema.editarReserva(
            reserva.getSala(), usuarioLogado, reserva.getDia(),
            todas.get(sidx), novoDia
        );
        switch (res) {
            case SUCESSO        -> System.out.println("[SISTEMA] Reserva atualizada.");
            case JA_EXISTE      -> System.out.println("[SISTEMA] Nova reserva negada pela política. Reserva original mantida.");
            case NAO_AUTORIZADO -> System.out.println("[SISTEMA] Você não tem permissão para editar esta reserva.");
            case NAO_ENCONTRADO -> System.out.println("[SISTEMA] Reserva não encontrada.");
        }
    }

    private static void fluxoRelatorio() {
        cabecalho("RELATÓRIO DIÁRIO");
        Date dia = lerData("Data do relatório");

        List<Reserva> reservasDoDia = sistema.relatorioDiario(dia);
        List<ReservaDTO> dtos = reservasDoDia.stream()
            .map(r -> new ReservaDTO(
                r.getPessoa().getNome(),
                r.getPessoa().getClass().getSimpleName(),
                r.getSala().getClass().getSimpleName(),
                r.getSala().getNumero(),
                r.getDia(),
                r.getStatus()
            ))
            .collect(Collectors.toList());

        relatorio.imprimirRelatorioDiario(dtos, dia);
    }

    private static void fluxoConfiguracoes() {
        String politicaAtual = Configuracao.getInstance()
            .getPoliticaDeReserva().getClass().getSimpleName();

        cabecalho("CONFIGURAÇÕES");
        System.out.println("  [1] Trocar política de reserva  (atual: " + politicaAtual + ")");
        System.out.println("  [2] Cadastrar nova sala");
        System.out.println("  [0] Voltar");
        System.out.println();

        switch (lerInt("Opção")) {
            case 1 -> fluxoTrocarPolitica();
            case 2 -> fluxoCadastrarSala();
            case 0 -> { }
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void fluxoTrocarPolitica() {
        System.out.println("\n  [1] PrimeiroAReservar");
        System.out.println("  [2] PrioridadeParaDocente");
        System.out.println();
        switch (lerInt("Escolha a política")) {
            case 1 -> {
                Configuracao.getInstance().setPoliticaDeReserva(new PrimeiroAReservar());
                System.out.println("Política alterada para PrimeiroAReservar.");
            }
            case 2 -> {
                Configuracao.getInstance().setPoliticaDeReserva(new PrioridadeParaDocente());
                System.out.println("Política alterada para PrioridadeParaDocente.");
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void fluxoCadastrarSala() {
        System.out.println("\nTipo de sala:");
        System.out.println("  [1] Sala Individual");
        System.out.println("  [2] Sala de Grupo");
        System.out.println("  [3] Laboratório");
        System.out.println();
        int tipo = lerInt("Tipo");

        FabricaSala fabrica = switch (tipo) {
            case 1 -> new FabricaIndividual();
            case 2 -> new FabricaGrupo();
            case 3 -> new FabricaLab();
            default -> null;
        };
        if (fabrica == null) { System.out.println("Tipo inválido."); return; }

        String numero = lerString("Número da sala (ex: D401)");
        int cap = lerInt("Capacidade");

        sistema.adicionarSala(fabrica.criarSala(numero, cap));
        System.out.println("Sala " + numero + " cadastrada com sucesso.");
    }

    private static Pessoa fluxoCadastrarPessoa() {
        cabecalho("CADASTRAR PESSOA");
        String nome = lerString("Nome");
        System.out.println("  [1] Aluno");
        System.out.println("  [2] Professor");
        System.out.println();

        Pessoa nova = switch (lerInt("Tipo")) {
            case 1 -> new Aluno(nome);
            case 2 -> new Professor(nome);
            default -> null;
        };
        if (nova == null) { System.out.println("Tipo inválido."); return usuarioLogado; }

        pessoas.add(nova);
        System.out.println("Usuário " + nome + " cadastrado com sucesso.");
        return nova;
    }

    // ── helpers de listagem ────────────────────────────────────────────────────

    private static List<Reserva> reservasAtivas() {
        return sistema.getReservas().stream()
            .filter(r -> r.getStatus() == StatusReserva.CONFIRMADO
                      && r.getPessoa() == usuarioLogado)
            .collect(Collectors.toList());
    }

    private static void listarPessoas() {
        for (int i = 0; i < pessoas.size(); i++) {
            Pessoa p = pessoas.get(i);
            System.out.printf("  [%d] %-20s (%s)%n",
                i + 1, p.getNome(), p.getClass().getSimpleName());
        }
    }

    private static void imprimirReservas(List<Reserva> lista) {
        System.out.println();
        for (int i = 0; i < lista.size(); i++) {
            Reserva r = lista.get(i);
            System.out.printf("  [%d] Sala %-6s  %-24s  %s%n",
                i + 1,
                r.getSala().getNumero(),
                r.getSala().getClass().getSimpleName(),
                FMT.format(r.getDia()));
        }
        System.out.println();
    }

    // ── helpers de I/O ────────────────────────────────────────────────────────

    private static int lerInt(String label) {
        while (true) {
            System.out.print("  " + label + ": ");
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Digite um número inteiro.");
            }
        }
    }

    private static String lerString(String label) {
        System.out.print("  " + label + ": ");
        return sc.nextLine().trim();
    }

    private static Date lerData(String label) {
        while (true) {
            System.out.print("  " + label + " (dd/MM/yyyy ou 'hoje'): ");
            String input = sc.nextLine().trim();
            try {
                Date raw = input.equalsIgnoreCase("hoje") ? new Date() : FMT.parse(input);
                return meiaNuite(raw);
            } catch (ParseException e) {
                System.out.println("  Data inválida. Use dd/MM/yyyy.");
            }
        }
    }

    private static Date meiaNuite(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private static void cabecalho(String titulo) {
        String linha = "═".repeat(titulo.length() + 4);
        System.out.println("╔" + linha + "╗");
        System.out.println("║  " + titulo + "  ║");
        System.out.println("╚" + linha + "╝");
    }
}
