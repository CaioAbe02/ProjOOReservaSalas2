package report;

import java.util.Date;

import model.StatusReserva;

public class ReservaDTO {
    private final String nomePessoa;
    private final String tipoPessoa;
    private final String tipoSala;
    private final String numeroSala;
    private final Date dia;
    private final StatusReserva status;

    public ReservaDTO(String nomePessoa, String tipoPessoa, String tipoSala, String numeroSala, Date dia, StatusReserva status) {
        this.nomePessoa = nomePessoa;
        this.tipoPessoa = tipoPessoa;
        this.tipoSala = tipoSala;
        this.numeroSala = numeroSala;
        this.dia = dia;
        this.status = status;
    }

    public String getNomePessoa() { return nomePessoa; }
    public String getTipoPessoa() { return tipoPessoa; }
    public String getTipoSala() { return tipoSala; }
    public String getNumeroSala() { return numeroSala; }
    public Date getDia() { return dia; }
    public StatusReserva getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("  Sala %-8s (%-24s) | %-20s (%s) | Status: %s",
            numeroSala, tipoSala, nomePessoa, tipoPessoa, status);
    }
}
