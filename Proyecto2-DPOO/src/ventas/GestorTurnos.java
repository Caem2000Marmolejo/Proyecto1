package ventas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Usuarios.Empleado;
import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;

public class GestorTurnos {
    private Map<Turno, List<Empleado>> asignaciones = new HashMap<>();

    public void asignarTurno(Empleado e, Turno t) {
        asignaciones.putIfAbsent(t, new ArrayList<>());
        asignaciones.get(t).add(e);
        e.getAsignados().add(t); 
    }

    public int contarEntrenadores(Turno t) {
        if (!asignaciones.containsKey(t)) return 0;
        int count = 0;
        for (Empleado e : asignaciones.get(t)) {
            if (e instanceof Entrenador) count++;
        }
        return count;
    }

    public int contarFisioterapeutas(Turno t) {
        if (!asignaciones.containsKey(t)) return 0;
        int count = 0;
        for (Empleado e : asignaciones.get(t)) {
            if (e instanceof Fisioterapeuta) count++;
        }
        return count;
    }

    public boolean cumpleMinimo(Turno t) {
        return contarEntrenadores(t) >= 2 && contarFisioterapeutas(t) >= 1;
    }

    public boolean aprobarCambio(SolicitudCambioTurno solicitud) {
        Turno actual = solicitud.getTurnoActual();
        Turno nuevo = solicitud.getTurnoNuevo();
        Empleado empleado = solicitud.getSolicita();

        int entrenadoresActual = contarEntrenadores(actual);
        int fisiosActual = contarFisioterapeutas(actual);
        
        int entrenadoresNuevo = contarEntrenadores(nuevo);
        int fisiosNuevo = contarFisioterapeutas(nuevo);

        boolean dejaActualBien = true;
        boolean dejaNuevoBien = true;

        if (empleado instanceof Entrenador) {
            dejaActualBien = (entrenadoresActual - 1) >= 2;
            dejaNuevoBien = (entrenadoresNuevo + 1) >= 2 && fisiosNuevo >= 1;
        } else if (empleado instanceof Fisioterapeuta) {
            dejaActualBien = (fisiosActual - 1) >= 1;
            dejaNuevoBien = (fisiosNuevo + 1) >= 1 && entrenadoresNuevo >= 2;
        }

        if (dejaActualBien && dejaNuevoBien) {
            solicitud.setEstadoAprobacion("APROBADO");
            asignaciones.get(actual).remove(empleado);
            asignarTurno(empleado, nuevo);
            return true;
        }
        solicitud.setEstadoAprobacion("RECHAZADO");
        return false;
    }

    public Map<Turno, List<Empleado>> getAsignaciones() {
        return asignaciones;
    }
    
    private List<SolicitudCambioTurno> solicitudes = new ArrayList<>();

    public void registrarSolicitud(SolicitudCambioTurno solicitud) {
        solicitudes.add(solicitud);
    }

    public void consultarEstadoSolicitudes(Empleado empleado) {
        System.out.println("Solicitudes de cambio de turno de " + empleado.getLogin() + ":");
        boolean encontro = false;
        for (SolicitudCambioTurno s : solicitudes) {
            if (s.getSolicita().getLogin().equals(empleado.getLogin())) {
                System.out.println("  - Cambio de " + s.getTurnoActual().getDia() + " a " + s.getTurnoNuevo().getDia() + " | Estado: " + s.getEstadoAprobacion());
                encontro = true;
            }
        }
        if (!encontro) {
            System.out.println("  No hay solicitudes registradas.");
        }
    }
}