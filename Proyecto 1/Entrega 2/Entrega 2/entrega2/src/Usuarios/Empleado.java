package Usuarios;

import java.util.ArrayList;
import java.util.List;

import ventas.Turno;

public abstract class Empleado extends Usuario {
    protected String codigoDescuento;
    protected List<Turno> asignados = new ArrayList<>();

    public Empleado(String login, String password, String codigoDescuento) {
        super(login, password);
        this.codigoDescuento = codigoDescuento;
    }

    public boolean validarCodigo(String codigo) {
        return this.codigoDescuento.equals(codigo);
    }

    public String getCodigoDescuento() { return codigoDescuento; }
    public List<Turno> getAsignados() { return asignados; }
}

