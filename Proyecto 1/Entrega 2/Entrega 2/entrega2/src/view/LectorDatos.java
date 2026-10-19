package view;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Usuarios.Socio;
import Usuarios.Empleado;
import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;
import deportes.Equipo;
import instalaciones.Instalacion;
import ventas.ArticuloTienda;
import ventas.Bebida;
import ventas.Snack;

public class LectorDatos {

	public static List<Socio> cargarSocios(String ruta) {
	    List<Socio> lista = new ArrayList<>();
	    try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
	        String linea;
	        while ((linea = br.readLine()) != null) {
	            String[] datos = linea.split(";");
	            Socio s = new Socio(
	                datos[0],
	                datos[1],
	                datos[2],
	                Integer.parseInt(datos[3]),
	                datos[4]
	            );
	            if (datos.length >= 8) {
	                s.setRanking("Tenis", Integer.parseInt(datos[5]));
	                s.setRanking("Padel", Integer.parseInt(datos[6]));
	                s.setRanking("Tenis de Mesa", Integer.parseInt(datos[7]));
	            }
	            if (datos.length >= 9) {
	                s.setPuntosFidelidad(Double.parseDouble(datos[8]));
	            }
	            lista.add(s);
	        }
	    } catch (IOException e) {
	        System.out.println("Error leyendo socios: " + e.getMessage());
	    }
	    return lista;
	}
    public static List<Equipo> cargarEquipos(String ruta) {
        List<Equipo> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                Equipo e = new Equipo(
                    datos[0],
                    datos[1],
                    datos[2],
                    Integer.parseInt(datos[3]),
                    Integer.parseInt(datos[4])
                );
                lista.add(e);
            }
        } catch (IOException e) {
            System.out.println("Error leyendo equipos: " + e.getMessage());
        }
        return lista;
    }

    public static List<Empleado> cargarEmpleados(String ruta) {
        List<Empleado> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                String login = datos[0];
                String password = datos[1];
                String rol = datos[2];
                String codigo = "DESC-" + login;

                if (rol.equalsIgnoreCase("Entrenador")) {
                    lista.add(new Entrenador(login, password, codigo));
                } else if (rol.equalsIgnoreCase("Fisioterapeuta")) {
                    lista.add(new Fisioterapeuta(login, password, codigo));
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo empleados: " + e.getMessage());
        }
        return lista;
    }

    public static List<Instalacion> cargarInstalaciones(String ruta) {
        List<Instalacion> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                Instalacion i = new Instalacion(
                    datos[0],
                    Integer.parseInt(datos[1]),
                    Boolean.parseBoolean(datos[2])
                );
                lista.add(i);
            }
        } catch (IOException e) {
            System.out.println("Error leyendo instalaciones: " + e.getMessage());
        }
        return lista;
    }

    public static List<Object> cargarProductos(String ruta) {
        List<Object> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                String tipo = datos[0];

                if (tipo.equals("A")) {
                    ArticuloTienda a = new ArticuloTienda(
                        datos[1],
                        datos[2],
                        Double.parseDouble(datos[3]),
                        Integer.parseInt(datos[4]),
                        "Bodega Principal"
                    );
                    lista.add(a);
                } else if (tipo.equals("B")) {
                    boolean esCaliente = datos[4].equalsIgnoreCase("Caliente");
                    Bebida b = new Bebida(Double.parseDouble(datos[3]), esCaliente);
                    lista.add(b);
                } else if (tipo.equals("S")) {
                    List<String> alergenos = Arrays.asList(datos[4]);
                    Snack s = new Snack(Double.parseDouble(datos[3]), alergenos);
                    lista.add(s);
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo productos: " + e.getMessage());
        }
        return lista;
    }
}