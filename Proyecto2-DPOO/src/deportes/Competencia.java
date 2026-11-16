package deportes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Usuarios.Socio;
import estadisticas.EstadisticaFutbol;
import estadisticas.EstadisticaIndividual;

public class Competencia {
    private String nombre;
    private String entidadOrganizadora;
    private String temporada;
    private String deporte;
    private String categoriaHabilitada;
    private String formatoJuego;
    private int nominaMinimaRequerida;
    
    private List<Equipo> equiposInscritos = new ArrayList<>();
    private List<Socio> nominaCompetencia = new ArrayList<>();
    
    private Map<String, Integer> tarjetasAmarillasTorneo = new HashMap<>();
    private Set<String> suspendidosTorneo = new HashSet<>();
    
    private List<PartidoCompetencia> historialPartidos = new ArrayList<>();
    private List<EstadisticaIndividual> estadisticasAcumuladas = new ArrayList<>();

    public void registrarPartidoFinalizado(PartidoCompetencia partido) {
        this.historialPartidos.add(partido);
        this.estadisticasAcumuladas.addAll(partido.getRegistra());
        procesarFinalizacionPartido(partido.getRegistra());
    }

    public void mostrarGoleadorTorneo() {
        java.util.Map<String, Integer> golesPorJugador = new java.util.HashMap<>();
        
        for (EstadisticaIndividual est : estadisticasAcumuladas) {
            if (est instanceof EstadisticaFutbol) {
                EstadisticaFutbol ef = (EstadisticaFutbol) est;
                String login = ef.getSocio().getLogin();
                golesPorJugador.put(login, golesPorJugador.getOrDefault(login, 0) + ef.getGoles());
            }
        }

        String goleador = null;
        int maxGoles = -1;
        
        for (java.util.Map.Entry<String, Integer> entry : golesPorJugador.entrySet()) {
            if (entry.getValue() > maxGoles) {
                maxGoles = entry.getValue();
                goleador = entry.getKey();
            }
        }

        if (goleador != null) {
            System.out.println("Goleador del torneo " + nombre + ": " + goleador + " con " + maxGoles + " goles.");
        } else {
            System.out.println("Aún no hay goles registrados en el torneo.");
        }
    }

    public List<PartidoCompetencia> getHistorialPartidos() { return historialPartidos; }

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
        if (equipo == null) return false;
        
        if (!equipo.getDeporte().equalsIgnoreCase(deporte)) {
            System.out.println("El equipo no pertenece al deporte de la competencia.");
            return false;
        }
        if (!equipo.getCategoriaEdad().equalsIgnoreCase(categoriaHabilitada)) {
            System.out.println("El equipo no pertenece a la categoría habilitada.");
            return false;
        }
        if (equipo.contarJugadoresHabilitados() < nominaMinimaRequerida) {
            System.out.println("El equipo no cumple con la nómina mínima requerida.");
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
                System.out.println("El equipo ya está inscrito en esta competencia.");
                return false;
            }
        }
        
        equiposInscritos.add(equipo);
        
        for (InscripcionEquipo ins : equipo.getNominaJugadores()) {
            if (!ins.isSuspendido()) {
                nominaCompetencia.add(ins.getSocio());
            }
        }
        System.out.println("Equipo " + equipo.getNombre() + " inscrito exitosamente en " + this.nombre);
        return true;
    }

    public boolean jugadorPuedeJugar(String loginJugador) {
        boolean estaEnNomina = false;
        for (Socio s : nominaCompetencia) {
            if (s.getLogin().equals(loginJugador)) {
                estaEnNomina = true;
                break;
            }
        }
        return estaEnNomina && !suspendidosTorneo.contains(loginJugador);
    }

    public void procesarFinalizacionPartido(List<EstadisticaIndividual> estadisticas) {
        suspendidosTorneo.clear();

        for (EstadisticaIndividual est : estadisticas) {
            if (est instanceof EstadisticaFutbol) {
                EstadisticaFutbol ef = (EstadisticaFutbol) est;
                String login = ef.getSocio().getLogin();

                if (ef.getRojas() > 0) {
                    suspendidosTorneo.add(login);
                }

                if (ef.getAmarillas() > 0) {
                    int acumuladas = tarjetasAmarillasTorneo.getOrDefault(login, 0) + ef.getAmarillas();
                    
                    if (acumuladas >= 2) {
                        suspendidosTorneo.add(login);
                        tarjetasAmarillasTorneo.put(login, 0);
                    } else {
                        tarjetasAmarillasTorneo.put(login, acumuladas);
                    }
                }
            }
        }
    }

    public String getNombre() { return nombre; }
    public String getEntidadOrganizadora() { return entidadOrganizadora; }
    public String getTemporada() { return temporada; }
    public String getDeporte() { return deporte; }
    public String getCategoriaHabilitada() { return categoriaHabilitada; }
    public String getFormatoJuego() { return formatoJuego; }
    public int getNominaMinimaRequerida() { return nominaMinimaRequerida; }
    public List<Equipo> getEquiposInscritos() { return equiposInscritos; }
    public List<Socio> getNominaCompetencia() { return nominaCompetencia; }
    public Set<String> getSuspendidosTorneo() { return suspendidosTorneo; }
    public Map<String, Integer> getTarjetasAmarillasTorneo() { return tarjetasAmarillasTorneo; }
}