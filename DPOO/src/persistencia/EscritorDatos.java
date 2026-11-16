package persistencia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import Usuarios.Socio;
import Usuarios.Empleado;
import Usuarios.Entrenador;
import deportes.Equipo;
import deportes.InscripcionEquipo;
import instalaciones.Instalacion;
import ventas.ArticuloTienda;
import ventas.Bebida;
import ventas.Snack;

public class EscritorDatos {

    private static void crearCarpetaSiNoExiste(String ruta) {
        File archivo = new File(ruta);
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }
    }

    public static void guardarSocios(List<Socio> socios, String ruta) {
        crearCarpetaSiNoExiste(ruta);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            for (Socio s : socios) {
                bw.write(s.getLogin() + ";" + s.getPassword() + ";" + s.getCategoria() + ";"
                        + s.getEdad() + ";" + s.getNivelRaqueta() + ";"
                        + s.getRanking("Tenis") + ";" + s.getRanking("Padel") + ";" + s.getRanking("Tenis de Mesa") + ";"
                        + s.getPuntosFidelidad());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error guardando socios: " + e.getMessage());
        }
    }

    public static void guardarEmpleados(List<Empleado> empleados, String ruta) {
        crearCarpetaSiNoExiste(ruta);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            for (Empleado e : empleados) {
                String rol = (e instanceof Entrenador) ? "Entrenador" : "Fisioterapeuta";
                bw.write(e.getLogin() + ";" + e.getPassword() + ";" + rol);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error guardando empleados: " + e.getMessage());
        }
    }

    public static void guardarEquipos(List<Equipo> equipos, String ruta) {
        crearCarpetaSiNoExiste(ruta);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            for (Equipo e : equipos) {
                bw.write(e.getNombre() + ";" + e.getDeporte() + ";" + e.getCategoriaEdad() + ";"
                        + e.getCupoMaximoJugadores() + ";" + e.getNominaMinimaHabilitados());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error guardando equipos: " + e.getMessage());
        }
    }

    public static void guardarNominas(List<Equipo> equipos, String ruta) {
        crearCarpetaSiNoExiste(ruta);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            for (Equipo e : equipos) {
                for (InscripcionEquipo ins : e.getNominaJugadores()) {
                    bw.write(e.getNombre() + ";" + ins.getSocio().getLogin() + ";"
                            + ins.getNumeroCamiseta() + ";" + ins.isSuspendido() + ";"
                            + (ins.getPosicion() != null ? ins.getPosicion() : "N/A"));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error guardando nominas: " + e.getMessage());
        }
    }

    public static void guardarInstalaciones(List<Instalacion> instalaciones, String ruta) {
        crearCarpetaSiNoExiste(ruta);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            for (Instalacion i : instalaciones) {
                bw.write(i.getId() + ";" + i.getDisciplina() + ";" + i.getCapacidadDefinida() + ";" + i.isTechada());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error guardando instalaciones: " + e.getMessage());
        }
    }

    public static void guardarProductos(List<Object> productos, String ruta) {
        crearCarpetaSiNoExiste(ruta);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            for (Object obj : productos) {
                if (obj instanceof ArticuloTienda) {
                    ArticuloTienda a = (ArticuloTienda) obj;
                    bw.write("A;" + a.getNombre() + ";" + a.getCategoria() + ";" + a.getPrecio() + ";" + a.getCantidadInventario());
                } else if (obj instanceof Bebida) {
                    Bebida b = (Bebida) obj;
                    String tipo = b.isEsCaliente() ? "Caliente" : "Fria";
                    bw.write("B;" + b.getNombre() + ";Bebida;" + b.getPrecio() + ";" + tipo);
                } else if (obj instanceof Snack) {
                    Snack s = (Snack) obj;
                    String alergenos = String.join(",", s.getAlergenos());
                    bw.write("S;" + s.getNombre() + ";Snack;" + s.getPrecio() + ";" + alergenos);
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error guardando productos: " + e.getMessage());
        }
    }

    public static void agregarLinea(String linea, String ruta) {
        crearCarpetaSiNoExiste(ruta);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, true))) {
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error guardando registro en " + ruta + ": " + e.getMessage());
        }
    }
}