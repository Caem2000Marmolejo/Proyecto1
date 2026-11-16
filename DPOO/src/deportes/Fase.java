package deportes;

import java.util.ArrayList;
import java.util.List;

public class Fase {
    private String nombreFase;
    private String tipo;
    private TablaPosiciones tabla;
    private List<Equipo> clasificados = new ArrayList<>();

    public Fase(String nombreFase, String tipo, List<Equipo> equiposParticipantes) {
        this.nombreFase = nombreFase;
        this.tipo = tipo;
        this.tabla = new TablaPosiciones(equiposParticipantes);
    }

    public void registrarResultado(Equipo equipo, int puntos, int golesFavor, int golesContra) {
        tabla.registrarResultado(equipo, puntos, golesFavor, golesContra);
    }

    public void clasificarEquipo(Equipo e) {
        if (!clasificados.contains(e)) {
            clasificados.add(e);
        }
    }

    public void mostrarClasificados() {
        System.out.println("Clasificados de la fase: " + nombreFase + " (" + tipo + ")");
        for (Equipo e : clasificados) {
            System.out.println("  " + e.getNombre());
        }
    }

    public void mostrarTabla() {
        tabla.mostrar();
    }

    public String getNombreFase() { return nombreFase; }
    public String getTipo() { return tipo; }
    public List<Equipo> getClasificados() { return clasificados; }
    public TablaPosiciones getTabla() { return tabla; }
}