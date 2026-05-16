# Template Method — Geração de Relatórios

## Intenção

Definir o **esqueleto** do algoritmo de geração de relatórios em uma classe-mãe (`ServicoRelatorio`), deixando que cada subclasse implemente os passos específicos (cabeçalho, corpo e rodapé) sem alterar a ordem do fluxo.

Todo relatório do sistema segue a mesma sequência:

```
cabeçalho → (lista vazia? → mensagem de vazio) 
→ corpo 
→ rodapé
```

O que muda entre `RelatorioDiario`, `RelatorioSemanal` e `RelatorioMensal` é **como** cada passo é renderizado (título do cabeçalho, formato das linhas, agregações no rodapé), não a sequência.

## Estrutura

```
        ServicoRelatorio  (abstrata)
        ──────────────────────────────────────
        + imprimir(reservas, data)    ← template method (final)
        + atualizar(reserva)          ← do Observer
        # imprimirCabecalho(data)     ← abstrato
        # imprimirCorpo(reservas)     ← abstrato
        # imprimirRodape(reservas)    ← abstrato
        # imprimirVazio()             ← hook com default
                                 △
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
       RelatorioDiario    RelatorioSemanal    RelatorioMensal
```

## Participantes

### `ServicoRelatorio` (AbstractClass)

Arquivo: [src/report/ServicoRelatorio.java](../src/report/ServicoRelatorio.java)

- **`imprimir(List<ReservaDTO>, Date)`** — o **template method**, marcado como `final` para garantir que nenhuma subclasse altere a ordem dos passos.
- **`imprimirCabecalho`, `imprimirCorpo`, `imprimirRodape`** — operações primitivas abstratas que cada relatório concreto preenche.
- **`imprimirVazio`** — *hook method* com implementação default ("Nenhuma reserva confirmada para o período."); subclasses podem sobrescrever para customizar a mensagem.
- **`atualizar(Reserva)`** — herdado de [`Observador`](../src/observer/Observador.java); permite que qualquer relatório também receba notificações push de mudanças de reserva.
- **`FMT`** — `SimpleDateFormat("dd/MM/yyyy")` protegido, reutilizado por todas as subclasses.

Fluxo do template method:

```java
public final void imprimir(List<ReservaDTO> reservas, Date data) {
    imprimirCabecalho(data);
    if (reservas.isEmpty()) {
        imprimirVazio();
        return;
    }
    imprimirCorpo(reservas);
    imprimirRodape(reservas);
}
```

### `RelatorioDiario` (ConcreteClass)

Arquivo: [src/report/RelatorioDiario.java](../src/report/RelatorioDiario.java)

| Passo | Comportamento |
|---|---|
| `imprimirCabecalho` | Caixa com borda dupla: `RELATÓRIO DIÁRIO DE RESERVAS - dd/MM/yyyy` |
| `imprimirVazio` | "Nenhuma reserva confirmada para este dia." |
| `imprimirCorpo` | Agrupa por número da sala; lista cada reserva via `ReservaDTO.toString()` |
| `imprimirRodape` | "Total de reservas confirmadas: N" |

### `RelatorioSemanal` (ConcreteClass)

Arquivo: [src/report/RelatorioSemanal.java](../src/report/RelatorioSemanal.java)

| Passo | Comportamento |
|---|---|
| `imprimirCabecalho` | Calcula o 1º dia da semana (locale) e soma 6 dias para o fim; cabeçalho `RELATÓRIO SEMANAL DE RESERVAS - dd/MM/yyyy a dd/MM/yyyy` |
| `imprimirVazio` | "Nenhuma reserva confirmada para este dia." |
| `imprimirCorpo` | Agrupa por sala e renderiza linhas com `data | nome | tipoPessoa` (a data importa porque a semana tem múltiplos dias) |
| `imprimirRodape` | "Total de reservas confirmadas: N" |

### `RelatorioMensal` (ConcreteClass)

Arquivo: [src/report/RelatorioMensal.java](../src/report/RelatorioMensal.java)

| Passo | Comportamento |
|---|---|
| `imprimirCabecalho` | Formata `MMMM/yyyy` com `Locale("pt","BR")` (em maiúsculas): `RELATÓRIO MENSAL DE RESERVAS - MAIO/2026` |
| `imprimirVazio` | "Nenhuma reserva confirmada para este dia." |
| `imprimirCorpo` | Agrupa por sala; lista reservas via `ReservaDTO.toString()` |
| `imprimirRodape` | "Total de reservas confirmadas: N" |

> **Observação:** as três subclasses sobrescrevem `imprimirVazio` com a mesma mensagem. Se essa duplicação incomodar, dá para movê-la para o default da classe-mãe e remover os overrides.

## Filtragem das reservas

A obtenção dos dados fica em [`SistemaDeReservas`](../src/service/SistemaDeReservas.java) — separada da renderização:

| Método | Janela considerada |
|---|---|
| `relatorioDiario(Date data)` | Apenas reservas com `dia.equals(data)` |
| `relatorioSemanal(Date data)` | 1º dia da semana → +6 dias (inclusivo) |
| `relatorioMensal(Date data)` | Dia 1 → último dia do mês (inclusivo) |

Cada método ordena por número de sala antes de devolver a lista. O `Main` converte `Reserva → ReservaDTO` e passa para o relatório correspondente.

## Como adicionar um novo tipo de relatório

1. Criar uma classe em `src/report/` que estenda `ServicoRelatorio`.
2. Implementar os três métodos abstratos (`imprimirCabecalho`, `imprimirCorpo`, `imprimirRodape`).
3. Opcionalmente sobrescrever `imprimirVazio` se a mensagem padrão não servir.
4. Se a janela de filtragem for diferente das já existentes, adicionar um método correspondente em `SistemaDeReservas`.
5. Plugar no menu de `Main` (novo `fluxoRelatorioX`).

Exemplo (esqueleto):

```java
public class RelatorioPorPessoa extends ServicoRelatorio {

    @Override
    protected void imprimirCabecalho(Date data) {
        System.out.printf("=== Reservas agrupadas por pessoa - %s ===%n", FMT.format(data));
    }

    @Override
    protected void imprimirCorpo(List<ReservaDTO> reservas) {
        reservas.stream()
            .collect(Collectors.groupingBy(ReservaDTO::getNomePessoa))
            .forEach((pessoa, lista) -> {
                System.out.printf("%n%s (%d reserva(s))%n", pessoa, lista.size());
                lista.forEach(System.out::println);
            });
    }

    @Override
    protected void imprimirRodape(List<ReservaDTO> reservas) {
        System.out.printf("%nTotal: %d%n", reservas.size());
    }
}
```

Nenhuma mudança em `ServicoRelatorio` nem no fluxo do template method — princípio Aberto/Fechado preservado.

## Consequências

**Vantagens**

- A ordem dos passos é garantida pela classe-mãe (`final`) é impossível um relatório esquecer o cabeçalho ou trocar a ordem do rodapé.
- Código repetido entre relatórios (sequência de impressão, fallback para vazio, formatador de data) fica concentrado em um lugar só.
- Novos relatórios = nova subclasse; o cliente (`Main`) não precisa mudar a forma de chamar.
- Combina com o **Observer** já existente: qualquer relatório pode ser registrado como ouvinte e reagir a mudanças de reserva via `atualizar`.

**Cuidados**

- Inversão de controle ("Hollywood principle"): a subclasse **não** chama a classe-mãe, a classe-mãe é que chama a subclasse nos pontos de extensão.
- Mudanças na sequência do algoritmo exigem mexer na classe-mãe e podem impactar todas as subclasses.
- Evitar criar hooks demais: cada ponto de extensão precisa ter uma razão concreta. Hoje só `imprimirVazio` é hook, os outros são abstratos para forçar a implementação.
