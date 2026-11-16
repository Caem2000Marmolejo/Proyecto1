package deportes;

import Usuarios.Socio;
import Usuarios.Usuario;
import estadisticas.EstadisticaIndividual;

public class PartidoPractica extends Partido {
    private String disciplina;
    private Usuario jugador1;
    private Usuario jugador2;

    public PartidoPractica(String fecha, String disciplina, Usuario jugador1, Usuario jugador2) {
        this.fecha = fecha;
        this.disciplina = disciplina;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
    }

    public boolean validarNivelesAdyacentes() {
        boolean esEntrenador = !(jugador1 instanceof Socio) || !(jugador2 instanceof Socio);
        return validarNivelesAdyacentes(jugador1, jugador2, esEntrenador);
    }

    public boolean validarNivelesAdyacentes(Usuario s1, Usuario s2, boolean unoEsEntrenador) {
        if (unoEsEntrenador) {
            return true;
        }

        if (!(s1 instanceof Socio) || !(s2 instanceof Socio)) {
            return false;
        }

        String nivel1 = ((Socio) s1).getNivelRaqueta();
        String nivel2 = ((Socio) s2).getNivelRaqueta();

        if (nivel1.equals(nivel2)) {
            return true;
        }
        if (nivel1.equals("Intermedio") && nivel2.equals("Avanzado")) return true;
        if (nivel1.equals("Avanzado") && nivel2.equals("Intermedio")) return true;

        return false;
    }

    public void agregarEstadistica(EstadisticaIndividual e) {
        registra.add(e);
    }

    public void actualizarRanking(Usuario ganador, Usuario perdedor) {
        if (ganador instanceof Socio) {
            Socio socioGanador = (Socio) ganador;
            int rankingActual = socioGanador.getRanking(disciplina);
            socioGanador.setRanking(disciplina, rankingActual + 20);
        }
        if (perdedor instanceof Socio) {
            Socio socioPerdedor = (Socio) perdedor;
            int rankingActual = socioPerdedor.getRanking(disciplina);
            int nuevoRanking = rankingActual - 20;
            if (nuevoRanking < 0) nuevoRanking = 0;
            socioPerdedor.setRanking(disciplina, nuevoRanking);
        }
    }

    public String getDisciplina() { return disciplina; }
    public Usuario getJugador1() { return jugador1; }
    public Usuario getJugador2() { return jugador2; }
}