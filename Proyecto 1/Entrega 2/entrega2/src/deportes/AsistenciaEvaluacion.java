package deportes;

import Usuarios.Socio;

public class AsistenciaEvaluacion {
    private String estadoAsistencia; 
    private int notaDesempeno;
    private String comentario;
    private Socio asiste;

    public void setNotaDesempeno(int nota) throws Exception {
        if (nota >= 1 && nota <= 5) {
            this.notaDesempeno = nota;
        } else {
            throw new Exception("La nota debe estar entre 1 y 5");
        }
    }
}
