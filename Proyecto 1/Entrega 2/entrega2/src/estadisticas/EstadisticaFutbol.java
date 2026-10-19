package estadisticas;

import Usuarios.Socio;
import deportes.InscripcionEquipo;

public class EstadisticaFutbol extends EstadisticaIndividual {
    private int goles;
    private int asistencias;
    private int amarillas;
    private int rojas;

    public EstadisticaFutbol(Socio s, int goles, int amarillas, int rojas) {
        super(s);
        this.goles = goles;
        this.amarillas = amarillas;
        this.rojas = rojas;
    }
    
    public void procesarSanciones(InscripcionEquipo fichaJugador) {
        if (this.rojas > 0) {
            fichaJugador.aplicarRojaDirecta();
        }
        for (int i = 0; i < this.amarillas; i++) {
            fichaJugador.agregarAmarilla();
        }
    }
}