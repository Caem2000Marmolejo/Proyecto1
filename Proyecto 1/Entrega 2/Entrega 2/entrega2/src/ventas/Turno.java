package ventas;

public class Turno {
    private String dia;
    private String hora;

    public Turno(String dia, String hora) {
        this.dia = dia;
        this.hora = hora;
    }

    public String getDia() { return dia; }
    public String getHora() { return hora; }

    public boolean mismoTurno(Turno otro) {
        return this.dia.equalsIgnoreCase(otro.getDia()) && this.hora.equalsIgnoreCase(otro.getHora());
    }
}