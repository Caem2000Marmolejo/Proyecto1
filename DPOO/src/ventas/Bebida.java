package ventas;

public class Bebida {
    private String nombre;
    private double precio;
    private boolean esCaliente;

    public Bebida(String nombre, double precio, boolean esCaliente) {
        this.nombre = nombre;
        this.precio = precio;
        this.esCaliente = esCaliente;
    }

    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public boolean isEsCaliente() { return esCaliente; }
}