package model;

import java.time.LocalDateTime;
import java.util.Date;

import observer.Assunto;

public class Reserva extends Assunto {
    private LocalDateTime diaEHoraQueOcorreuAReserva;
    private Date dia;
    private StatusReserva status;
    private Sala sala;
    private Pessoa pessoa;

    public Reserva(LocalDateTime diaEHoraQueOcorreuAReserva, Date dia, StatusReserva status, Sala sala, Pessoa pessoa) {
        this.diaEHoraQueOcorreuAReserva = diaEHoraQueOcorreuAReserva;
        this.dia = dia;
        this.status = status;
        this.sala = sala;
        this.pessoa = pessoa;
    }

    public void setDiaQueOcorreuAReserva(LocalDateTime dia) {
        this.diaEHoraQueOcorreuAReserva = dia;
    }

    public LocalDateTime getDiaQueOcorreuAReserva() {
        return diaEHoraQueOcorreuAReserva;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }

    public Date getDia() {
        return dia;
    }

    public void setStatus(StatusReserva status) {
        this.status = status;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Sala getSala() {
        return sala;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }
}
