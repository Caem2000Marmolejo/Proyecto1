package view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import Usuarios.*;
import deportes.*;
import ventas.*;

public class MenuAdministrador {
    private final ContextoConsola c;
    private final EntradaConsola e;
    private final SeleccionConsola s;

    public MenuAdministrador(ContextoConsola c, EntradaConsola e) {
        this.c = c;
        this.e = e;
        s = new SeleccionConsola(c, e);
    }

    public void mostrar(Administrador admin) {
        while (true) {
            int op = e.menu(
                "Administrador",
                new String[]{
                    "Torneos",
                    "Registrar usuario",
                    "Consultar usuarios",
                    "Crear equipo",
                    "Inscribir socio en equipo",
                    "Inventario",
                    "Reabastecer articulo",
                    "Mover articulo",
                    "Crear turno",
                    "Asignar turno",
                    "Resolver cambio de turno",
                    "Revisar sugerencias",
                    "Reporte de ventas",
                    "Reporte deportivo",
                    "Registrar mensualidad",
                    "Historial de reservas",
                    "Historial de entrenamientos",
                    "Historial de partidos",
                    "Competencias externas"
                }
            );

            if (op == 0) {
                return;
            }

            try {
                c.usuarios.exigirAdministrador(admin);

                if (op == 1) {
                    new MenuTorneos(c, e).administrar(admin);
                }

                if (op == 2) {
                    registrar(admin);
                }

                if (op == 3) {
                    for (Usuario usuario : c.usuarios.getUsuarios(admin)) {
                        System.out.println(
                            usuario.getLogin()
                            + " - " + usuario.getClass().getSimpleName()
                        );
                    }
                }

                if (op == 4) {
                    Equipo equipo = new Equipo(
                        e.texto("Nombre"),
                        e.texto("Deporte"),
                        e.texto("Categoria"),
                        e.entero("Cupo maximo", 1, 1000),
                        e.entero("Minimo habilitados", 1, 1000)
                    );

                    System.out.println(
                        admin.crearEquipo(c.equipos, equipo)
                        ? "Equipo creado."
                        : "No se pudo crear."
                    );
                }

                if (op == 5) {
                    Equipo equipo = s.equipo();
                    Socio socio = s.socio();

                    InscripcionEquipo inscripcion = new InscripcionEquipo(
                        socio,
                        e.entero("Camiseta", 0, 999)
                    );

                    inscripcion.setPosicion(e.texto("Posicion"));

                    System.out.println(
                        c.gestorEquipos.inscribirJugador(equipo, inscripcion)
                        ? "Socio inscrito."
                        : "La logica rechazo la inscripcion."
                    );
                }

                if (op == 6) {
                    admin.verInventario(c.instalaciones, articulos());
                }

                if (op == 7) {
                    admin.reabastecerTienda(
                        articulo(),
                        e.entero("Unidades a agregar", 1, 1000000)
                    );
                }

                if (op == 8) {
                    admin.moverArticuloUbicacion(
                        articulo(),
                        e.texto("Nueva ubicacion")
                    );
                }

                if (op == 9) {
                    String[] dias = {
                        "MONDAY",
                        "TUESDAY",
                        "WEDNESDAY",
                        "THURSDAY",
                        "FRIDAY",
                        "SATURDAY",
                        "SUNDAY"
                    };

                    String dia = dias[
                        e.entero("Dia: 1 lunes a 7 domingo", 1, 7) - 1
                    ];

                    String hora = e.texto("Hora o franja");
                    boolean existe = false;

                    for (Turno turno : c.turnosDisponibles) {
                        if (turno.getDia().equals(dia)
                                && turno.getHora().equals(hora)) {
                            existe = true;
                        }
                    }

                    if (existe) {
                        throw new IllegalArgumentException(
                            "Ese turno ya existe."
                        );
                    }

                    c.turnosDisponibles.add(new Turno(dia, hora));
                    System.out.println("Turno creado.");
                }

                if (op == 10) {
                    Empleado empleado = s.empleado();
                    Turno turno = s.turno();

                    if (empleado.getAsignados().contains(turno)) {
                        throw new IllegalArgumentException(
                            "La asignacion ya existe."
                        );
                    }

                    c.turnos.asignarTurno(empleado, turno);
                    System.out.println("Turno asignado.");
                }

                if (op == 11) {
                    resolverSolicitud();
                }

                if (op == 12) {
                    revisarSugerencia(admin);
                }

                if (op == 13) {
                    String[] periodos = {
                        "diario",
                        "semanal",
                        "mensual"
                    };

                    String periodo = periodos[
                        e.entero(
                            "1 diario, 2 semanal, 3 mensual",
                            1,
                            3
                        ) - 1
                    ];

                    admin.verReporteVentas(
                        c.ventas,
                        e.texto("Rubro: Tienda, Cafeteria o Mensualidad"),
                        periodo,
                        LocalDate.parse(e.fecha())
                    );
                }

                if (op == 14) {
                    admin.verReporteDeportivo(c.socios, c.sesiones, c.partidos);
                }

                if (op == 15) {
                    admin.registrarPagoMensualidad(
                        s.socio(),
                        e.decimal("Monto", 0.01),
                        e.texto("Periodo"),
                        c.ventas
                    );
                }

                if (op == 16) {
                    admin.verHistorialReservas(c.reservas.getReservas());
                }

                if (op == 17) {
                    admin.verHistorialSesiones(c.sesiones);
                }

                if (op == 18) {
                    admin.verHistorialPartidos(c.partidos);
                }

                if (op == 19) {
                    new MenuCompetencias(c, e).mostrar(admin);
                }
            } catch (IllegalArgumentException
                    | IllegalStateException
                    | SecurityException ex) {

                System.out.println(ex.getMessage());
            }
        }
    }

    private void registrar(Administrador admin) {
        int tipo = e.entero(
            "1 socio, 2 entrenador, 3 fisioterapeuta, 4 estudiante, 5 externo",
            1,
            5
        );

        String login = e.texto("Login");
        String clave = e.texto("Clave");
        Usuario nuevo;

        if (tipo == 1) {
            nuevo = new Socio(
                login,
                clave,
                e.texto("Categoria"),
                e.entero("Edad", 1, 120),
                e.texto("Nivel: Principiante, Intermedio o Avanzado")
            );
        } else if (tipo == 2) {
            nuevo = new Entrenador(login, clave, "DESC-" + login);
        } else if (tipo == 3) {
            nuevo = new Fisioterapeuta(login, clave, "DESC-" + login);
        } else {
            nuevo = new JugadorInvitado(
                login,
                clave,
                e.texto("Nombre"),
                tipo == 4
                    ? TipoJugadorInvitado.ESTUDIANTE
                    : TipoJugadorInvitado.EXTERNO,
                tipo == 4 && e.confirmar("Matricula activa")
            );
        }

        c.registrar(admin, nuevo);
        System.out.println("Usuario registrado.");
    }

    private List<ArticuloTienda> articulos() {
        List<ArticuloTienda> lista = new ArrayList<>();

        for (Object producto : c.productos) {
            if (producto instanceof ArticuloTienda) {
                lista.add((ArticuloTienda) producto);
            }
        }

        return lista;
    }

    private ArticuloTienda articulo() {
        List<ArticuloTienda> lista = articulos();

        if (lista.isEmpty()) {
            throw new IllegalStateException("No hay articulos.");
        }

        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + lista.get(i).getNombre());
        }

        return lista.get(
            e.entero("Articulo", 1, lista.size()) - 1
        );
    }

    private void resolverSolicitud() {
        List<SolicitudCambioTurno> pendientes = new ArrayList<>();

        for (SolicitudCambioTurno solicitud : c.solicitudes) {
            if (solicitud.getEstadoAprobacion().equals("PENDIENTE")) {
                pendientes.add(solicitud);
            }
        }

        if (pendientes.isEmpty()) {
            throw new IllegalStateException(
                "No hay solicitudes pendientes."
            );
        }

        for (int i = 0; i < pendientes.size(); i++) {
            System.out.println(
                (i + 1) + ". " + pendientes.get(i).getSolicita().getLogin()
            );
        }

        SolicitudCambioTurno solicitud = pendientes.get(
            e.entero("Solicitud", 1, pendientes.size()) - 1
        );

        if (e.confirmar(
                "Evaluar aprobacion con las reglas de personal minimo")) {

            c.turnos.aprobarCambio(solicitud);
        } else {
            solicitud.setEstadoAprobacion("RECHAZADO");
        }

        System.out.println(solicitud.getEstadoAprobacion());
    }

    private void revisarSugerencia(Administrador admin) {
        if (c.sugerencias.isEmpty()) {
            throw new IllegalStateException("No hay sugerencias.");
        }

        for (int i = 0; i < c.sugerencias.size(); i++) {
            System.out.println(
                (i + 1) + ". " + c.sugerencias.get(i).getDescripcion()
                + " - " + c.sugerencias.get(i).getEstado()
            );
        }

        SugerenciaEmpleado sugerencia = c.sugerencias.get(
            e.entero("Sugerencia", 1, c.sugerencias.size()) - 1
        );

        if (!sugerencia.getEstado().equalsIgnoreCase("PENDIENTE")) {
            throw new IllegalStateException(
                "La sugerencia ya fue revisada."
            );
        }

        List<ArticuloTienda> articulos = articulos();
        List<Snack> snacks = new ArrayList<>();

        for (Object producto : c.productos) {
            if (producto instanceof Snack) {
                snacks.add((Snack) producto);
            }
        }

        if (e.confirmar("Aprobar sugerencia")) {
            admin.aprobarSugerencia(sugerencia, articulos, snacks);

            for (ArticuloTienda articulo : articulos) {
                if (!c.productos.contains(articulo)) {
                    c.productos.add(articulo);
                }
            }

            for (Snack snack : snacks) {
                if (!c.productos.contains(snack)) {
                    c.productos.add(snack);
                }
            }
        } else {
            sugerencia.setEstado("RECHAZADA");
            sugerencia.setRevisaAprobacion(admin);
        }

        System.out.println(sugerencia.getEstado());
    }
}