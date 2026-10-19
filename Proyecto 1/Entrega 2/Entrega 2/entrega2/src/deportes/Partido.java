package deportes;
import java.util.ArrayList;
import java.util.List;

import estadisticas.EstadisticaIndividual;

public abstract class Partido {
	protected String fecha;
    protected String marcadorFinal;
    protected List<EstadisticaIndividual> registra = new ArrayList<>();

    
    public void agregarEstadistica(EstadisticaIndividual e) {
        registra.add(e);
    }

    public List<EstadisticaIndividual> getRegistra() {
        return registra;
    }
    public String getFecha() { return fecha; }
    public String getMarcadorFinal() { return marcadorFinal; }
}