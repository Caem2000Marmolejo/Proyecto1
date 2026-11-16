package ventas;

public class BonoDescuento {
    private double valor;
    private boolean usado;

    public BonoDescuento(double valor) {
        this.valor = valor;
        this.usado = false;
    }

    public double getValor() { return valor; }
    public boolean isUsado() { return usado; }
    
    public void usar() {
        this.usado = true;
    }
}