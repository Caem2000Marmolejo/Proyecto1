package estadisticas;

import Usuarios.Socio;

public class EstadisticaVoleibol extends EstadisticaIndividual {
    private int aces;
    private int bloqueos;

    public EstadisticaVoleibol(Socio s, int aces, int bloqueos) { 
        super(s); 
        this.aces = aces;
        this.bloqueos = bloqueos;
    }

    public int getAces() { return aces; }
    public int getBloqueos() { return bloqueos; }
}