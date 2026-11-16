package view;

import Usuarios.*;
import deportes.*;
import instalaciones.Instalacion;
import ventas.*;

public class MenuEmpleado {
    private final ContextoConsola c;
    private final EntradaConsola e;
    private final SeleccionConsola s;

    public MenuEmpleado(ContextoConsola c, EntradaConsola e) {
        this.c = c;
        this.e = e;
        s = new SeleccionConsola(c, e);
    }

    public void mostrar(Empleado empleado) {
        while (true) {
            int op = e.menu(
                "Empleado: " + empleado.getLogin(),
                new String[]{
                    "Torneos",
                    "Comprar",
                    "Mis turnos",
                    "Solicitar cambio de turno",
                    "Estado de solicitudes",
                    "Sugerir producto",
                    "Crear entrenamiento",
                    "Convocar socio",
                    "Convocar equipo",
                    "Registrar asistencia",
                    "Consultar entrenamientos",
                    "Competencias externas"
                }
            );

            if (op == 0) {
                return;
            }

            try {
                c.usuarios.exigirAutenticado(empleado);

                if (op == 1) {
                    new MenuTorneos(c, e).mostrar(empleado);
                }

                if (op == 2) {
                    new MenuCompras(c, e).comprar(empleado);
                }

                if (op == 3) {
                    for (Turno turno : empleado.getAsignados()) {
                        System.out.println(
                            turno.getDia() + " " + turno.getHora()
                        );
                    }
                }

                if (op == 4) {
                    System.out.println("Turno actual:");
                    Turno actual = s.turno();

                    if (!empleado.getAsignados().contains(actual)) {
                        throw new SecurityException(
                            "El turno no pertenece al empleado."
                        );
                    }

                    System.out.println("Turno solicitado:");
                    Turno nuevo = s.turno();

                    if (actual == nuevo) {
                        throw new IllegalArgumentException(
                            "Los turnos deben ser diferentes."
                        );
                    }

                    SolicitudCambioTurno solicitud = new SolicitudCambioTurno(
                        empleado,
                        actual,
                        nuevo
                    );

                    c.turnos.registrarSolicitud(solicitud);
                    c.solicitudes.add(solicitud);

                    System.out.println("Solicitud pendiente de revision.");
                }

                if (op == 5) {
                    c.turnos.consultarEstadoSolicitudes(empleado);
                }

                if (op == 6) {
                    c.sugerencias.add(
                        new SugerenciaEmpleado(
                            e.texto("Descripcion"),
                            empleado
                        )
                    );

                    System.out.println("Sugerencia registrada.");
                }

                if (op == 12) {
                    new MenuCompetencias(c, e).mostrar(empleado);
                    continue;
                }

                if (op >= 7) {
                    if (!(empleado instanceof Entrenador)) {
                        throw new SecurityException(
                            "Esta operacion corresponde al entrenador."
                        );
                    }

                    if (op == 7) {
                        String fecha = e.fecha();
                        String hora = e.texto("Hora HH:MM");
                        java.time.LocalTime.parse(hora);

                        int duracion = e.entero(
                            "Duracion en minutos",
                            1,
                            1440
                        );

                        Instalacion instalacion = s.instalacion();

                        c.sesiones.add(
                            new SesionEntrenamiento(
                                fecha,
                                hora,
                                duracion,
                                (Entrenador) empleado,
                                instalacion
                            )
                        );

                        System.out.println("Entrenamiento creado.");
                    } else if (op == 11) {
                        for (SesionEntrenamiento sesion : c.sesiones) {
                            if (sesion.getPlanea() == empleado) {
                                System.out.println(
                                    sesion.getFecha() + " " + sesion.getHora()
                                    + " | Convocados: "
                                    + sesion.getListaJugadoresConvocados().size()
                                );
                            }
                        }
                    } else {
                        SesionEntrenamiento sesion = s.sesion();

                        if (sesion.getPlanea() != empleado) {
                            throw new SecurityException(
                                "Solo puede gestionar sus entrenamientos."
                            );
                        }

                        if (op == 8) {
                            System.out.println(
                                sesion.convocarJugador(s.socio())
                                ? "Convocado."
                                : "No se pudo convocar."
                            );
                        }

                        if (op == 9) {
                            System.out.println(
                                "Convocados: " + sesion.convocarEquipo(s.equipo())
                            );
                        }

                        if (op == 10) {
                            Socio socio = s.socio();

                            String[] estados = {
                                "Asistio",
                                "Falto",
                                "Llego tarde"
                            };

                            String estado = estados[
                                e.entero(
                                    "1: asistio, 2: falto, 3: llego tarde",
                                    1,
                                    3
                                ) - 1
                            ];

                            AsistenciaEvaluacion registro =
                                sesion.registrarAsistencia(
                                    socio,
                                    estado,
                                    e.entero("Nota 1 a 5; 0 sin nota", 0, 5),
                                    e.texto("Comentario")
                                );

                            System.out.println(
                                registro != null
                                ? "Asistencia registrada."
                                : "La logica rechazo el registro."
                            );
                        }
                    }
                }
            } catch (IllegalArgumentException
                    | IllegalStateException
                    | SecurityException ex) {

                System.out.println(ex.getMessage());
            } catch (java.time.format.DateTimeParseException ex) {
                System.out.println("Hora invalida.");
            }
        }
    }
}