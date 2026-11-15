package Usuarios;

public class JugadorInvitado extends Usuario {

    private final String nombre;
    private final TipoJugadorInvitado tipo;
    private final boolean matriculaActiva;

    public JugadorInvitado(
            String login,
            String password,
            String nombre,
            TipoJugadorInvitado tipo,
            boolean matriculaActiva) {

        super(validarTexto(login, "login"), validarClave(password));

        this.nombre = validarTexto(nombre, "nombre");

        if (tipo == null) {
            throw new IllegalArgumentException(
                "El tipo de jugador es obligatorio."
            );
        }

        if (tipo == TipoJugadorInvitado.EXTERNO && matriculaActiva) {
            throw new IllegalArgumentException(
                "Un jugador externo no tiene matricula universitaria activa."
            );
        }

        this.tipo = tipo;
        this.matriculaActiva = matriculaActiva;
    }

    private static String validarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                "Debe indicar " + campo + "."
            );
        }

        return texto.trim();
    }

    private static String validarClave(String clave) {
        if (clave == null || clave.isBlank()) {
            throw new IllegalArgumentException(
                "La clave es obligatoria."
            );
        }

        return clave;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoJugadorInvitado getTipo() {
        return tipo;
    }

    public boolean isMatriculaActiva() {
        return matriculaActiva;
    }

    public boolean puedeParticipar() {
        return tipo == TipoJugadorInvitado.EXTERNO || matriculaActiva;
    }
}