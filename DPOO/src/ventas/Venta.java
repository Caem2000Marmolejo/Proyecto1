package ventas;

import java.util.ArrayList;
import java.util.List;

import Usuarios.Empleado;
import Usuarios.Socio;
import Usuarios.Usuario;
import instalaciones.Instalacion;
import instalaciones.Reserva;

public class Venta {
    private String fecha;
    private String discriminacionRubros;
    private double propina;
    private double ivaDiecinueve;
    private double impConsumoOcho;
    private double puntosRedimidos;

    private Usuario realizaCompra;
    private String codigoUsado;
    private BonoDescuento bonoUsado;

    private List<ArticuloTienda> incluyeArticulos = new ArrayList<>();
    private List<Bebida> incluyeBebidas = new ArrayList<>();
    private List<Snack> incluyeSnacks = new ArrayList<>();
    private boolean ventaProcesada = false;

    public Venta() {
        this.fecha = "";
        this.discriminacionRubros = "";
        this.propina = 0;
        this.ivaDiecinueve = 0;
        this.impConsumoOcho = 0;
        this.puntosRedimidos = 0;
    }

    public boolean agregarArticulo(ArticuloTienda a) {
        if (!a.hayStock(1)) {
            System.out.println("No hay stock de " + a.getNombre());
            return false;
        }
        incluyeArticulos.add(a);
        return true;
    }

    public void agregarBebida(Bebida b) { incluyeBebidas.add(b); }
    public void agregarSnack(Snack s) { incluyeSnacks.add(s); }

    public List<String> obtenerAvisoAlergenos() {
        List<String> avisos = new ArrayList<>();
        for (Snack snackActual : incluyeSnacks) {
            if (!snackActual.getAlergenos().isEmpty()) {
                String mensaje = "El snack " + snackActual.getNombre() + " contiene: " + String.join(", ", snackActual.getAlergenos());
                avisos.add(mensaje);
            }
        }
        return avisos;
    }

    public boolean verificarRestriccionSeguridadBebidas(List<Reserva> reservasActivasDelSocio) {
        boolean tieneCaliente = false;
        for (Bebida bebidaActual : incluyeBebidas) {
            if (bebidaActual.isEsCaliente()) tieneCaliente = true;
        }

        if (!tieneCaliente || !(realizaCompra instanceof Socio)) return true;

        for (Reserva reservaActual : reservasActivasDelSocio) {
            if (reservaActual.getParticipantes().contains(realizaCompra)) {
                Instalacion lugar = reservaActual.getInstalacion();
                String deporte = lugar.getDisciplina();
                boolean esTechada = lugar.isTechada();

                if (deporte.equalsIgnoreCase("Padel")) return false;
                if (deporte.equalsIgnoreCase("Tenis de Mesa") && esTechada) return false;
            }
        }
        return true;
    }

    public boolean usarBonoDescuento() {
        if (codigoUsado != null) { 
            return false; 
        }
        if (realizaCompra instanceof Socio) {
            Socio socio = (Socio) realizaCompra;
            BonoDescuento bono = socio.obtenerBonoDisponible();
            if (bono != null) {
                this.bonoUsado = bono;
                return true;
            }
        }
        return false;
    }

    public double calcularTotal(double propinaModificada) {
        double subtotalTienda = 0;
        double subtotalCafeteria = 0;

        for (ArticuloTienda a : incluyeArticulos) subtotalTienda += a.getPrecio();
        for (Bebida b : incluyeBebidas) subtotalCafeteria += b.getPrecio();
        for (Snack s : incluyeSnacks) subtotalCafeteria += s.getPrecio();

        double descuentoPorcentaje = 0.0;
        double descuentoFijo = 0.0;

        if (bonoUsado != null) {
            descuentoFijo = bonoUsado.getValor();
        } else if (realizaCompra instanceof Empleado) {
            descuentoPorcentaje = 0.15;
        } else if (realizaCompra instanceof Socio && codigoUsado != null) {
            descuentoPorcentaje = 0.08;
        }

        subtotalTienda -= (subtotalTienda * descuentoPorcentaje);
        subtotalCafeteria -= (subtotalCafeteria * descuentoPorcentaje);
        
        double baseDescuentoFijo = subtotalTienda + subtotalCafeteria;
        if (descuentoFijo > 0) {
            if (descuentoFijo > baseDescuentoFijo) descuentoFijo = baseDescuentoFijo;
            subtotalTienda -= descuentoFijo; 
            if (subtotalTienda < 0) subtotalTienda = 0;
        }

        this.ivaDiecinueve = subtotalTienda * 0.19;
        this.impConsumoOcho = subtotalCafeteria * 0.08;

        double basePropina = subtotalCafeteria + this.impConsumoOcho;
        if (basePropina > 0) {
            this.propina = (propinaModificada >= 0) ? propinaModificada : basePropina * 0.10;
        } else {
            this.propina = 0;
        }

        double total = subtotalTienda + this.ivaDiecinueve + subtotalCafeteria + this.impConsumoOcho + this.propina - this.puntosRedimidos;
        if (total < 0) total = 0;

        this.discriminacionRubros = "Tienda:" + String.format("%.2f", subtotalTienda) + "|Cafeteria:" + String.format("%.2f", subtotalCafeteria);
        return total;
    }

    public double procesarPago(double propinaModificada) {
        if (ventaProcesada) return 0;
        
        double total = calcularTotal(propinaModificada);

        for (ArticuloTienda a : incluyeArticulos) a.reducirInventario(1);
        
        if (realizaCompra instanceof Socio) {
            ((Socio) realizaCompra).sumarPuntosFidelidad(total);
        }
        
        if (bonoUsado != null) {
            bonoUsado.usar();
        }
        
        this.ventaProcesada = true;
        return total;
    }

    public boolean validarCodigoEmpleado(String codigo, Empleado empleadoDuenoDelCodigo) {
        if (empleadoDuenoDelCodigo != null && empleadoDuenoDelCodigo.validarCodigo(codigo)) {
            this.codigoUsado = codigo;
            return true;
        }
        return false;
    }

    public boolean redimirPuntos(double cantidad) {
        if (realizaCompra instanceof Socio) {
            double redimido = ((Socio) realizaCompra).redimirPuntos(cantidad);
            if (redimido > 0) {
                this.puntosRedimidos = redimido;
                return true;
            }
        }
        return false;
    }
    public void setDiscriminacionRubros(String rubros) {
        this.discriminacionRubros = rubros;
    }
    
    public Usuario getRealizaCompra() { return realizaCompra; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getFecha() { return fecha; }
    public String getDiscriminacionRubros() { return discriminacionRubros; }
    public void setRealizaCompra(Usuario u) { this.realizaCompra = u; }
    public double getPropina() { return propina; }
    public double getIvaDiecinueve() { return ivaDiecinueve; }
    public double getImpConsumoOcho() { return impConsumoOcho; }
}