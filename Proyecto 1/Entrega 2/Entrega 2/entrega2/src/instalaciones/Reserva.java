package instalaciones;
import java.util.ArrayList;
import java.util.List;

import Usuarios.Socio;
public class Reserva {
	private String bloqueHorario;
    private int cantidadJugadores;
    private String modalidad;
    private List<Socio> participan = new ArrayList<>();
    private Instalacion ocupaInstalacion;
    public Reserva(String bloqueHorario, int cantidadJugadores, String modalidad, Instalacion ocupaInstalacion) {
        this.bloqueHorario = bloqueHorario;
        this.cantidadJugadores = cantidadJugadores;
        this.modalidad = modalidad;
        this.ocupaInstalacion = ocupaInstalacion;}
    public void agregarParticipante(Socio s) {
        this.participan.add(s);
    }

    public boolean validarCapacidad() {
        String disc = ocupaInstalacion.getDisciplina();
        
        if (disc.equalsIgnoreCase("padel")) {
            return cantidadJugadores == 4 && modalidad.equalsIgnoreCase("dobles");
        } 
        else if (disc.equalsIgnoreCase("tenis") || disc.equalsIgnoreCase("tenis de mesa")) {
            if (modalidad.equalsIgnoreCase("sencillos") && cantidadJugadores == 2) {
            	return true;
            }
            if (modalidad.equalsIgnoreCase("dobles") && cantidadJugadores == 4) {
            	return true;
            }
        }
        return false;
    }

    public Instalacion getInstalacion() { 
    	return ocupaInstalacion; 
    	}
    public List<Socio> getParticipantes() { 
    	return participan; 
    	}
    public String getBloqueHorario() {
        return bloqueHorario;
    }

    public int getCantidadJugadores() {
        return cantidadJugadores;
    }

    public String getModalidad() {
        return modalidad;
    }
}
