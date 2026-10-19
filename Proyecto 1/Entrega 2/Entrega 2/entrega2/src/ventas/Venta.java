package ventas;

import java.util.ArrayList;
import java.util.List;

import Usuarios.Empleado;
import Usuarios.Socio;
import instalaciones.Instalacion;
import instalaciones.Reserva;

public class Venta {
    private String fecha;
    private String discriminacionRubros;
    private double propina;
    private double ivaDiecinueve;
    private double impConsumoOcho;
    private double puntosRedimidos;

    private Socio realizaCompra;
    private Empleado realizaCompraDescuento;
    private String codigoUsado;

    private List<ArticuloTienda> incluyeArticulos = new ArrayList<>();
    private List<Bebida> incluyeBebidas = new ArrayList<>();
    private List<Snack> incluyeSnacks = new ArrayList<>();

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

    public void agregarBebida(Bebida b) {
        incluyeBebidas.add(b);
    }

    public void agregarSnack(Snack s) {
        incluyeSnacks.add(s);
    }

    public List<String> obtenerAvisoAlergenos() {
        List<String> avisos = new ArrayList<>();
        for (Snack snackActual : incluyeSnacks) {
            if (snackActual.getAlergenos().size() > 0) {
                String mensaje = " El snack contiene: ";
                for (String alergeno : snackActual.getAlergenos()) {
                    mensaje = mensaje + alergeno + " ";
                }
                avisos.add(mensaje);
            }
        }
        return avisos;
    }

    public boolean verificarRestriccionSeguridadBebidas(List<Reserva> reservasActivasDelSocio) {
        boolean tieneCaliente = false;
        for (Bebida bebidaActual : incluyeBebidas) {
            if (bebidaActual.isEsCaliente()) {
                tieneCaliente = true;
            }
        }

        if (!tieneCaliente || realizaCompra == null) {
            return true;
        }

        for (Reserva reservaActual : reservasActivasDelSocio) {
            if (reservaActual.getParticipantes().contains(realizaCompra)) {
                Instalacion lugar = reservaActual.getInstalacion();
                String deporte = lugar.getDisciplina();
                boolean esTechada = lugar.isTechada();

                if (deporte.equalsIgnoreCase("Padel")) {
                    return false;
                }
                if (deporte.equalsIgnoreCase("Tenis de Mesa") && esTechada) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validarCodigoEmpleado(String codigo) {
        if (realizaCompraDescuento == null) {
            return false;
        }
        if (realizaCompraDescuento.validarCodigo(codigo)) {
            this.codigoUsado = codigo;
            return true;
        }
        return false;
    }

    public double calcularTotal(double propinaModificada) {
        double subtotalTienda = 0;
        double subtotalCafeteria = 0;

        for (ArticuloTienda a : incluyeArticulos) {
            subtotalTienda += a.getPrecio();
        }
        for (Bebida b : incluyeBebidas) {
            subtotalCafeteria += b.getPrecio();
        }
        for (Snack s : incluyeSnacks) {
            subtotalCafeteria += s.getPrecio();
        }

        double porcentajeDescuento = 0.0;
        if (realizaCompraDescuento != null && codigoUsado != null) {
            porcentajeDescuento = 0.15;
        } else if (codigoUsado != null) {
            porcentajeDescuento = 0.08;
        }

        subtotalTienda = subtotalTienda - (subtotalTienda * porcentajeDescuento);
        subtotalCafeteria = subtotalCafeteria - (subtotalCafeteria * porcentajeDescuento);

        this.ivaDiecinueve = subtotalTienda * 0.19;
        this.impConsumoOcho = subtotalCafeteria * 0.08;

        double basePropina = subtotalCafeteria + this.impConsumoOcho;
        if (basePropina > 0) {
            if (propinaModificada >= 0) {
                this.propina = propinaModificada;
            } else {
                this.propina = basePropina * 0.10;
            }
        } else {
            this.propina = 0;
        }

        double total = subtotalTienda + this.ivaDiecinueve + subtotalCafeteria + this.impConsumoOcho + this.propina;
        total -= this.puntosRedimidos;
        if (total < 0) total = 0;

        for (ArticuloTienda a : incluyeArticulos) {
            a.reducirInventario(1);
        }

        if (realizaCompra != null) {
            realizaCompra.sumarPuntosFidelidad(total);
        }

        generarDiscriminacionRubros(subtotalTienda, subtotalCafeteria);
        return total;
    }

    public void generarDiscriminacionRubros(double subtotalTienda, double subtotalCafeteria) {
        String rubros = "";
        if (subtotalTienda > 0) rubros += "Tienda:" + subtotalTienda;
        if (subtotalCafeteria > 0) {
            if (!rubros.isEmpty()) rubros += "|";
            rubros += "Cafeteria:" + subtotalCafeteria;
        }
        this.discriminacionRubros = rubros;
    }

    public boolean redimirPuntos(double cantidad) {
        if (realizaCompra == null) return false;
        double redimido = realizaCompra.redimirPuntos(cantidad);
        if (redimido > 0) {
            this.puntosRedimidos = redimido;
            return true;
        }
        return false;
    }

    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getFecha() { return fecha; }
    public String getDiscriminacionRubros() { return discriminacionRubros; }
    public void setRealizaCompra(Socio s) { this.realizaCompra = s; }
    public void setRealizaCompraDescuento(Empleado e) { this.realizaCompraDescuento = e; }
    public double getPropina() { return propina; }
    public double getIvaDiecinueve() { return ivaDiecinueve; }
    public double getImpConsumoOcho() { return impConsumoOcho; }
}