package ventas;

import java.util.ArrayList;
import java.util.List;

public class Snack {
    private double precio;
    private List<String> alergenos = new ArrayList<>();
    public Snack(double precio, List<String> alergenos) {
        this.precio = precio;
        this.alergenos = alergenos;}
    public double getPrecio() { 
    	return precio; 
    	}
    public List<String> getAlergenos() { 
    	return alergenos; 
    	}
}