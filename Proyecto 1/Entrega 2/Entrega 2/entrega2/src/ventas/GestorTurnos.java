package ventas;

import java.util.ArrayList;
import java.util.List;

import Usuarios.Empleado;
import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;

public class GestorTurnos {
    private List<Turno> turnos = new ArrayList<>();
    private List<Empleado> empleadosAsignados = new ArrayList<>();

    public void asignarTurno(Empleado e, Turno t) {
        turnos.add(t);
        empleadosAsignados.add(e);
    }

    public int contarEntrenadores(Turno t) {
        int count = 0;
        for (int i = 0; i < turnos.size(); i++) {
            if (turnos.get(i).mismoTurno(t) && empleadosAsignados.get(i) instanceof Entrenador) {
                count++;
            }
        }
        return count;
    }

    public int contarFisioterapeutas(Turno t) {
        int count = 0;
        for (int i = 0; i < turnos.size(); i++) {
            if (turnos.get(i).mismoTurno(t) && empleadosAsignados.get(i) instanceof Fisioterapeuta) {
                count++;
            }
        }
        return count;
    }

    public boolean cumpleMinimo(Turno t) {
        return contarEntrenadores(t) >= 2 && contarFisioterapeutas(t) >= 1;
    }

    public boolean aprobarCambio(SolicitudCambioTurno solicitud) {
        Turno actual = solicitud.getTurnoActual();
        Turno nuevo = solicitud.getTurnoNuevo();

        int entrenadoresActual = contarEntrenadores(actual);
        int fisiosActual = contarFisioterapeutas(actual);
        int entrenadoresNuevo = contarEntrenadores(nuevo);
        int fisiosNuevo = contarFisioterapeutas(nuevo);

        boolean dejaActualBien = true;
        boolean dejaNuevoBien = true;

        if (solicitud.getSolicita() instanceof Entrenador) {
            dejaActualBien = (entrenadoresActual - 1) >= 2;
            dejaNuevoBien = (entrenadoresNuevo + 1) >= 2 && fisiosNuevo >= 1;
        } else if (solicitud.getSolicita() instanceof Fisioterapeuta) {
            dejaActualBien = (fisiosActual - 1) >= 1;
            dejaNuevoBien = (fisiosNuevo + 1) >= 1 && entrenadoresNuevo >= 2;
        }

        if (dejaActualBien && dejaNuevoBien) {
            solicitud.setEstadoAprobacion("APROBADO");
            return true;
        }
        solicitud.setEstadoAprobacion("RECHAZADO");
        return false;
    }

    public List<Turno> getTurnos() { return turnos; }
    public List<Empleado> getEmpleadosAsignados() { return empleadosAsignados; }
}