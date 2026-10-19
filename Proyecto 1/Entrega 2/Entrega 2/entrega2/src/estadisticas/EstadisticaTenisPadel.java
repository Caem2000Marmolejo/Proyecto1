package estadisticas;

import Usuarios.Socio;

public class EstadisticaTenisPadel extends EstadisticaIndividual {
    private int setsGanados;
    private int gamesGanados;
    private int aces;
    private int erroresNoForzados;

    public EstadisticaTenisPadel(Socio s) { 
        super(s); 
    }
}