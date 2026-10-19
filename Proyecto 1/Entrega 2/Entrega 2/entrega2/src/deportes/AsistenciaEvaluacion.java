package deportes;

import Usuarios.Socio;

public class AsistenciaEvaluacion {
    private String estadoAsistencia;
    private int notaDesempeno;
    private String comentario;
    private Socio asiste;

    public AsistenciaEvaluacion(Socio asiste, String estadoAsistencia) {
        this.asiste = asiste;
        setEstadoAsistencia(estadoAsistencia);
        this.notaDesempeno = 0;
        this.comentario = "";
    }

    public void setEstadoAsistencia(String estado) {
        if (estado.equalsIgnoreCase("Asistio") || estado.equalsIgnoreCase("Falto")
                || estado.equalsIgnoreCase("Llego tarde")) {
            this.estadoAsistencia = estado;
        } else {
            this.estadoAsistencia = "Falto";
        }
    }

    public void setNotaDesempeno(int nota) throws Exception {
        if (nota >= 1 && nota <= 5) {
            this.notaDesempeno = nota;
        } else {
            throw new Exception("La nota debe estar entre 1 y 5");
        }
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getEstadoAsistencia() { return estadoAsistencia; }
    public int getNotaDesempeno() { return notaDesempeno; }
    public String getComentario() { return comentario; }
    public Socio getAsiste() { return asiste; }
    public boolean tieneEvaluacion() { return notaDesempeno > 0; }
}