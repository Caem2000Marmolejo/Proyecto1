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

    private Socio realizaCompra;
    private Empleado realizaCompraDescuento;

    private List<ArticuloTienda> incluyeArticulos = new ArrayList<>();
    private List<Bebida> incluyeBebidas = new ArrayList<>();
    private List<Snack> incluyeSnacks = new ArrayList<>();


    public List<String> obtenerAvisoAlergenos() {
        List<String> avisos = new ArrayList<>();
        
        for (int i = 0; i < incluyeSnacks.size(); i++) {
            Snack snackActual = incluyeSnacks.get(i);
            
            if (snackActual.getAlergenos().size() > 0) {
                String mensaje = " El snack contiene: ";
                
                for (int j = 0; j < snackActual.getAlergenos().size(); j++) {
                    mensaje = mensaje + snackActual.getAlergenos().get(j) + " ";
                }
                
                avisos.add(mensaje);
            }
        }
        return avisos;
    }


    public boolean verificarRestriccionSeguridadBebidas(List<Reserva> reservasActivasDelSocio) {
        
        boolean tieneCaliente = false;
        for (int i = 0; i < incluyeBebidas.size(); i++) {
            Bebida bebidaActual = incluyeBebidas.get(i);
            if (bebidaActual.isEsCaliente() == true) {
                tieneCaliente = true;
            }
        }
        
        

        if (tieneCaliente == false || realizaCompra == null) {
            return true; 
        }

        for (int i = 0; i < reservasActivasDelSocio.size(); i++) {
            Reserva reservaActual = reservasActivasDelSocio.get(i);
            
            if (reservaActual.getParticipantes().contains(realizaCompra)) {
                
                Instalacion lugar = reservaActual.getInstalacion();
                String deporte = lugar.getDisciplina();
                boolean esTechada = lugar.isTechada();

                if (deporte.equalsIgnoreCase("Padel")) {
                    return false; 
                }
                if (deporte.equalsIgnoreCase("Tenis de Mesa") && esTechada == true) {
                    return false; 
                }
            }
        }
        return true; 
    }

  
    public double calcularTotal(boolean usaCodigoDescuentoEmpleado, double propinaModificada) {
        
        double subtotalTienda = 0;
        double subtotalCafeteria = 0;

        for (int i = 0; i < incluyeArticulos.size(); i++) {
            subtotalTienda = subtotalTienda + incluyeArticulos.get(i).getPrecio();
        }

        for (int i = 0; i < incluyeBebidas.size(); i++) {
            subtotalCafeteria = subtotalCafeteria + incluyeBebidas.get(i).getPrecio();
        }
        for (int i = 0; i < incluyeSnacks.size(); i++) {
            subtotalCafeteria = subtotalCafeteria + incluyeSnacks.get(i).getPrecio();
        }

        double porcentajeDescuento = 0.0;
        
        if (realizaCompraDescuento != null) {
            porcentajeDescuento = 0.15; 
        } else if (usaCodigoDescuentoEmpleado == true) {
            porcentajeDescuento = 0.08; 
        } else {
            porcentajeDescuento = 0.0;  
        }

        subtotalTienda = subtotalTienda - (subtotalTienda * porcentajeDescuento);
        subtotalCafeteria = subtotalCafeteria - (subtotalCafeteria * porcentajeDescuento);

        this.ivaDiecinueve = subtotalTienda * 0.19;
        this.impConsumoOcho = subtotalCafeteria * 0.08;
        
        if (propinaModificada >= 0) {
            this.propina = propinaModificada; 
        } else {
            this.propina = (subtotalCafeteria + this.impConsumoOcho) * 0.10; 
        }

        double totalFinal = subtotalTienda + this.ivaDiecinueve + subtotalCafeteria + this.impConsumoOcho + this.propina;

        if (realizaCompra != null) {
            realizaCompra.sumarPuntosFidelidad(totalFinal);
        }

        return totalFinal;
    }
    public void setRealizaCompra(Socio realizaCompra) { 
    	this.realizaCompra = realizaCompra; }
    public void setRealizaCompraDescuento(Empleado realizaCompraDescuento) { 
    	this.realizaCompraDescuento = realizaCompraDescuento; }
    public void agregarArticulo(ArticuloTienda a) { 
    	this.incluyeArticulos.add(a); }
    public void agregarBebida(Bebida b) { 
    	this.incluyeBebidas.add(b); }
    public void agregarSnack(Snack s) { 
    	this.incluyeSnacks.add(s); }
}