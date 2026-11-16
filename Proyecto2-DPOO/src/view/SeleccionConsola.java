package view;

import Usuarios.*;
import deportes.*;
import instalaciones.*;
import ventas.*;
import java.util.List;

public class SeleccionConsola {
    private final ContextoConsola contexto;
    private final EntradaConsola entrada;

    public SeleccionConsola(
            ContextoConsola contexto,
            EntradaConsola entrada) {

        this.contexto = contexto;
        this.entrada = entrada;
    }

    public Socio socio() {
        if (contexto.socios.isEmpty()) {
            throw new IllegalStateException("No hay socios.");
        }

        for (int i = 0; i < contexto.socios.size(); i++) {
            System.out.println(
                (i + 1) + ". " + contexto.socios.get(i).getLogin()
            );
        }

        return contexto.socios.get(
            entrada.entero("Socio", 1, contexto.socios.size()) - 1
        );
    }

    public Usuario jugador() {
        for (int i = 0; i < contexto.cuentas.size(); i++) {
            System.out.println(
                (i + 1) + ". " + contexto.cuentas.get(i).getLogin()
            );
        }

        return contexto.cuentas.get(
            entrada.entero("Cuenta", 1, contexto.cuentas.size()) - 1
        );
    }

    public Equipo equipo() {
        if (contexto.equipos.isEmpty()) {
            throw new IllegalStateException("No hay equipos.");
        }

        for (int i = 0; i < contexto.equipos.size(); i++) {
            System.out.println(
                (i + 1) + ". " + contexto.equipos.get(i).getNombre()
            );
        }

        return contexto.equipos.get(
            entrada.entero("Equipo", 1, contexto.equipos.size()) - 1
        );
    }

    public Instalacion instalacion() {
        if (contexto.instalaciones.isEmpty()) {
            throw new IllegalStateException("No hay instalaciones.");
        }

        for (int i = 0; i < contexto.instalaciones.size(); i++) {
            Instalacion instalacion = contexto.instalaciones.get(i);

            System.out.println(
                (i + 1) + ". " + instalacion.getId()
                + " - " + instalacion.getDisciplina()
            );
        }

        return contexto.instalaciones.get(
            entrada.entero(
                "Instalacion",
                1,
                contexto.instalaciones.size()
            ) - 1
        );
    }

    public Empleado empleado() {
        if (contexto.empleados.isEmpty()) {
            throw new IllegalStateException("No hay empleados.");
        }

        for (int i = 0; i < contexto.empleados.size(); i++) {
            System.out.println(
                (i + 1) + ". " + contexto.empleados.get(i).getLogin()
            );
        }

        return contexto.empleados.get(
            entrada.entero("Empleado", 1, contexto.empleados.size()) - 1
        );
    }

    public Torneo torneo() {
        List<Torneo> torneos = contexto.torneos.getTorneos();

        if (torneos.isEmpty()) {
            throw new IllegalStateException("No hay torneos registrados.");
        }

        for (int i = 0; i < torneos.size(); i++) {
            System.out.println(
                (i + 1) + ". " + torneos.get(i).getNombre()
                + " - " + torneos.get(i).getEstado()
            );
        }

        return torneos.get(
            entrada.entero("Torneo", 1, torneos.size()) - 1
        );
    }

    public ParticipacionTorneo participacion(Torneo torneo) {
        List<ParticipacionTorneo> lista = torneo.getInscritos();

        if (lista.isEmpty()) {
            throw new IllegalStateException("No hay participaciones.");
        }

        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + lista.get(i).getNombre());
        }

        return lista.get(
            entrada.entero("Participacion", 1, lista.size()) - 1
        );
    }

    public SesionEntrenamiento sesion() {
        if (contexto.sesiones.isEmpty()) {
            throw new IllegalStateException("No hay sesiones registradas.");
        }

        for (int i = 0; i < contexto.sesiones.size(); i++) {
            System.out.println(
                (i + 1) + ". " + contexto.sesiones.get(i).getFecha()
                + " " + contexto.sesiones.get(i).getHora()
            );
        }

        return contexto.sesiones.get(
            entrada.entero("Sesion", 1, contexto.sesiones.size()) - 1
        );
    }

    public Turno turno() {
        if (contexto.turnosDisponibles.isEmpty()) {
            throw new IllegalStateException("No hay turnos registrados.");
        }

        for (int i = 0; i < contexto.turnosDisponibles.size(); i++) {
            System.out.println(
                (i + 1) + ". "
                + contexto.turnosDisponibles.get(i).getDia()
                + " " + contexto.turnosDisponibles.get(i).getHora()
            );
        }

        return contexto.turnosDisponibles.get(
            entrada.entero(
                "Turno",
                1,
                contexto.turnosDisponibles.size()
            ) - 1
        );
    }
}