package deportes;

import java.util.ArrayList;
import java.util.List;
import Usuarios.Socio;

public class HistorialPartidos {
    private List<PartidoPractica> partidosPractica = new ArrayList<>();

    public void registrarPartido(PartidoPractica partido) {
        partidosPractica.add(partido);
    }

    public List<PartidoPractica> consultarHistorialSocio(Socio socio) {
        List<PartidoPractica> historial = new ArrayList<>();
        for (PartidoPractica p : partidosPractica) {
            if (p.getJugador1().getLogin().equals(socio.getLogin()) || 
                p.getJugador2().getLogin().equals(socio.getLogin())) {
                historial.add(p);
            }
        }
        return historial;
    }
    
    public void mostrarHistorialSocio(Socio socio) {
        System.out.println("Historial de partidos de práctica de " + socio.getLogin() + ":");
        for (PartidoPractica p : consultarHistorialSocio(socio)) {
            System.out.println("  Fecha: " + p.getFecha() + " - Disciplina: " + p.getDisciplina());
        }
    }
}