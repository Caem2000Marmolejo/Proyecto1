package estadisticas;

import Usuarios.Socio;

public class EstadisticaTenisPadel extends EstadisticaIndividual {
    private int setsGanados;
    private int gamesGanados;
    private int aces;
    private int erroresNoForzados;

    public EstadisticaTenisPadel(Socio s, int setsGanados, int gamesGanados, int aces, int erroresNoForzados) { 
        super(s); 
        this.setsGanados = setsGanados;
        this.gamesGanados = gamesGanados;
        this.aces = aces;
        this.erroresNoForzados = erroresNoForzados;
    }

    public int getSetsGanados() { return setsGanados; }
    public int getGamesGanados() { return gamesGanados; }
    public int getAces() { return aces; }
    public int getErroresNoForzados() { return erroresNoForzados; }
}