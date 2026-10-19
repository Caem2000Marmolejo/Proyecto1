package deportes;

import java.util.ArrayList;
import java.util.List;

public class PartidoInterno extends Partido {
    private String deporte;
    private List<Equipo> contrincantes = new ArrayList<>();
    public PartidoInterno(String fecha, String marcadorFinal, String deporte) {
        this.fecha = fecha;
        this.marcadorFinal = marcadorFinal;
        this.deporte = deporte;
    }
    public void agregarContrincante(Equipo e) {
        this.contrincantes.add(e);
    }
    public boolean validarParaDisputarse() {
        int minimo = 0;
        if (deporte.equalsIgnoreCase("futbol")) {
        	minimo = 7;
        }
        else if (deporte.equalsIgnoreCase("baloncesto")) {
        	minimo = 5;
        }
        else if (deporte.equalsIgnoreCase("voleibol")) {
        	minimo = 6;
        }

        for (Equipo equipo : contrincantes) {
            if (equipo.contarJugadoresHabilitados() < minimo) return false;
        }
        return true;
    }
}