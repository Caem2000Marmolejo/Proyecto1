package deportes;
import java.util.ArrayList;
import java.util.List;

import estadisticas.EstadisticaIndividual;

public abstract class Partido {
	protected String fecha;
    protected String marcadorFinal;
    protected List<EstadisticaIndividual> registra = new ArrayList<>();
}