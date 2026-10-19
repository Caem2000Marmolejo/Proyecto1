package Usuarios;

import ventas.ArticuloTienda;
import ventas.SolicitudCambioTurno;
import ventas.SugerenciaEmpleado;

public class Administrador extends Usuario{
	public Administrador(String login, String password) { 
        super(login, password); 
    }

    public void crearEquipo() { }
    public void darDeBajaEquipo() { }
    public void crearJugador() { }
    public void darDeBajaJugador() { }
    
    public void moverArticuloUbicacion(ArticuloTienda art, String nuevaUbicacion) {
        art.setUbicacionInventario(nuevaUbicacion);
    }
    public void reabastecerTienda(ArticuloTienda art, int cantidad) {
        art.setCantidadInventario(art.getCantidadInventario() + cantidad);
    }
    public void aprobarSugerencia(SugerenciaEmpleado sug) { }
    public void verReporteVentas(String rubro, String periodo) { }
    public void verReporteDeportivo() { }

    public boolean apruebaSiCumpleMinimo(SolicitudCambioTurno solicitud, int totalEntrenadores, int totalFisios) {
        if (totalEntrenadores >= 2 && totalFisios >= 1) {
            solicitud.setEstadoAprobacion("APROBADO");
            return true;
        }
        solicitud.setEstadoAprobacion("RECHAZADO");
        return false;
    }
}
