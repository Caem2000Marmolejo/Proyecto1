package deportes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;
import Usuarios.JugadorInvitado;
import Usuarios.Socio;
import Usuarios.Usuario;

public class ParticipacionTorneo {

    private final String id;
    private final String nombre;
    private final ModalidadTorneo modalidad;
    private final List<Usuario> integrantes;
    private final String deporteEquipo;
    private final String categoriaEquipo;

    private ParticipacionTorneo(
            String id,
            String nombre,
            ModalidadTorneo modalidad,
            List<? extends Usuario> integrantes,
            String deporteEquipo,
            String categoriaEquipo) {

        this.id = validarTexto(id, "identificador");
        this.nombre = validarTexto(nombre, "nombre");

        if (modalidad == null
                || integrantes == null
                || integrantes.isEmpty()) {
            throw new IllegalArgumentException(
                "La modalidad y los integrantes son obligatorios."
            );
        }

        if (modalidad == ModalidadTorneo.INDIVIDUAL
                && integrantes.size() != 1) {
            throw new IllegalArgumentException(
                "Una participacion individual requiere un jugador."
            );
        }

        if (modalidad == ModalidadTorneo.PAREJAS
                && integrantes.size() != 2) {
            throw new IllegalArgumentException(
                "Una pareja requiere dos jugadores diferentes."
            );
        }

        if (modalidad == ModalidadTorneo.EQUIPOS
                && integrantes.size() < 2) {
            throw new IllegalArgumentException(
                "Un equipo requiere al menos dos integrantes."
            );
        }

        Set<String> identificadores = new HashSet<>();
        List<Usuario> copia = new ArrayList<>();

        for (Usuario usuario : integrantes) {
            validarJugador(usuario, modalidad);

            String login = usuario.getLogin()
                .trim()
                .toLowerCase(Locale.ROOT);

            if (!identificadores.add(login)) {
                throw new IllegalArgumentException(
                    "No puede repetirse un jugador en la participacion."
                );
            }

            copia.add(usuario);
        }

        this.modalidad = modalidad;
        this.integrantes = Collections.unmodifiableList(copia);

        if (modalidad == ModalidadTorneo.EQUIPOS) {
            this.deporteEquipo = validarTexto(
                deporteEquipo, "deporte del equipo"
            );

            this.categoriaEquipo = validarTexto(
                categoriaEquipo, "categoria del equipo"
            );
        } else {
            this.deporteEquipo = null;
            this.categoriaEquipo = null;
        }
    }

    public static ParticipacionTorneo individual(
            String id,
            Usuario jugador) {

        List<Usuario> integrantes = new ArrayList<>();
        integrantes.add(jugador);

        String nombre = jugador == null
            ? "Individual"
            : jugador.getLogin();

        return new ParticipacionTorneo(
            id,
            nombre,
            ModalidadTorneo.INDIVIDUAL,
            integrantes,
            null,
            null
        );
    }

    public static ParticipacionTorneo pareja(
            String id,
            String nombre,
            Usuario primero,
            Usuario segundo) {

        List<Usuario> integrantes = new ArrayList<>();
        integrantes.add(primero);
        integrantes.add(segundo);

        return new ParticipacionTorneo(
            id,
            nombre,
            ModalidadTorneo.PAREJAS,
            integrantes,
            null,
            null
        );
    }

    public static ParticipacionTorneo equipo(
            String id,
            Equipo equipo) {

        if (equipo == null) {
            throw new IllegalArgumentException(
                "El equipo es obligatorio."
            );
        }

        List<Usuario> jugadores = new ArrayList<>();

        for (InscripcionEquipo inscripcion
                : equipo.getNominaJugadores()) {

            if (inscripcion == null
                    || inscripcion.getSocio() == null) {
                throw new IllegalArgumentException(
                    "La nomina del equipo contiene una inscripcion invalida."
                );
            }

            jugadores.add(inscripcion.getSocio());
        }

        return equipo(
            id,
            equipo.getNombre(),
            equipo.getDeporte(),
            equipo.getCategoriaEdad(),
            jugadores
        );
    }

    public static ParticipacionTorneo equipo(
            String id,
            String nombre,
            String deporte,
            String categoria,
            List<? extends Usuario> integrantes) {

        return new ParticipacionTorneo(
            id,
            nombre,
            ModalidadTorneo.EQUIPOS,
            integrantes,
            deporte,
            categoria
        );
    }

    private static void validarJugador(
            Usuario usuario,
            ModalidadTorneo modalidad) {

        if (usuario == null
                || usuario.getLogin() == null
                || usuario.getLogin().isBlank()) {
            throw new IllegalArgumentException(
                "Cada jugador debe tener una cuenta identificable."
            );
        }

        boolean empleado =
            usuario instanceof Entrenador
            || usuario instanceof Fisioterapeuta;

        if (!(usuario instanceof Socio)
                && !(usuario instanceof JugadorInvitado)
                && !empleado) {
            throw new IllegalArgumentException(
                "Este tipo de usuario no puede participar en torneos."
            );
        }

        if (usuario instanceof JugadorInvitado
                && !((JugadorInvitado) usuario).puedeParticipar()) {
            throw new IllegalArgumentException(
                "El estudiante debe tener matricula activa."
            );
        }

        if (empleado && modalidad != ModalidadTorneo.INDIVIDUAL) {
            throw new IllegalArgumentException(
                "Los empleados solo pueden participar en modalidad individual."
            );
        }
    }

    private static String validarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                "Debe indicar " + campo + "."
            );
        }

        return texto.trim();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public ModalidadTorneo getModalidad() {
        return modalidad;
    }

    public List<Usuario> getIntegrantes() {
        return integrantes;
    }

    public String getDeporteEquipo() {
        return deporteEquipo;
    }

    public String getCategoriaEquipo() {
        return categoriaEquipo;
    }
}