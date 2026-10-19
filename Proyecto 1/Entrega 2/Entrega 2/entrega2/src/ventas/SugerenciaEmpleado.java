package ventas;

import Usuarios.Administrador;
import Usuarios.Empleado;

public class SugerenciaEmpleado {
    private String descripcionNuevoArticuloOSnack;
    private Empleado haceSugerencia;
    private Administrador revisaAprobacion;
    private String estado;

    public SugerenciaEmpleado(String descripcion, Empleado haceSugerencia) {
        this.descripcionNuevoArticuloOSnack = descripcion;
        this.haceSugerencia = haceSugerencia;
        this.estado = "PENDIENTE";
    }

    public String getDescripcion() { return descripcionNuevoArticuloOSnack; }
    public Empleado getHaceSugerencia() { return haceSugerencia; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setRevisaAprobacion(Administrador admin) { this.revisaAprobacion = admin; }
}