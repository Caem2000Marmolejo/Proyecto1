package Usuarios;


public class Socio extends Usuario {
	private int edad;
    private double puntosFidelidad;
    private String nivelRaqueta; 
    private int rankingElo;

    public Socio(String login, String password, int edad) {
        super(login, password);
        this.edad = edad;
        this.puntosFidelidad = 0;
        this.rankingElo = 1000;
    }

    public double redimirPuntos(double cantidadARedimir) {
        if (cantidadARedimir <= puntosFidelidad) {
            puntosFidelidad -= cantidadARedimir;
            return cantidadARedimir;
        }
        return 0;
    }

    public void sumarPuntosFidelidad(double montoCompra) {
        this.puntosFidelidad += (montoCompra * 0.02);
    }

    public int getEdad() { 
    	return edad; }
    public double getPuntosFidelidad() { 
    	return puntosFidelidad; }
    public String getNivelRaqueta() { 
    	return nivelRaqueta; }
    public void setNivelRaqueta(String nivel) { 
    	this.nivelRaqueta = nivel; }
}