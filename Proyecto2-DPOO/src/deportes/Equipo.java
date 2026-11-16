package deportes;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Equipo {
    private String nombre;
    private String deporte;
    private String categoriaEdad;
    private int cupoMaximoJugadores;
    private int nominaMinimaHabilitados;
    private List<InscripcionEquipo> nominaJugadores = new ArrayList<>();

    public Equipo(String nombre, String deporte, String categoriaEdad, int cupoMaximoJugadores, int nominaMinimaHabilitados) {
        this.nombre = nombre;
        
        
        String deporteMin = deporte.toLowerCase();
        if (!deporteMin.equals("futbol") && !deporteMin.equals("baloncesto") && !deporteMin.equals("voleibol")) {
            throw new IllegalArgumentException("El club solo ofrece fútbol, baloncesto o voleibol.");
        }
        this.deporte = deporte;
        
        this.categoriaEdad = categoriaEdad;
        this.cupoMaximoJugadores = cupoMaximoJugadores;
        this.nominaMinimaHabilitados = nominaMinimaHabilitados;
    }

    public boolean validarEdad(int edadJugador) {
        if (categoriaEdad.equals("Sub-13") && edadJugador <= 13) { 
            return true;
        }
        if (categoriaEdad.equals("Sub-15") && edadJugador > 13 && edadJugador <= 15) {
            return true;
        }
        if (categoriaEdad.equals("Sub-17") && edadJugador > 15 && edadJugador <= 17) {
            return true;
        }
        if (categoriaEdad.equals("Mayores") && edadJugador >= 18) {
            return true;
        }
        return false;
    }

    public boolean agregarJugador(InscripcionEquipo nuevaInscripcion) {
        if (nuevaInscripcion == null || nuevaInscripcion.getSocio() == null) {
            return false;
        }
        if (!validarEdad(nuevaInscripcion.getSocio().getEdad())) {
            return false;
        }
        if (nominaJugadores.size() >= cupoMaximoJugadores) {
            return false;
        }
        for (InscripcionEquipo inscrito : nominaJugadores) {
            if (inscrito.getNumeroCamiseta() == nuevaInscripcion.getNumeroCamiseta()) {
                return false;
            }
            if (inscrito.getSocio().getLogin().equals(nuevaInscripcion.getSocio().getLogin())) {
                return false;
            }
        }
        nominaJugadores.add(nuevaInscripcion);
        return true;
    }

    public int contarJugadoresHabilitados() {
        int count = 0;
        for (InscripcionEquipo ins : nominaJugadores) {
            if (!ins.isSuspendido()) {
                count++;
            }
        }
        return count;
    }

    public String getDeporte() { 
        return deporte; 
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoriaEdad() {
        return categoriaEdad;
    }

    public List<InscripcionEquipo> getNominaJugadores() {
        return Collections.unmodifiableList(nominaJugadores);
    }

    public boolean retirarJugador(String login) {
        if (login == null || login.isBlank()) {
            return false;
        }

        return nominaJugadores.removeIf(
            ins -> login.equalsIgnoreCase(ins.getSocio().getLogin())
        );
    }
    public int getCupoMaximoJugadores() {
        return cupoMaximoJugadores;
    }

    public int getNominaMinimaHabilitados() {
        return nominaMinimaHabilitados;
    }
}