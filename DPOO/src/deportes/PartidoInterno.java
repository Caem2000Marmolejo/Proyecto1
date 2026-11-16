package deportes;

import java.util.ArrayList;
import java.util.List;
import estadisticas.EstadisticaIndividual;
import estadisticas.EstadisticaFutbol;

public class PartidoInterno extends Partido {
    private String deporte;
    private List<Equipo> contrincantes = new ArrayList<>();
    
    public PartidoInterno(String fecha, String marcadorFinal, String deporte) {
        this.fecha = fecha;
        this.marcadorFinal = marcadorFinal;
        this.deporte = deporte;
    }
    
    public void agregarContrincante(Equipo e) {
        this.contrincantes.add(e);
    }

    
    @Override
    public void agregarEstadistica(EstadisticaIndividual e) {
        boolean perteneceAlPartido = false;
        for (Equipo eq : contrincantes) {
            for (InscripcionEquipo ins : eq.getNominaJugadores()) {
                if (ins.getSocio().getLogin().equals(e.getSocio().getLogin())) {
                    perteneceAlPartido = true;
                    break;
                }
            }
        }
        if (perteneceAlPartido) {
            super.agregarEstadistica(e);
        } else {
            System.out.println("Error: El jugador " + e.getSocio().getLogin() + " no pertenece a los equipos de este partido.");
        }
    }
    
    public boolean validarParaDisputarse() {
        if (!validarMismaCategoriaYDeporte()) {
            return false;
        }

        int minimo = 0;
        if (deporte.equalsIgnoreCase("futbol")) minimo = 7;
        else if (deporte.equalsIgnoreCase("baloncesto")) minimo = 5;
        else if (deporte.equalsIgnoreCase("voleibol")) minimo = 6;

        for (Equipo equipo : contrincantes) {
            if (equipo.contarJugadoresHabilitados() < minimo) {
                System.out.println("El equipo " + equipo.getNombre() + " no tiene el mínimo de " + minimo + " jugadores habilitados.");
                return false;
            }
        }
        return true;
    }
    
    public boolean validarMismaCategoriaYDeporte() {
        if (contrincantes.size() != 2) {
            System.out.println("Un partido interno debe tener exactamente 2 equipos.");
            return false;
        }
        Equipo a = contrincantes.get(0);
        Equipo b = contrincantes.get(1);
        
        boolean mismoDeporte = a.getDeporte().equalsIgnoreCase(b.getDeporte());
        boolean mismaCategoria = a.getCategoriaEdad().equalsIgnoreCase(b.getCategoriaEdad());
        
        if (!mismoDeporte || !mismaCategoria) {
            System.out.println("Los equipos deben ser del mismo deporte y categoría.");
            return false;
        }
        return true;
    }

    
    public void finalizarPartidoYProcesarSanciones() {
        
        for (Equipo equipo : contrincantes) {
            for (InscripcionEquipo ins : equipo.getNominaJugadores()) {
                if (ins.isSuspendido()) {
                    ins.cumplirSancion(); 
                }
            }
        }

        
        for (EstadisticaIndividual est : registra) {
            if (est instanceof EstadisticaFutbol) {
                EstadisticaFutbol ef = (EstadisticaFutbol) est;
                for (Equipo eq : contrincantes) {
                    for (InscripcionEquipo ins : eq.getNominaJugadores()) {
                        if (ins.getSocio().getLogin().equals(ef.getSocio().getLogin())) {
                            ef.procesarSanciones(ins);
                        }
                    }
                }
            }
        }
    }
}