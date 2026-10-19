package estadisticas;

import Usuarios.Socio;

public abstract class EstadisticaIndividual {
  
    protected Socio delJugador;

    public EstadisticaIndividual(Socio s) { 
        this.delJugador = s; 
    }
    
    public Socio getSocio() { 
    	return delJugador; 
    	}
}