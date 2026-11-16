package deportes;


import java.util.List;

public class GestorEquipos {

    private final List<Equipo> equipos;

    public GestorEquipos(List<Equipo> equipos) {
        this.equipos = equipos;
    }

    public boolean inscribirJugador(Equipo destino, InscripcionEquipo inscripcion) {
        if (destino == null || inscripcion == null || inscripcion.getSocio() == null) {
            return false;
        }
        if (!equipos.contains(destino)) {
            return false;
        }

        String login = inscripcion.getSocio().getLogin();
        if (login == null || login.isBlank()) {
            return false;
        }

        
        for (Equipo equipo : equipos) {
            if (equipo.getDeporte().trim().equalsIgnoreCase(destino.getDeporte().trim())) {
                for (InscripcionEquipo existente : equipo.getNominaJugadores()) {
                    if (login.equalsIgnoreCase(existente.getSocio().getLogin())) {
                        System.out.println("El jugador ya está inscrito en un equipo de " + destino.getDeporte());
                        return false;
                    }
                }
            }
        }

        
        return destino.agregarJugador(inscripcion);
    }
}   
    