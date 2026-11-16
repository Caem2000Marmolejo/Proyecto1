package ventas;

import Usuarios.Socio;
import java.time.LocalDate;

public class PagoMensualidad {
    private Socio socio;
    private double monto;
    private LocalDate fechaPago;
    private String periodo;

    public PagoMensualidad(Socio socio, double monto, String periodo) {
        this.socio = socio;
        this.monto = monto;
        this.periodo = periodo;
        this.fechaPago = LocalDate.now();
    }

    public Socio getSocio() { return socio; }
    public double getMonto() { return monto; }
    public LocalDate getFechaPago() { return fechaPago; }
    public String getPeriodo() { return periodo; }
}