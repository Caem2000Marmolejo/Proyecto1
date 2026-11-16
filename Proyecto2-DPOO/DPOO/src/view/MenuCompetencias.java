package view;

import Usuarios.*;
import deportes.*;
import estadisticas.*;

public class MenuCompetencias {
    private final ContextoConsola c;
    private final EntradaConsola e;
    private final SeleccionConsola s;

    public MenuCompetencias(ContextoConsola c, EntradaConsola e) {
        this.c = c;
        this.e = e;
        s = new SeleccionConsola(c, e);
    }

    public void mostrar(Usuario usuario) {
        while (true) {
            int opcion = e.menu(
                "Competencias externas",
                new String[]{
                    "Consultar",
                    "Crear",
                    "Inscribir equipo",
                    "Registrar partido y estadisticas",
                    "Consultar goleador",
                    "Consultar habilitacion"
                }
            );

            if (opcion == 0) {
                return;
            }

            try {
                c.usuarios.exigirAutenticado(usuario);

                if (opcion >= 2
                        && opcion <= 4
                        && !(usuario instanceof Administrador)
                        && !(usuario instanceof Entrenador)) {

                    throw new SecurityException(
                        "Esta operacion requiere administrador o entrenador."
                    );
                }

                if (opcion == 2) {
                    c.competencias.add(
                        new Competencia(
                            e.texto("Nombre"),
                            e.texto("Entidad organizadora"),
                            e.texto("Temporada"),
                            e.texto("Deporte"),
                            e.texto("Categoria"),
                            e.texto("Formato de juego"),
                            e.entero("Nomina minima", 1, 1000)
                        )
                    );

                    System.out.println("Competencia creada.");
                    continue;
                }

                Competencia competencia = seleccionar();

                if (opcion == 1) {
                    System.out.println(
                        competencia.getNombre()
                        + " - " + competencia.getEntidadOrganizadora()
                        + " - " + competencia.getTemporada()
                    );

                    for (Equipo equipo : competencia.getEquiposInscritos()) {
                        System.out.println("Equipo: " + equipo.getNombre());
                    }

                    for (PartidoCompetencia partido
                            : competencia.getHistorialPartidos()) {

                        System.out.println(
                            partido.getFecha()
                            + " - " + partido.getInstitucionRival()
                            + " - " + partido.getMarcadorFinal()
                        );
                    }
                }

                if (opcion == 3) {
                    competencia.inscribirEquipo(s.equipo());
                }

                if (opcion == 4) {
                    registrarPartido(competencia);
                }

                if (opcion == 5) {
                    competencia.mostrarGoleadorTorneo();
                }

                if (opcion == 6) {
                    System.out.println(
                        competencia.jugadorPuedeJugar(s.socio().getLogin())
                        ? "Jugador habilitado."
                        : "Jugador no habilitado."
                    );
                }
            } catch (IllegalArgumentException
                    | IllegalStateException
                    | SecurityException ex) {

                System.out.println(ex.getMessage());
            }
        }
    }

    private Competencia seleccionar() {
        if (c.competencias.isEmpty()) {
            throw new IllegalStateException("No hay competencias externas.");
        }

        for (int i = 0; i < c.competencias.size(); i++) {
            System.out.println(
                (i + 1) + ". " + c.competencias.get(i).getNombre()
            );
        }

        return c.competencias.get(
            e.entero("Competencia", 1, c.competencias.size()) - 1
        );
    }

    private void registrarPartido(Competencia competencia) {
        Equipo equipo = s.equipo();

        if (!competencia.getEquiposInscritos().contains(equipo)) {
            throw new IllegalArgumentException(
                "El equipo no esta inscrito."
            );
        }

        PartidoCompetencia partido = new PartidoCompetencia(
            e.fecha(),
            null,
            e.texto("Institucion rival"),
            e.texto("Escenario"),
            e.texto("Fase"),
            equipo
        );

        partido.setMarcador(
            e.entero("Marcador equipo oficial", 0, 100000),
            e.entero("Marcador rival", 0, 100000)
        );

        if (partido.getMarcadorFinal() == null) {
            throw new IllegalArgumentException(
                "La logica rechazo el marcador."
            );
        }

        while (e.confirmar("Agregar estadistica de jugador")) {
            Socio jugador = s.socio();

            if (!competencia.jugadorPuedeJugar(jugador.getLogin())) {
                System.out.println("Jugador no habilitado.");
                continue;
            }

            String deporte = equipo.getDeporte();
            EstadisticaIndividual estadistica;

            if (deporte.equalsIgnoreCase("Futbol")) {
                estadistica = new EstadisticaFutbol(
                    jugador,
                    e.entero("Goles", 0, 10000),
                    e.entero("Asistencias", 0, 10000),
                    e.entero("Amarillas", 0, 2),
                    e.entero("Rojas", 0, 1)
                );
            } else if (deporte.equalsIgnoreCase("Baloncesto")) {
                estadistica = new EstadisticaBaloncesto(
                    jugador,
                    e.entero("Puntos", 0, 10000),
                    e.entero("Rebotes", 0, 10000)
                );
            } else if (deporte.equalsIgnoreCase("Voleibol")) {
                estadistica = new EstadisticaVoleibol(
                    jugador,
                    e.entero("Aces", 0, 10000),
                    e.entero("Bloqueos", 0, 10000)
                );
            } else if (deporte.equalsIgnoreCase("Tenis de Mesa")) {
                estadistica = new EstadisticaTenisMesa(
                    jugador,
                    e.entero("Sets", 0, 100),
                    e.entero("Puntos", 0, 10000)
                );
            } else {
                estadistica = new EstadisticaTenisPadel(
                    jugador,
                    e.entero("Sets", 0, 100),
                    e.entero("Games", 0, 10000),
                    e.entero("Aces", 0, 10000),
                    e.entero("Errores no forzados", 0, 10000)
                );
            }

            partido.agregarEstadistica(estadistica);
        }

        if (!e.confirmar("Registrar definitivamente el partido")) {
            return;
        }

        competencia.registrarPartidoFinalizado(partido);
        c.partidos.add(partido);

        System.out.println(
            "Partido registrado. Puntos obtenidos: " + partido.calcularPuntos()
        );
    }
}