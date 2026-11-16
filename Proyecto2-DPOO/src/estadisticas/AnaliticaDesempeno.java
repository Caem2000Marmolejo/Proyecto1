package estadisticas;

import java.util.ArrayList;
import java.util.List;

import Usuarios.Socio;
import deportes.AsistenciaEvaluacion;

public class AnaliticaDesempeno {
    private List<AsistenciaEvaluacion> registrosAsistencia = new ArrayList<>();
    private List<EstadisticaIndividual> estadisticasPartidos = new ArrayList<>();

    public void agregarRegistro(AsistenciaEvaluacion a) {
        registrosAsistencia.add(a);
    }

    public void agregarEstadisticaPartido(EstadisticaIndividual e) {
        estadisticasPartidos.add(e);
    }

    public int contarAsistencias(Socio s) {
        int count = 0;
        for (AsistenciaEvaluacion a : registrosAsistencia) {
            if (a.getAsiste().getLogin().equals(s.getLogin()) && a.getEstadoAsistencia().equalsIgnoreCase("Asistio")) {
                count++;
            }
        }
        return count;
    }

    public int contarFaltas(Socio s) {
        int count = 0;
        for (AsistenciaEvaluacion a : registrosAsistencia) {
            if (a.getAsiste().getLogin().equals(s.getLogin()) && a.getEstadoAsistencia().equalsIgnoreCase("Falto")) {
                count++;
            }
        }
        return count;
    }

    public int contarLlegadasTarde(Socio s) {
        int count = 0;
        for (AsistenciaEvaluacion a : registrosAsistencia) {
            if (a.getAsiste().getLogin().equals(s.getLogin()) && a.getEstadoAsistencia().equalsIgnoreCase("Llego tarde")) {
                count++;
            }
        }
        return count;
    }

    public double promedioNotas(Socio s) {
        double suma = 0;
        int count = 0;
        for (AsistenciaEvaluacion a : registrosAsistencia) {
            if (a.getAsiste().getLogin().equals(s.getLogin()) && a.tieneEvaluacion()) {
                suma += a.getNotaDesempeno();
                count++;
            }
        }
        if (count == 0) return 0;
        return suma / count;
    }

    public int totalGolesJugador(Socio s) {
        int goles = 0;
        for (EstadisticaIndividual e : estadisticasPartidos) {
            if (e.getSocio().getLogin().equals(s.getLogin()) && e instanceof EstadisticaFutbol) {
                goles += ((EstadisticaFutbol) e).getGoles();
            }
        }
        return goles;
    }

    public void mostrarResumen(Socio s) {
        System.out.println("Analitica de " + s.getLogin() + ":");
        System.out.println("  Asistencias: " + contarAsistencias(s));
        System.out.println("  Faltas: " + contarFaltas(s));
        System.out.println("  Llegadas tarde: " + contarLlegadasTarde(s));
        System.out.println("  Promedio de notas: " + promedioNotas(s));
        System.out.println("  Goles en partidos: " + totalGolesJugador(s));
    }
}