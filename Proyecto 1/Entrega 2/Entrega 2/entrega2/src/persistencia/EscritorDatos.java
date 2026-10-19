package persistencia;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import Usuarios.Socio;
import deportes.Equipo;
import deportes.InscripcionEquipo;

public class EscritorDatos {

    public static void guardarSocios(List<Socio> socios, String ruta) {
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

    public static void guardarEquipos(List<Equipo> equipos, String ruta) {
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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            for (Equipo e : equipos) {
                for (InscripcionEquipo ins : e.getNominaJugadores()) {
                    bw.write(e.getNombre() + ";" + ins.getSocio().getLogin() + ";"
                            + ins.getNumeroCamiseta() + ";" + ins.isSuspendido() + ";"
                            + ins.getPosicion());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error guardando nominas: " + e.getMessage());
        }
    }

    public static void agregarLineaVenta(String linea, String ruta) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, true))) {
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error guardando venta: " + e.getMessage());
        }
    }

    public static void agregarLineaReserva(String linea, String ruta) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, true))) {
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error guardando reserva: " + e.getMessage());
        }
    }

    public static void agregarLineaSesion(String linea, String ruta) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, true))) {
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error guardando sesion: " + e.getMessage());
        }
    }

    public static void agregarLineaAsistencia(String linea, String ruta) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, true))) {
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error guardando asistencia: " + e.getMessage());
        }
    }
    public static void agregarLineaSugerencia(String linea, String ruta) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, true))) {
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error guardando sugerencia: " + e.getMessage());
        }
    }

    public static void agregarLineaTurno(String linea, String ruta) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, true))) {
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error guardando turno: " + e.getMessage());
        }
    }
}