package ventas;

public class Bebida {
    private double precio;
    private boolean esCaliente;
    public Bebida(double precio, boolean esCaliente) {
        this.precio = precio;
        this.esCaliente = esCaliente;}
    public double getPrecio() { return precio; }
    public boolean isEsCaliente() { return esCaliente; }
}