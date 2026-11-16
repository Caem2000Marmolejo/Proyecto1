package view;

import java.io.IOException;
import java.util.NoSuchElementException;
import Usuarios.*;

public class AplicacionConsola {
    public static void ejecutar(int rolFijo) {
        EntradaConsola entrada = new EntradaConsola();
        ContextoConsola contexto = null;

        try {
            contexto = new ContextoConsola(entrada);
            boolean terminar = false;

            while (!terminar) {
                int rol = rolFijo;

                if (rol == 0) {
                    rol = entrada.menu(
                        "Acceso",
                        new String[]{
                            "Jugador",
                            "Empleado",
                            "Administrador"
                        }
                    );
                }

                if (rol == 0) {
                    break;
                }

                String login = entrada.texto(
                    "Login; escriba 0 para salir"
                );

                if (login.equals("0")) {
                    if (rolFijo != 0) {
                        break;
                    }

                    continue;
                }

                Usuario usuario = contexto.usuarios.autenticar(
                    login,
                    entrada.texto("Clave")
                );

                if (usuario == null) {
                    System.out.println("Credenciales incorrectas.");
                    continue;
                }

                try {
                    boolean permitido =
                        (rol == 1
                            && (usuario instanceof Socio
                                || usuario instanceof JugadorInvitado))
                        || (rol == 2 && usuario instanceof Empleado)
                        || (rol == 3 && usuario instanceof Administrador);

                    if (!permitido) {
                        System.out.println(
                            "La cuenta no corresponde a este acceso."
                        );
                        continue;
                    }

                    if (rol == 1) {
                        new MenuJugador(contexto, entrada).mostrar(usuario);
                    }

                    if (rol == 2) {
                        new MenuEmpleado(contexto, entrada).mostrar(
                            (Empleado) usuario
                        );
                    }

                    if (rol == 3) {
                        new MenuAdministrador(contexto, entrada).mostrar(
                            (Administrador) usuario
                        );
                    }
                } finally {
                    contexto.usuarios.cerrarSesion(usuario);
                }

                if (rolFijo != 0) {
                    terminar = !entrada.confirmar("Iniciar otra sesion");
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.println("Fin de entrada. Cerrando la consola.");
        } catch (IOException
                | IllegalArgumentException
                | IllegalStateException ex) {

            System.out.println(
                "No fue posible iniciar o continuar: " + ex.getMessage()
            );
        } finally {
            if (contexto != null) {
                try {
                    contexto.guardar();
                } catch (IOException ex) {
                    System.out.println(
                        "No se pudo completar el guardado: " + ex.getMessage()
                    );
                }
            }
        }
    }
}