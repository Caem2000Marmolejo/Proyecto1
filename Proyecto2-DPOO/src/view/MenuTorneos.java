package view;

import Usuarios.*;
import deportes.*;

public class MenuTorneos {
    private final ContextoConsola c;
    private final EntradaConsola e;
    private final SeleccionConsola s;

    public MenuTorneos(ContextoConsola c, EntradaConsola e) {
        this.c = c;
        this.e = e;
        s = new SeleccionConsola(c, e);
    }

    public void mostrar(Usuario usuario) {
        while (true) {
            int opcion = e.menu(
                "Torneos",
                new String[]{
                    "Consultar historial",
                    "Inscribirme",
                    "Retirarme"
                }
            );

            if (opcion == 0) {
                return;
            }

            try {
                c.usuarios.exigirAutenticado(usuario);
                Torneo torneo = s.torneo();

                if (opcion == 1) {
                    torneo.consultarHistorialDetallado();
                }

                if (opcion == 2) {
                    String id = e.texto("Identificador de participacion");
                    ParticipacionTorneo participacion;

                    if (torneo.getModalidad() == ModalidadTorneo.INDIVIDUAL) {
                        participacion = ParticipacionTorneo.individual(
                            id,
                            usuario
                        );
                    } else if (torneo.getModalidad() == ModalidadTorneo.PAREJAS) {
                        String nombre = e.texto("Nombre de pareja");

                        participacion = ParticipacionTorneo.pareja(
                            id,
                            nombre,
                            usuario,
                            s.jugador()
                        );
                    } else {
                        Equipo equipo = s.equipo();
                        boolean pertenece = false;

                        for (InscripcionEquipo ins : equipo.getNominaJugadores()) {
                            if (ins.getSocio() == usuario) {
                                pertenece = true;
                            }
                        }

                        if (!pertenece) {
                            throw new SecurityException(
                                "Debe pertenecer al equipo que inscribe."
                            );
                        }

                        participacion = ParticipacionTorneo.equipo(id, equipo);
                    }

                    boolean inscrito = c.torneos.inscribir(
                        torneo,
                        participacion,
                        c.turnos
                    );

                    System.out.println(
                        inscrito
                        ? "Inscripcion registrada."
                        : "La logica rechazo la inscripcion."
                    );
                }

                if (opcion == 3) {
                    ParticipacionTorneo participacion = s.participacion(torneo);

                    if (!participacion.getIntegrantes().contains(usuario)) {
                        throw new SecurityException(
                            "Solo puede retirar su participacion."
                        );
                    }

                    if (e.confirmar("Retirar la participacion completa")) {
                        System.out.println(
                            c.torneos.retirar(torneo, participacion)
                            ? "Retiro registrado."
                            : "No fue posible retirar."
                        );
                    }
                }
            } catch (IllegalArgumentException
                    | IllegalStateException
                    | SecurityException ex) {

                System.out.println(ex.getMessage());
            }
        }
    }

    public void administrar(Administrador admin) {
        while (true) {
            int opcion = e.menu(
                "Administrar torneos",
                new String[]{
                    "Crear",
                    "Consultar",
                    "Iniciar",
                    "Liberar preferenciales",
                    "Registrar encuentro de equipos",
                    "Finalizar",
                    "Otorgar premio"
                }
            );

            if (opcion == 0) {
                return;
            }

            try {
                c.usuarios.exigirAdministrador(admin);

                if (opcion == 1) {
                    String id = e.texto("Identificador");
                    String nombre = e.texto("Nombre");
                    String deporte = e.texto("Deporte");
                    String categoria = e.texto("Categoria o nivel");

                    TipoTorneo tipo = e.entero(
                        "1: recreativo, 2: competitivo",
                        1,
                        2
                    ) == 1
                        ? TipoTorneo.RECREATIVO
                        : TipoTorneo.COMPETITIVO;

                    ModalidadTorneo modalidad = ModalidadTorneo.values()[
                        e.entero(
                            "1: individual, 2: parejas, 3: equipos",
                            1,
                            3
                        ) - 1
                    ];

                    java.time.LocalDateTime fecha = e.fechaHora();
                    int cupos = e.entero("Cupos", 1, 10000);

                    double tarifa = tipo == TipoTorneo.RECREATIVO
                        ? 0
                        : e.decimal("Tarifa", 0.01);

                    c.torneos.crearTorneo(
                        admin,
                        id,
                        nombre,
                        deporte,
                        categoria,
                        tipo,
                        modalidad,
                        fecha,
                        cupos,
                        tarifa
                    );

                    System.out.println("Torneo creado.");
                    continue;
                }

                Torneo torneo = s.torneo();

                if (opcion == 2) {
                    torneo.consultarHistorialDetallado();
                }

                if (opcion == 3) {
                    System.out.println(
                        torneo.iniciarTorneo()
                        ? "Torneo iniciado."
                        : "No fue posible iniciar."
                    );
                }

                if (opcion == 4) {
                    torneo.liberarCuposPreferenciales();
                    System.out.println(
                        "Estado de cupos actualizado por la logica."
                    );
                }

                if (opcion == 5) {
                    registrarEncuentro(torneo);
                }

                if (opcion == 6) {
                    torneo.finalizarTorneo();
                    System.out.println("Estado: " + torneo.getEstado());
                }

                if (opcion == 7) {
                    if (torneo.getGanador() != null) {
                        throw new IllegalStateException(
                            "Ya existe un ganador premiado."
                        );
                    }

                    ParticipacionTorneo ganador = s.participacion(torneo);

                    if (e.confirmar("Entregar el premio a " + ganador.getNombre())) {
                        c.torneos.otorgarPremio(torneo, ganador);
                    }
                }
            } catch (IllegalArgumentException
                    | IllegalStateException
                    | SecurityException ex) {

                System.out.println(ex.getMessage());
            }
        }
    }

    private void registrarEncuentro(Torneo torneo) {
        if (torneo.getEstado() != EstadoTorneo.EN_CURSO) {
            throw new IllegalStateException(
                "Seleccione un torneo en curso."
            );
        }

        if (torneo.getModalidad() != ModalidadTorneo.EQUIPOS) {
            throw new IllegalStateException(
                "Esta version no ofrece una clase de resultado para parejas "
                + "ni marcador de PartidoPractica."
            );
        }

        System.out.println("Equipo local:");
        Equipo local = s.equipo();

        System.out.println("Equipo visitante:");
        Equipo visitante = s.equipo();

        if (local == visitante) {
            throw new IllegalArgumentException(
                "Seleccione equipos diferentes."
            );
        }

        boolean localInscrito = false;
        boolean visitanteInscrito = false;

        for (ParticipacionTorneo participacion : torneo.getInscritos()) {
            if (participacion.getNombre().equals(local.getNombre())) {
                localInscrito = true;
            }

            if (participacion.getNombre().equals(visitante.getNombre())) {
                visitanteInscrito = true;
            }
        }

        if (!localInscrito || !visitanteInscrito) {
            throw new IllegalArgumentException(
                "Los dos equipos deben estar inscritos."
            );
        }

        String fecha = e.fecha();
        int a = e.entero("Marcador local", 0, 100000);
        int b = e.entero("Marcador visitante", 0, 100000);

        PartidoInterno partido = new PartidoInterno(
            fecha,
            a + "-" + b,
            torneo.getDeporte()
        );

        partido.agregarContrincante(local);
        partido.agregarContrincante(visitante);

        if (!partido.validarParaDisputarse()) {
            throw new IllegalStateException(
                "La logica rechazo el encuentro."
            );
        }

        torneo.registrarEncuentro(partido);
        c.partidos.add(partido);

        System.out.println(
            "Encuentro registrado. La tabla requiere soporte "
            + "de actualizacion en la logica existente."
        );
    }
}