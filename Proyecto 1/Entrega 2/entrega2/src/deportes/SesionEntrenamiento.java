package deportes;

import java.util.ArrayList;
import java.util.List;

import Usuarios.Entrenador;
import instalaciones.Instalacion;

public class SesionEntrenamiento {
    private String fecha;
    private String hora;
    private int duracion;
    private List<String> listaJugadoresConvocados = new ArrayList<>();
    private Entrenador planea;
    private Instalacion utiliza;
    private List<AsistenciaEvaluacion> generaRegistros = new ArrayList<>();
}