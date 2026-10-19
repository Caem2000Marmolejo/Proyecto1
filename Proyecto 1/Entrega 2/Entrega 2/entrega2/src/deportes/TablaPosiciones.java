package deportes;

import java.util.ArrayList;
import java.util.List;

public class TablaPosiciones {
    private List<Equipo> equipos;
    private List<Integer> puntos;
    private List<Integer> partidosJugados;

    public TablaPosiciones(List<Equipo> equipos) {
        this.equipos = new ArrayList<>(equipos);
        this.puntos = new ArrayList<>();
        this.partidosJugados = new ArrayList<>();
        for (int i = 0; i < equipos.size(); i++) {
            puntos.add(0);
            partidosJugados.add(0);
        }
    }

    public void registrarResultado(Equipo equipo, int puntosGanados) {
        for (int i = 0; i < equipos.size(); i++) {
            if (equipos.get(i).getNombre().equalsIgnoreCase(equipo.getNombre())) {
                puntos.set(i, puntos.get(i) + puntosGanados);
                partidosJugados.set(i, partidosJugados.get(i) + 1);
            }
        }
    }

    public void mostrar() {
        System.out.println("Tabla de posiciones:");
        for (int i = 0; i < equipos.size(); i++) {
            System.out.println("  " + equipos.get(i).getNombre() + " - Puntos: " + puntos.get(i)
                    + " - PJ: " + partidosJugados.get(i));
        }
    }

    public int getPuntos(Equipo equipo) {
        for (int i = 0; i < equipos.size(); i++) {
            if (equipos.get(i).getNombre().equalsIgnoreCase(equipo.getNombre())) {
                return puntos.get(i);
            }
        }
        return 0;
    }
}