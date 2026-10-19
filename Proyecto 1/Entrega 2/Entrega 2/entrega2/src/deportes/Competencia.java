package deportes;

import java.util.ArrayList;
import java.util.List;

public class Competencia {
    private String nombre;
    private String entidadOrganizadora;
    private String temporada;
    private String deporte;
    private String categoriaHabilitada;
    private String formatoJuego;
    private int nominaMinimaRequerida;
    private List<Equipo> equiposInscritos = new ArrayList<>();
    private List<InscripcionEquipo> nominaCompetencia = new ArrayList<>();

    public Competencia(String nombre, String entidadOrganizadora, String temporada,
                       String deporte, String categoriaHabilitada, String formatoJuego,
                       int nominaMinimaRequerida) {
        this.nombre = nombre;
        this.entidadOrganizadora = entidadOrganizadora;
        this.temporada = temporada;
        this.deporte = deporte;
        this.categoriaHabilitada = categoriaHabilitada;
        this.formatoJuego = formatoJuego;
        this.nominaMinimaRequerida = nominaMinimaRequerida;
    }

    public boolean validarInscripcion(Equipo equipo) {
        if (!equipo.getDeporte().equalsIgnoreCase(deporte)) {
            return false;
        }
        if (!equipo.getCategoriaEdad().equalsIgnoreCase(categoriaHabilitada)) {
            return false;
        }
        if (equipo.contarJugadoresHabilitados() < nominaMinimaRequerida) {
            return false;
        }
        return true;
    }

    public boolean inscribirEquipo(Equipo equipo) {
        if (!validarInscripcion(equipo)) {
            return false;
        }
        for (Equipo e : equiposInscritos) {
            if (e.getNombre().equalsIgnoreCase(equipo.getNombre())) {
                return false;
            }
        }
        equiposInscritos.add(equipo);
        for (InscripcionEquipo ins : equipo.getNominaJugadores()) {
            if (!ins.isSuspendido()) {
                nominaCompetencia.add(ins);
            }
        }
        return true;
    }

    public boolean jugadorPuedeJugar(String loginJugador) {
        for (InscripcionEquipo ins : nominaCompetencia) {
            if (ins.getSocio().getLogin().equals(loginJugador)) {
                return !ins.isSuspendido();
            }
        }
        return false;
    }

    public String getNombre() { return nombre; }
    public String getEntidadOrganizadora() { return entidadOrganizadora; }
    public String getTemporada() { return temporada; }
    public String getDeporte() { return deporte; }
    public String getCategoriaHabilitada() { return categoriaHabilitada; }
    public String getFormatoJuego() { return formatoJuego; }
    public List<Equipo> getEquiposInscritos() { return equiposInscritos; }
    public List<InscripcionEquipo> getNominaCompetencia() { return nominaCompetencia; }
}