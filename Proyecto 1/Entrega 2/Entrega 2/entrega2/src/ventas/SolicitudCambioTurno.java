package ventas;

import Usuarios.Empleado;

public class SolicitudCambioTurno {
    private String estadoAprobacion;
    private Empleado solicita;
    private Turno turnoActual;
    private Turno turnoNuevo;

    public SolicitudCambioTurno(Empleado solicita, Turno turnoActual, Turno turnoNuevo) {
        this.solicita = solicita;
        this.turnoActual = turnoActual;
        this.turnoNuevo = turnoNuevo;
        this.estadoAprobacion = "PENDIENTE";
    }

    public void setEstadoAprobacion(String estado) { this.estadoAprobacion = estado; }
    public String getEstadoAprobacion() { return estadoAprobacion; }
    public Empleado getSolicita() { return solicita; }
    public Turno getTurnoActual() { return turnoActual; }
    public Turno getTurnoNuevo() { return turnoNuevo; }
}