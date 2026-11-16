package view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import Usuarios.*;
import instalaciones.Reserva;
import ventas.*;

public class MenuCompras {
    private final ContextoConsola c;
    private final EntradaConsola e;

    public MenuCompras(ContextoConsola c, EntradaConsola e) {
        this.c = c;
        this.e = e;
    }

    public void comprar(Usuario usuario) {
        c.usuarios.exigirAutenticado(usuario);

        if (c.productos.isEmpty()) {
            throw new IllegalStateException("No hay productos.");
        }

        Venta venta = new Venta();
        venta.setRealizaCompra(usuario);
        venta.setFecha(LocalDate.now().toString());

        List<Object> carrito = new ArrayList<>();

        while (true) {
            for (int i = 0; i < c.productos.size(); i++) {
                Object producto = c.productos.get(i);
                String nombre;
                double precio;

                if (producto instanceof ArticuloTienda) {
                    nombre = ((ArticuloTienda) producto).getNombre();
                    precio = ((ArticuloTienda) producto).getPrecio();
                } else if (producto instanceof Bebida) {
                    nombre = ((Bebida) producto).getNombre();
                    precio = ((Bebida) producto).getPrecio();
                } else {
                    nombre = ((Snack) producto).getNombre();
                    precio = ((Snack) producto).getPrecio();
                }

                System.out.println(
                    (i + 1) + ". " + nombre + " $" + precio
                );
            }

            int opcion = e.entero(
                "Producto; 0 para terminar el carrito",
                0,
                c.productos.size()
            );

            if (opcion == 0) {
                break;
            }

            Object producto = c.productos.get(opcion - 1);

            if (producto instanceof ArticuloTienda) {
                ArticuloTienda articulo = (ArticuloTienda) producto;
                int cantidad = 1;

                for (Object actual : carrito) {
                    if (actual == articulo) {
                        cantidad++;
                    }
                }

                if (!articulo.hayStock(cantidad)) {
                    System.out.println("No hay mas unidades disponibles.");
                    continue;
                }

                if (!venta.agregarArticulo(articulo)) {
                    continue;
                }
            } else if (producto instanceof Bebida) {
                venta.agregarBebida((Bebida) producto);
            } else {
                venta.agregarSnack((Snack) producto);
            }

            carrito.add(producto);
        }

        if (carrito.isEmpty()) {
            System.out.println("Compra cancelada.");
            return;
        }

        for (String aviso : venta.obtenerAvisoAlergenos()) {
            System.out.println(aviso);
        }

        List<Reserva> reservasActivas = new ArrayList<>();

        for (Reserva reserva : c.reservas.getReservas()) {
            if (reserva.getBloqueHorario().startsWith(
                    LocalDate.now().toString())) {

                reservasActivas.add(reserva);
            }
        }

        if (!venta.verificarRestriccionSeguridadBebidas(reservasActivas)) {
            throw new IllegalStateException(
                "La reserva activa impide vender la bebida caliente."
            );
        }

        if (usuario instanceof Socio) {
            int beneficio = e.entero(
                "Beneficio: 0 ninguno, 1 bono, 2 codigo empleado, 3 puntos",
                0,
                3
            );

            if (beneficio == 1 && !venta.usarBonoDescuento()) {
                throw new IllegalStateException("No hay un bono disponible.");
            }

            if (beneficio == 2) {
                String codigo = e.texto("Codigo");
                Empleado dueno = null;

                for (Empleado empleado : c.empleados) {
                    if (empleado.validarCodigo(codigo)) {
                        dueno = empleado;
                    }
                }

                if (!venta.validarCodigoEmpleado(codigo, dueno)) {
                    throw new IllegalArgumentException("Codigo incorrecto.");
                }
            }

            if (beneficio == 3) {
                double puntos = e.decimal("Puntos a redimir", 0.01);

                double propina = e.confirmar("Usar propina sugerida")
                    ? -1
                    : e.decimal("Propina", 0);

                System.out.println(
                    "La implementacion actual descuenta los puntos "
                    + "al solicitarlos."
                );

                if (!e.confirmar(
                        "Continuar con la compra y redimir esos puntos")) {
                    return;
                }

                if (!venta.redimirPuntos(puntos)) {
                    throw new IllegalArgumentException(
                        "No se pudieron redimir los puntos."
                    );
                }

                finalizar(venta, propina);
                return;
            }
        }

        double propina = e.confirmar("Usar propina sugerida")
            ? -1
            : e.decimal("Propina", 0);

        System.out.println("Total: $" + venta.calcularTotal(propina));

        if (e.confirmar("Confirmar compra")) {
            finalizar(venta, propina);
        }
    }

    private void finalizar(Venta venta, double propina) {
        double total = venta.procesarPago(propina);
        c.ventas.add(venta);

        System.out.println("Compra procesada: $" + total);
        System.out.println(venta.getDiscriminacionRubros());
    }
}