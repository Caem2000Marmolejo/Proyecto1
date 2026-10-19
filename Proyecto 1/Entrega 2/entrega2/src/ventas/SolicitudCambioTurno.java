package ventas;

import Usuarios.Empleado;

public class SolicitudCambioTurno {
    private String estadoAprobacion;
    private Empleado solicita;
    
    public void setEstadoAprobacion(String estado) { 
        this.estadoAprobacion = estado; 
    }
    public String getEstadoAprobacion() { 
        return estadoAprobacion; 
    }
}