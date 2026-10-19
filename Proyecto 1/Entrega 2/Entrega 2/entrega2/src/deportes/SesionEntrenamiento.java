package deportes;

import java.util.ArrayList;
import java.util.List;

import Usuarios.Entrenador;
import Usuarios.Socio;
import instalaciones.Instalacion;

public class SesionEntrenamiento {
    private String fecha;
    private String hora;
    private int duracion;
    private Entrenador planea;
    private Instalacion utiliza;
    private List<Socio> listaJugadoresConvocados;
    private List<AsistenciaEvaluacion> generaRegistros;

    public SesionEntrenamiento(String fecha, String hora, int duracion,
                               Entrenador planea, Instalacion utiliza) {
        this.fecha = fecha;
        this.hora = hora;
        this.duracion = duracion;
        this.planea = planea;
        this.utiliza = utiliza;
        this.listaJugadoresConvocados = new ArrayList<>();
        this.generaRegistros = new ArrayList<>();
    }

    public boolean convocarJugador(Socio s) {
        if (duracion <= 0) return false;
        if (utiliza == null) return false;
        for (Socio convocado : listaJugadoresConvocados) {
            if (convocado.getLogin().equals(s.getLogin())) {
                return false;
            }
        }
        listaJugadoresConvocados.add(s);
        return true;
    }

    public int convocarEquipo(Equipo equipo) {
        int convocados = 0;
        for (InscripcionEquipo ins : equipo.getNominaJugadores()) {
            if (!ins.isSuspendido()) {
                if (convocarJugador(ins.getSocio())) {
                    convocados++;
                }
            }
        }
        return convocados;
    }

    public boolean estaConvocado(Socio s) {
        for (Socio convocado : listaJugadoresConvocados) {
            if (convocado.getLogin().equals(s.getLogin())) {
                return true;
            }
        }
        return false;
    }

    public AsistenciaEvaluacion registrarAsistencia(Socio s, String estado, int nota, String comentario) {
        if (!estaConvocado(s)) {
            System.out.println("El jugador no esta convocado a la sesion.");
            return null;
        }
        for (AsistenciaEvaluacion a : generaRegistros) {
            if (a.getAsiste().getLogin().equals(s.getLogin())) {
                System.out.println("El jugador ya tiene asistencia registrada.");
                return null;
            }
        }
        AsistenciaEvaluacion registro = new AsistenciaEvaluacion(s, estado);
        if (nota > 0) {
            try {
                registro.setNotaDesempeno(nota);
            } catch (Exception e) {
                System.out.println("Nota invalida: " + e.getMessage());
            }
        }
        if (comentario != null && !comentario.isEmpty()) {
            registro.setComentario(comentario);
        }
        generaRegistros.add(registro);
        return registro;
    }

    public int contarAsistencias() {
        int count = 0;
        for (AsistenciaEvaluacion a : generaRegistros) {
            if (a.getEstadoAsistencia().equalsIgnoreCase("Asistio")) count++;
        }
        return count;
    }

    public int contarFaltas() {
        int count = 0;
        for (AsistenciaEvaluacion a : generaRegistros) {
            if (a.getEstadoAsistencia().equalsIgnoreCase("Falto")) count++;
        }
        return count;
    }

    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public int getDuracion() { return duracion; }
    public Entrenador getPlanea() { return planea; }
    public Instalacion getUtiliza() { return utiliza; }
    public List<Socio> getListaJugadoresConvocados() { return listaJugadoresConvocados; }
    public List<AsistenciaEvaluacion> getGeneraRegistros() { return generaRegistros; }
}