package Usuarios;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class GestorUsuarios {

    private final Map<String, Usuario> usuarios = new LinkedHashMap<>();

    private final Set<Usuario> sesionesActivas =
        Collections.newSetFromMap(
            new IdentityHashMap<Usuario, Boolean>()
        );

    public GestorUsuarios(
            Collection<? extends Usuario> usuariosIniciales) {

        if (usuariosIniciales == null) {
            throw new IllegalArgumentException(
                "La coleccion de usuarios es obligatoria."
            );
        }

        for (Usuario usuario : usuariosIniciales) {
            agregarUsuario(usuario);
        }
    }

    public Usuario autenticar(String login, String password) {
        validarEstadoRegistro();

        if (login == null
                || login.isBlank()
                || password == null
                || password.isBlank()) {
            return null;
        }

        Usuario usuario = usuarios.get(normalizarLogin(login));

        if (usuario == null
                || !usuario.getPassword().equals(password)) {
            return null;
        }

        sesionesActivas.add(usuario);

        return usuario;
    }

    public boolean estaAutenticado(Usuario usuario) {
        validarEstadoRegistro();

        if (usuario == null
                || usuario.getLogin() == null
                || usuario.getLogin().isBlank()) {
            return false;
        }

        Usuario registrado = usuarios.get(
            normalizarLogin(usuario.getLogin())
        );

        return registrado == usuario
            && sesionesActivas.contains(usuario);
    }

    public void exigirAutenticado(Usuario usuario) {
        if (!estaAutenticado(usuario)) {
            throw new SecurityException(
                "Debe iniciar sesion para realizar esta operacion."
            );
        }
    }

    public void exigirAdministrador(Usuario usuario) {
        exigirAutenticado(usuario);

        if (!(usuario instanceof Administrador)) {
            throw new SecurityException(
                "Esta operacion requiere un administrador."
            );
        }
    }

    public void registrarUsuario(
            Usuario solicitante,
            Usuario nuevoUsuario) {

        exigirAdministrador(solicitante);
        agregarUsuario(nuevoUsuario);
    }

    public Usuario buscarUsuario(
            Usuario solicitante,
            String login) {

        exigirAdministrador(solicitante);

        return usuarios.get(normalizarLogin(login));
    }

    public List<Usuario> getUsuarios(Usuario solicitante) {
        exigirAdministrador(solicitante);

        return Collections.unmodifiableList(
            new ArrayList<>(usuarios.values())
        );
    }

    public void cerrarSesion(Usuario usuario) {
        sesionesActivas.remove(usuario);
    }

    private void agregarUsuario(Usuario usuario) {
        validarUsuario(usuario);

        String clave = normalizarLogin(usuario.getLogin());

        if (usuarios.containsKey(clave)) {
            throw new IllegalArgumentException(
                "Ya existe un usuario con ese login."
            );
        }

        usuarios.put(clave, usuario);
    }

    private static void validarUsuario(Usuario usuario) {
        if (!(usuario instanceof Socio)
                && !(usuario instanceof Entrenador)
                && !(usuario instanceof Fisioterapeuta)
                && !(usuario instanceof Administrador)
                && !(usuario instanceof JugadorInvitado)) {
            throw new IllegalArgumentException(
                "El tipo de usuario no es valido."
            );
        }

        normalizarLogin(usuario.getLogin());

        if (usuario.getPassword() == null
                || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                "Cada usuario debe tener una clave."
            );
        }
    }

    private void validarEstadoRegistro() {
        for (Map.Entry<String, Usuario> entrada
                : usuarios.entrySet()) {

            Usuario usuario = entrada.getValue();
            String login = usuario.getLogin();

            if (login == null
                    || login.isBlank()
                    || !entrada.getKey().equals(
                        normalizarLogin(login))) {
                throw new IllegalStateException(
                    "Se modifico un login fuera del gestor. "
                    + "No cambie el login de una cuenta registrada."
                );
            }

            if (usuario.getPassword() == null
                    || usuario.getPassword().isBlank()) {
                throw new IllegalStateException(
                    "Existe una cuenta registrada sin clave valida."
                );
            }
        }
    }

    private static String normalizarLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException(
                "El login es obligatorio."
            );
        }

        return login.trim().toLowerCase(Locale.ROOT);
    }
}