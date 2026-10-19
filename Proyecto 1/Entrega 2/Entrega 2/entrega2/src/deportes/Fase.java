package deportes;

import java.util.ArrayList;
import java.util.List;

public class Fase {
    private String nombreFase;
    private String tipo;
    private List<Equipo> clasificados = new ArrayList<>();

    public Fase(String nombreFase, String tipo) {
        this.nombreFase = nombreFase;
        this.tipo = tipo;
    }

    public void clasificarEquipo(Equipo e) {
        clasificados.add(e);
    }

    public void mostrarClasificados() {
        System.out.println("Fase: " + nombreFase + " (" + tipo + ")");
        for (Equipo e : clasificados) {
            System.out.println("  Clasificado: " + e.getNombre());
        }
    }

    public String getNombreFase() { return nombreFase; }
    public String getTipo() { return tipo; }
    public List<Equipo> getClasificados() { return clasificados; }
}