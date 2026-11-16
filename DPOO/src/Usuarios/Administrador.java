package Usuarios;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import deportes.Equipo;
import deportes.ModalidadTorneo;
import deportes.Partido;
import deportes.ParticipacionTorneo;
import deportes.SesionEntrenamiento;
import deportes.Torneo;
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
            System.out.println("  " + i.getId() + " (" + i.getDisciplina() + ") - Capacidad: " + i.getCapacidadDefinida() + " - Techada: " + i.isTechada());
        }
        System.out.println("Inventario de articulos:");
        for (ArticuloTienda a : articulos) {
            System.out.println("  " + a.getNombre() + " - Categoria: " + a.getCategoria() + " - Precio: " + a.getPrecio() + " - Stock: " + a.getCantidadInventario());
        }
    }

    public void verHistorialReservas(List<Reserva> reservas) {
        System.out.println("Historial de reservas:");
        for (Reserva r : reservas) {
            System.out.println("  " + r.getBloqueHorario() + " - " + r.getInstalacion().getDisciplina() + " - Participantes: " + r.getParticipantes().size());
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
            System.out.println("  " + s.getFecha() + " " + s.getHora() + " - Entrenador: " + s.getPlanea().getLogin() + " - Convocados: " + s.getListaJugadoresConvocados().size());
        }
    }

    public void verHistorialTorneos(List<Torneo> torneos) {
        System.out.println("=== HISTORIAL DE TORNEOS ===");
        for (Torneo t : torneos) {
            System.out.println("Torneo: " + t.getNombre() + " | Deporte: " + t.getDeporte() + " | Estado: " + t.getEstado());
            System.out.println("  Cupos: " + (t.getCuposPreferencialesOcupados() + t.getCuposRegularesOcupados()) + "/" + t.getCuposTotales());
            System.out.println("  Recaudado: $" + t.getPozoRecaudado());
            
            System.out.println("  Participantes:");
            for (ParticipacionTorneo p : t.getInscritos()) {
                System.out.println("    - " + p.getNombre());
            }
            
            System.out.println("  Encuentros jugados: " + t.getEncuentros().size());
            for (Partido p : t.getEncuentros()) {
                System.out.println("    * Fecha: " + p.getFecha() + " | Marcador: " + p.getMarcadorFinal());
            }
            
            if (t.getModalidad() == ModalidadTorneo.EQUIPOS) {
                System.out.println("  Tabla de Posiciones:");
                t.getTablaPosiciones().mostrar();
            }
            System.out.println();
        }
    }

    public boolean crearEquipo(List<Equipo> equipos, Equipo nuevo) {
        for (Equipo e : equipos) {
            if (e.getNombre().equalsIgnoreCase(nuevo.getNombre())) return false;
        }
        equipos.add(nuevo);
        return true;
    }

    public boolean darDeBajaEquipo(List<Equipo> equipos, String nombreEquipo) {
        return equipos.removeIf(e -> e.getNombre().equalsIgnoreCase(nombreEquipo));
    }

    public boolean crearJugador(List<Socio> socios, Socio nuevo) {
        for (Socio s : socios) {
            if (s.getLogin().equalsIgnoreCase(nuevo.getLogin())) return false;
        }
        socios.add(nuevo);
        return true;
    }

    public boolean darDeBajaJugador(List<Socio> socios, List<Equipo> equipos, String login) {
        for (Equipo e : equipos) {
            e.retirarJugador(login);
        }
        return socios.removeIf(s -> s.getLogin().equalsIgnoreCase(login));
    }

    public void moverArticuloUbicacion(ArticuloTienda art, String nuevaUbicacion) {
        art.setUbicacionInventario(nuevaUbicacion);
    }

    public void reabastecerTienda(ArticuloTienda art, int cantidad) {
        art.setCantidadInventario(art.getCantidadInventario() + cantidad);
    }

    public void aprobarSugerencia(SugerenciaEmpleado sug, List<ArticuloTienda> articulos, List<Snack> snacks) {
        sug.setEstado("APROBADA");
        sug.setRevisaAprobacion(this);
        
        if (sug.getDescripcion().toLowerCase().contains("snack")) {
            List<String> alergenos = new ArrayList<>();
            alergenos.add("Gluten");
            snacks.add(new Snack(sug.getDescripcion(), 2000, alergenos));
        } else {
            articulos.add(new ArticuloTienda(sug.getDescripcion(), "Nuevo", 10000, 5, "Bodega Principal"));
        }
        System.out.println("Sugerencia aprobada: " + sug.getDescripcion());
    }

    public void verReporteVentas(List<Venta> ventas, String rubro, String periodo, LocalDate fechaReferencia) {
        double total = 0;
        for (Venta v : ventas) {
            boolean coincideRubro = v.getDiscriminacionRubros() != null && v.getDiscriminacionRubros().toLowerCase().contains(rubro.toLowerCase());
            
            if (!coincideRubro) continue;

            LocalDate fechaVenta;
            try {
                fechaVenta = LocalDate.parse(v.getFecha());
            } catch (Exception e) {
                continue;
            }

            boolean coincidePeriodo = false;
            long diasDiferencia = ChronoUnit.DAYS.between(fechaVenta, fechaReferencia);

            if (periodo.equalsIgnoreCase("DIARIO") && diasDiferencia == 0) coincidePeriodo = true;
            else if (periodo.equalsIgnoreCase("SEMANAL") && diasDiferencia >= 0 && diasDiferencia < 7) coincidePeriodo = true;
            else if (periodo.equalsIgnoreCase("MENSUAL") && diasDiferencia >= 0 && diasDiferencia < 30) coincidePeriodo = true;

            if (coincidePeriodo) {
                total += v.calcularTotal(0);
            }
        }
        System.out.println("Reporte de ventas - Rubro: " + rubro + " - Periodo: " + periodo + " - Total: " + total);
    }

    public void verReporteDeportivo(List<Socio> socios, List<SesionEntrenamiento> sesiones, List<Partido> partidos) {
        System.out.println("Reporte deportivo:");
        
        System.out.println("  --- Evolucion de Rankings y Datos de Socios ---");
        for (Socio s : socios) {
            System.out.println("  " + s.getLogin() + " - Ranking Tenis: " + s.getRanking("Tenis") + " - Ranking Padel: " + s.getRanking("Padel") + " - Ranking Tenis de Mesa: " + s.getRanking("Tenis de Mesa"));
        }

        System.out.println("  --- Resultados de Partidos ---");
        for (Partido p : partidos) {
            System.out.println("  Fecha: " + p.getFecha() + " - Marcador: " + p.getMarcadorFinal());
        }

        System.out.println("  --- Asistencia a Entrenamientos ---");
        int totalConvocados = 0;
        for (SesionEntrenamiento ses : sesiones) {
            totalConvocados += ses.getListaJugadoresConvocados().size();
        }
        System.out.println("  Total convocados a sesiones: " + totalConvocados);
    }
    
    public void registrarPagoMensualidad(Socio socio, double monto, String periodo, List<Venta> ventas) {
        Venta ventaMensualidad = new Venta();
        ventaMensualidad.setRealizaCompra(socio);
        ventaMensualidad.setFecha(LocalDate.now().toString());
        
        ventaMensualidad.setDiscriminacionRubros("Mensualidades:" + monto);
        ventas.add(ventaMensualidad);
        
        System.out.println("Mensualidad de " + periodo + " registrada para " + socio.getLogin() + " por valor de $" + monto);
    }

    public boolean puedeInscribirse() { return false; }
    public boolean puedeReservar() { return false; }
}