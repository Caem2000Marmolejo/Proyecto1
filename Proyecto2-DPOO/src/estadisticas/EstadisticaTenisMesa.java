package estadisticas;

import Usuarios.Socio;

public class EstadisticaTenisMesa extends EstadisticaIndividual {
    private int setsGanados;
    private int puntosTotales;

    public EstadisticaTenisMesa(Socio s, int setsGanados, int puntosTotales) { 
        super(s); 
        this.setsGanados = setsGanados;
        this.puntosTotales = puntosTotales;
    }

    public int getSetsGanados() { return setsGanados; }
    public int getPuntosTotales() { return puntosTotales; }
}