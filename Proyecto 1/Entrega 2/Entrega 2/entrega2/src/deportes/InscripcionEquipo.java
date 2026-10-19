package deportes;
import Usuarios.Socio;
public class InscripcionEquipo {
	private String posicion;
    private int numeroCamiseta;
    private int amarillasAcumuladas;
    private boolean suspendido;
    private Socio pertenece;

    public InscripcionEquipo(Socio pertenece, int numeroCamiseta) {
        this.pertenece = pertenece;
        this.numeroCamiseta = numeroCamiseta;
    }

    public void agregarAmarilla() {
        this.amarillasAcumuladas++;
        if (this.amarillasAcumuladas >= 2) {
            this.suspendido = true;
            this.amarillasAcumuladas = 0; 
        }
    }
    public void aplicarRojaDirecta() { 
    	this.suspendido = true; 
    	}

    public boolean isSuspendido() { 
    	return suspendido; 
    	}
    public void setSuspendido(boolean suspendido) { 
    	this.suspendido = suspendido;
    	}
    public Socio getSocio() { 
    	return pertenece; 
    }
    public int getNumeroCamiseta() { 
    	return numeroCamiseta; 
    	}
    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public String getPosicion() {
        return posicion;
    }

}
