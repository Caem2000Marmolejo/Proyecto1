package instalaciones;

import java.util.ArrayList;
import java.util.List;

public class GestorReservas {
    private List<Reserva> reservas = new ArrayList<>();
    private int maxSimultaneasTenis;
    private int maxSimultaneasPadel;
    private int maxSimultaneasTenisMesa;

    public GestorReservas(int maxTenis, int maxPadel, int maxTenisMesa) {
        this.maxSimultaneasTenis = maxTenis;
        this.maxSimultaneasPadel = maxPadel;
        this.maxSimultaneasTenisMesa = maxTenisMesa;
    }

    public boolean puedeReservar(Reserva nueva) {
        if (!nueva.validarCapacidad()) {
            return false;
        }

        String disciplina = nueva.getInstalacion().getDisciplina();
        String bloque = nueva.getBloqueHorario();

        int ocupadas = 0;
        for (Reserva r : reservas) {
            if (r.getInstalacion().getDisciplina().equalsIgnoreCase(disciplina)
                    && r.getBloqueHorario().equals(bloque)) {
                if (r.getInstalacion() == nueva.getInstalacion()) {
                    return false;
                }
                ocupadas++;
            }
        }

        int maximo = 0;
        if (disciplina.equalsIgnoreCase("Tenis")) maximo = maxSimultaneasTenis;
        else if (disciplina.equalsIgnoreCase("Padel")) maximo = maxSimultaneasPadel;
        else if (disciplina.equalsIgnoreCase("Tenis de Mesa")) maximo = maxSimultaneasTenisMesa;

        return ocupadas < maximo;
    }

    public boolean registrarReserva(Reserva r) {
        if (puedeReservar(r)) {
            reservas.add(r);
            return true;
        }
        return false;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }
}