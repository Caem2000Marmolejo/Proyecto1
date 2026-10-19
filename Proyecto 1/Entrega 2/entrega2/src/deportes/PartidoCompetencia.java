package deportes;

public class PartidoCompetencia extends Partido {
    private String institucionRival;
    private String escenario;
    private String fase;
    private Equipo equipoOficial;

    public int calcularPuntos(boolean victoria, boolean empate) {
        if (equipoOficial.getDeporte().equalsIgnoreCase("futbol")) {
            if (victoria) return 3;
            if (empate) return 1;
            return 0;
        } else {
            return 0; 
        }
    }
}