package ventas;

public class ArticuloTienda {
    private String nombre;
    private String categoria;
    private double precio;
    private int cantidadInventario;
    private String ubicacionInventario;
    public ArticuloTienda(String nombre, String categoria, double precio, int cantidadInventario, String ubicacionInventario) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidadInventario = cantidadInventario;
        this.ubicacionInventario = ubicacionInventario;}
    public double getPrecio() { 
    	return precio; }
    public void setUbicacionInventario(String ubi) { 
    	this.ubicacionInventario = ubi; }
    public void setCantidadInventario(int cant) { 
    	this.cantidadInventario = cant; }
    public int getCantidadInventario() { 
    	return cantidadInventario; }
}
