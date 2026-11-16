package ventas;

import java.util.ArrayList;
import java.util.List;

public class Snack {
    private String nombre;
    private double precio;
    private List<String> alergenos = new ArrayList<>();

    public Snack(String nombre, double precio, List<String> alergenos) {
        this.nombre = nombre;
        this.precio = precio;
        this.alergenos = alergenos;
    }

    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public List<String> getAlergenos() { return alergenos; }
}