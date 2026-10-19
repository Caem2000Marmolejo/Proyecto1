package Usuarios;

import java.util.ArrayList;
import java.util.List;

import deportes.Equipo;
import deportes.Partido;
import deportes.SesionEntrenamiento;
import instalaciones.Instalacion;
import instalaciones.Reserva;
import ventas.ArticuloTienda;
import ventas.Snack;
import ventas.SugerenciaEmpleado;
import ventas.Venta;

public class Administrador extends Usuario {

    public Administrador(String login, String password) {
        super(login, password);
    }

    public void verInventario(List<Instalacion> instalaciones, List<ArticuloTienda> articulos) {
        System.out.println("Inventario de instalaciones:");
        for (Instalacion i : instalaciones) {
            System.out.println("  " + i.getDisciplina() + " - Capacidad: " + i.getCapacidadDefinida()
                    + " - Techada: " + i.isTechada());
        }
        System.out.println("Inventario de articulos:");
        for (ArticuloTienda a : articulos) {
            System.out.println("  " + a.getNombre() + " - Categoria: " + a.getCategoria()
                    + " - Precio: " + a.getPrecio() + " - Stock: " + a.getCantidadInventario());
        }
    }

    public void verHistorialReservas(List<Reserva> reservas) {
        System.out.println("Historial de reservas:");
        for (Reserva r : reservas) {
            System.out.println("  " + r.getBloqueHorario() + " - " + r.getInstalacion().getDisciplina()
                    + " - Participantes: " + r.getParticipantes().size());
        }
    }

    public void verHistorialPartidos(List<Partido> partidos) {
        System.out.println("Historial de partidos:");
        for (Partido p : partidos) {
            System.out.println("  Fecha: " + p.getFecha() + " - Marcador: " + p.getMarcadorFinal());
        }
    }

    public void verHistorialSesiones(List<SesionEntrenamiento> sesiones) {
        System.out.println("Historial de sesiones:");
        for (SesionEntrenamiento s : sesiones) {
            System.out.println("  " + s.getFecha() + " " + s.getHora() + " - Entrenador: "
                    + s.getPlanea().getLogin() + " - Convocados: " + s.getListaJugadoresConvocados().size());
        }
    }

    public boolean crearEquipo(List<Equipo> equipos, Equipo nuevo) {
        for (Equipo e : equipos) {
            if (e.getNombre().equalsIgnoreCase(nuevo.getNombre())) {
                return false;
            }
        }
        equipos.add(nuevo);
        return true;
    }

    public boolean darDeBajaEquipo(List<Equipo> equipos, String nombreEquipo) {
        return equipos.removeIf(e -> e.getNombre().equalsIgnoreCase(nombreEquipo));
    }

    public boolean crearJugador(List<Socio> socios, Socio nuevo) {
        for (Socio s : socios) {
            if (s.getLogin().equalsIgnoreCase(nuevo.getLogin())) {
                return false;
            }
        }
        socios.add(nuevo);
        return true;
    }

    public boolean darDeBajaJugador(List<Socio> socios, List<Equipo> equipos, String login) {
        for (Equipo e : equipos) {
            e.getNominaJugadores().removeIf(ins -> ins.getSocio().getLogin().equalsIgnoreCase(login));
        }
        return socios.removeIf(s -> s.getLogin().equalsIgnoreCase(login));
    }

    public void moverArticuloUbicacion(ArticuloTienda art, String nuevaUbicacion) {
        art.setUbicacionInventario(nuevaUbicacion);
    }

    public void reabastecerTienda(ArticuloTienda art, int cantidad) {
        art.setCantidadInventario(art.getCantidadInventario() + cantidad);
    }

    public void aprobarSugerencia(SugerenciaEmpleado sug, List<Object> productos) {
        sug.setEstado("APROBADA");
        sug.setRevisaAprobacion(this);
        String desc = sug.getDescripcion();
        if (desc.toLowerCase().contains("snack")) {
            List<String> alergenos = new ArrayList<>();
            alergenos.add("Gluten");
            productos.add(new Snack(2000, alergenos));
        } else {
            productos.add(new ArticuloTienda(desc, "Nuevo", 10000, 5, "Bodega Principal"));
        }
        System.out.println("Sugerencia aprobada: " + desc);
    }

    public void verReporteVentas(List<Venta> ventas, String rubro) {
        double total = 0;
        for (Venta v : ventas) {
            if (v.getDiscriminacionRubros() != null && v.getDiscriminacionRubros().toLowerCase().contains(rubro.toLowerCase())) {
                total += v.calcularTotal(-1);
            }
        }
        System.out.println("Reporte de ventas - Rubro: " + rubro + " - Total: " + total);
    }

    public void verReporteDeportivo(List<Socio> socios, List<SesionEntrenamiento> sesiones) {
        System.out.println("Reporte deportivo:");
        for (Socio s : socios) {
            System.out.println("  " + s.getLogin() + " - Ranking Tenis: " + s.getRanking("Tenis")
                    + " - Ranking Padel: " + s.getRanking("Padel"));
        }
        int totalConvocados = 0;
        for (SesionEntrenamiento ses : sesiones) {
            totalConvocados += ses.getListaJugadoresConvocados().size();
        }
        System.out.println("  Total convocados a sesiones: " + totalConvocados);
    }

    public boolean puedeInscribirse() {
        return false;
    }

    public boolean puedeReservar() {
        return false;
    }
}
