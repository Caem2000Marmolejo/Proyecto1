package deportes;

import estadisticas.EstadisticaIndividual;

public class PartidoCompetencia extends Partido {
    private String institucionRival;
    private String escenario;
    private String fase;
    private Equipo equipoOficial;
    private int golesEquipoOficial;
    private int golesRival;

    public PartidoCompetencia(String fecha, String marcadorFinal, String institucionRival,
                              String escenario, String fase, Equipo equipoOficial) {
        this.fecha = fecha;
        this.marcadorFinal = marcadorFinal;
        this.institucionRival = institucionRival;
        this.escenario = escenario;
        this.fase = fase;
        this.equipoOficial = equipoOficial;
    }

    public void setMarcador(int golesOficial, int golesRival) {
        String deporte = equipoOficial.getDeporte();
        
        
        if ((deporte.equalsIgnoreCase("baloncesto") || deporte.equalsIgnoreCase("voleibol")) 
                && golesOficial == golesRival) {
            System.out.println("Error: En " + deporte + " no existen los empates.");
            return;
        }

        this.golesEquipoOficial = golesOficial;
        this.golesRival = golesRival;
        
        
        if (deporte.equalsIgnoreCase("voleibol")) {
            this.marcadorFinal = golesOficial + " sets - " + golesRival + " sets";
        } else {
            this.marcadorFinal = golesOficial + "-" + golesRival;
        }
    }

    public boolean ganoEquipoOficial() {
        return golesEquipoOficial > golesRival;
    }

    public boolean empate() {
        return golesEquipoOficial == golesRival;
    }

    public int calcularPuntos() {
        String deporte = equipoOficial.getDeporte();
        if (deporte.equalsIgnoreCase("futbol")) {
            if (ganoEquipoOficial()) return 3;
            if (empate()) return 1;
            return 0;
        } else {
            
            if (ganoEquipoOficial()) return 1; 
            return 0;
        }
    }

    
    @Override
    public void agregarEstadistica(EstadisticaIndividual e) {
        boolean pertenece = false;
        for (InscripcionEquipo ins : equipoOficial.getNominaJugadores()) {
            if (ins.getSocio().getLogin().equals(e.getSocio().getLogin())) {
                pertenece = true;
                break;
            }
        }
        if (pertenece) {
            super.agregarEstadistica(e);
        } else {
            System.out.println("El jugador no pertenece al equipo oficial.");
        }
    }

    public String getInstitucionRival() { return institucionRival; }
    public String getEscenario() { return escenario; }
    public String getFase() { return fase; }
    public Equipo getEquipoOficial() { return equipoOficial; }
    public int getGolesEquipoOficial() { return golesEquipoOficial; }
    public int getGolesRival() { return golesRival; }
}