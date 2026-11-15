package estadisticas;

import Usuarios.Socio;

public class EstadisticaBaloncesto extends EstadisticaIndividual {
    private int puntos;
    private int rebotes;

    public EstadisticaBaloncesto(Socio s, int puntos, int rebotes) { 
        super(s); 
        this.puntos = puntos;
        this.rebotes = rebotes;
    }

    public int getPuntos() { return puntos; }
    public int getRebotes() { return rebotes; }
}