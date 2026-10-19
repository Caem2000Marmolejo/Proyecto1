package deportes;

import Usuarios.Socio;
import estadisticas.EstadisticaIndividual;

public class PartidoPractica extends Partido {
    private String disciplina;
    private Socio jugador1;
    private Socio jugador2;

    public PartidoPractica(String fecha, String disciplina, Socio jugador1, Socio jugador2) {
        this.fecha = fecha;
        this.disciplina = disciplina;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
    }

    public boolean validarNivelesAdyacentes() {
        return validarNivelesAdyacentes(jugador1, jugador2, false);
    }

    public boolean validarNivelesAdyacentes(Socio s1, Socio s2, boolean unoEsEntrenador) {
        if (unoEsEntrenador) {
            return true;
        }

        String nivel1 = s1.getNivelRaqueta();
        String nivel2 = s2.getNivelRaqueta();

        if (nivel1.equals(nivel2)) {
            return true;
        }
        if (nivel1.equals("Principiante") && nivel2.equals("Intermedio")) return true;
        if (nivel1.equals("Intermedio") && nivel2.equals("Principiante")) return true;
        if (nivel1.equals("Intermedio") && nivel2.equals("Avanzado")) return true;
        if (nivel1.equals("Avanzado") && nivel2.equals("Intermedio")) return true;

        return false;
    }

    public void agregarEstadistica(EstadisticaIndividual e) {
        registra.add(e);
    }

    public void actualizarRanking(Socio ganador, Socio perdedor) {
        int rankingGanador = ganador.getRanking(disciplina);
        int rankingPerdedor = perdedor.getRanking(disciplina);

        ganador.setRanking(disciplina, rankingGanador + 20);
        int nuevoPerdedor = rankingPerdedor - 20;
        if (nuevoPerdedor < 0) nuevoPerdedor = 0;
        perdedor.setRanking(disciplina, nuevoPerdedor);
    }

    public String getDisciplina() { return disciplina; }
    public Socio getJugador1() { return jugador1; }
    public Socio getJugador2() { return jugador2; }
}