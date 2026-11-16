 package view;

import Usuarios.*;
import deportes.*;
import instalaciones.*;

public class MenuJugador {
    private final ContextoConsola c;
    private final EntradaConsola e;
    private final SeleccionConsola s;

    public MenuJugador(ContextoConsola c, EntradaConsola e) {
        this.c = c;
        this.e = e;
        s = new SeleccionConsola(c, e);
    }

    public void mostrar(Usuario usuario) {
        while (true) {
            int op = e.menu(
                "Jugador: " + usuario.getLogin(),
                new String[]{
                    "Torneos",
                    "Comprar",
                    "Consultar perfil",
                    "Reservar instalacion",
                    "Mis reservas",
                    "Inscribirme en equipo",
                    "Salir de equipo",
                    "Mis entrenamientos",
                    "Registrar partido de practica",
                    "Competencias externas"
                }
            );

            if (op == 0) {
                return;
            }

            try {
                c.usuarios.exigirAutenticado(usuario);

                if (op == 1) {
                    new MenuTorneos(c, e).mostrar(usuario);
                }

                if (op == 2) {
                    new MenuCompras(c, e).comprar(usuario);
                }

                if (op == 10) {
                    new MenuCompetencias(c, e).mostrar(usuario);
                    continue;
                }

                if (op >= 3) {
                    if (!(usuario instanceof Socio)) {
                        throw new IllegalStateException(
                            "Esta operacion requiere una cuenta de socio."
                        );
                    }

                    Socio socio = (Socio) usuario;

                    if (op == 3) {
                        System.out.println(
                            "Edad: " + socio.getEdad()
                            + " | Categoria: " + socio.getCategoria()
                            + " | Nivel: " + socio.getNivelRaqueta()
                        );

                        System.out.println(
                            "Puntos: " + socio.getPuntosFidelidad()
                            + " | Ranking tenis: " + socio.getRanking("Tenis")
                        );

                        System.out.println(
                            socio.obtenerBonoDisponible() == null
                            ? "Sin bono disponible."
                            : "Bono disponible: $"
                                + socio.obtenerBonoDisponible().getValor()
                        );
                    }

                    if (op == 4) {
                        reservar(socio);
                    }

                    if (op == 5) {
                        for (Reserva reserva : c.reservas.getReservas()) {
                            if (reserva.getParticipantes().contains(socio)) {
                                System.out.println(
                                    reserva.getBloqueHorario()
                                    + " - " + reserva.getInstalacion().getId()
                                );
                            }
                        }
                    }

                    if (op == 6) {
                        Equipo equipo = s.equipo();

                        InscripcionEquipo inscripcion = new InscripcionEquipo(
                            socio,
                            e.entero("Numero de camiseta", 0, 999)
                        );

                        inscripcion.setPosicion(e.texto("Posicion"));

                        System.out.println(
                            c.gestorEquipos.inscribirJugador(equipo, inscripcion)
                            ? "Inscripcion registrada."
                            : "La logica rechazo la inscripcion."
                        );
                    }

                    if (op == 7) {
                        Equipo equipo = s.equipo();

                        if (e.confirmar("Salir de " + equipo.getNombre())) {
                            System.out.println(
                                equipo.retirarJugador(socio.getLogin())
                                ? "Retiro registrado."
                                : "No pertenece al equipo."
                            );
                        }
                    }

                    if (op == 8) {
                        for (SesionEntrenamiento sesion : c.sesiones) {
                            if (sesion.estaConvocado(socio)) {
                                System.out.println(
                                    sesion.getFecha() + " " + sesion.getHora()
                                    + " - " + sesion.getPlanea().getLogin()
                                );
                            }
                        }
                    }

                    if (op == 9) {
                        Socio rival = s.socio();

                        if (rival == socio) {
                            throw new IllegalArgumentException(
                                "Seleccione otro jugador."
                            );
                        }

                        PartidoPractica partido = new PartidoPractica(
                            e.fecha(),
                            e.texto("Disciplina"),
                            socio,
                            rival
                        );

                        if (!partido.validarNivelesAdyacentes()) {
                            throw new IllegalStateException(
                                "La logica rechazo los niveles."
                            );
                        }

                        c.partidos.add(partido);
                        System.out.println("Partido de practica registrado.");
                    }
                }
            } catch (IllegalArgumentException
                    | IllegalStateException
                    | SecurityException ex) {

                System.out.println(ex.getMessage());
            }
        }
    }

    private void reservar(Socio socio) {
        Instalacion instalacion = s.instalacion();
        String bloque = e.fechaHora().toString();
        int cantidad = e.entero("Cantidad de jugadores", 1, 20);

        String modalidad = e.entero(
            "1: sencillos, 2: dobles",
            1,
            2
        ) == 1 ? "Sencillos" : "Dobles";

        if (cantidad > c.socios.size()) {
            throw new IllegalArgumentException(
                "No hay suficientes socios para completar la reserva."
            );
        }

        Reserva reserva = new Reserva(
            bloque,
            cantidad,
            modalidad,
            instalacion,
            e.confirmar("Tiene bebida caliente")
        );

        reserva.agregarParticipante(socio);

        while (reserva.getParticipantes().size() < cantidad) {
            Socio otro = s.socio();

            if (reserva.getParticipantes().contains(otro)) {
                System.out.println("Ese socio ya esta incluido.");
            } else {
                reserva.agregarParticipante(otro);
            }
        }

        System.out.println(
            c.reservas.registrarReserva(reserva)
            ? "Reserva registrada."
            : "La logica rechazo la reserva."
        );
    }
}