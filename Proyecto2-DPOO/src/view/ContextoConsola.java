package view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Usuarios.*;
import deportes.*;
import instalaciones.*;
import persistencia.LectorDatos;
import persistencia.EscritorDatos;
import ventas.*;

public class ContextoConsola {
    final List<Socio> socios;
    final List<Empleado> empleados;
    final List<Usuario> cuentas = new ArrayList<>();
    final List<Equipo> equipos;
    final List<Instalacion> instalaciones;
    final List<Object> productos;

    final List<Venta> ventas = new ArrayList<>();
    final List<SesionEntrenamiento> sesiones = new ArrayList<>();
    final List<Competencia> competencias = new ArrayList<>();
    final List<Partido> partidos = new ArrayList<>();
    final List<SolicitudCambioTurno> solicitudes = new ArrayList<>();
    final List<SugerenciaEmpleado> sugerencias = new ArrayList<>();
    final List<Turno> turnosDisponibles = new ArrayList<>();

    final GestorUsuarios usuarios;
    final GestorTorneos torneos;
    final GestorEquipos gestorEquipos;
    final GestorTurnos turnos = new GestorTurnos();
    final GestorReservas reservas;
    final Administrador administrador;

    public ContextoConsola(EntradaConsola entrada) throws IOException {
        String[] archivos = {
            "socios.txt",
            "empleados.txt",
            "equipos.txt",
            "instalaciones.txt",
            "productos.txt"
        };

        for (String nombre : archivos) {
            File archivo = new File("data/" + nombre);

            if (!archivo.canRead()) {
                throw new IOException(
                    "No se puede leer data/" + nombre
                    + ". Ejecute desde la carpeta DPOO."
                );
            }
        }

        socios = LectorDatos.cargarSocios("data/socios.txt");
        empleados = LectorDatos.cargarEmpleados("data/empleados.txt");
        equipos = LectorDatos.cargarEquipos("data/equipos.txt");
        instalaciones = LectorDatos.cargarInstalaciones(
            "data/instalaciones.txt"
        );
        productos = LectorDatos.cargarProductos("data/productos.txt");

        cuentas.addAll(socios);
        cuentas.addAll(empleados);

        System.out.println(
            "Configure la cuenta del administrador para esta ejecucion."
        );

        String loginAdministrador;
        boolean disponible;

        do {
            loginAdministrador = entrada.texto("Login del administrador");
            disponible = true;

            if (loginAdministrador.equals("0")) {
                disponible = false;
                System.out.println("El login no puede ser 0.");
            }

            for (Usuario usuario : cuentas) {
                if (usuario.getLogin().equalsIgnoreCase(loginAdministrador)) {
                    disponible = false;
                    System.out.println("Ese login ya existe.");
                    break;
                }
            }
        } while (!disponible);

        String claveAdministrador = entrada.texto("Clave del administrador");

        administrador = new Administrador(
            loginAdministrador,
            claveAdministrador
        );

        cuentas.add(administrador);

        usuarios = new GestorUsuarios(cuentas);
        torneos = new GestorTorneos(usuarios);
        gestorEquipos = new GestorEquipos(equipos);

        int tenis = 0;
        int padel = 0;
        int mesa = 0;

        for (Instalacion instalacion : instalaciones) {
            if (instalacion.getDisciplina().equalsIgnoreCase("Tenis")) {
                tenis++;
            }

            if (instalacion.getDisciplina().equalsIgnoreCase("Padel")) {
                padel++;
            }

            if (instalacion.getDisciplina().equalsIgnoreCase("Tenis de Mesa")) {
                mesa++;
            }
        }

        reservas = new GestorReservas(tenis, padel, mesa);

        System.out.println("Cuentas y catalogos cargados.");
        System.out.println(
            "El administrador y los invitados se conservan "
            + "durante esta ejecucion."
        );
    }

    void guardar() throws IOException {
        EscritorDatos.guardarSocios(
            socios,
            "data/socios.txt"
        );

        EscritorDatos.guardarEmpleados(
            empleados,
            "data/empleados.txt"
        );

        EscritorDatos.guardarEquipos(
            equipos,
            "data/equipos.txt"
        );

        EscritorDatos.guardarInstalaciones(
            instalaciones,
            "data/instalaciones.txt"
        );

        EscritorDatos.guardarProductos(
            productos,
            "data/productos.txt"
        );

        System.out.println(
            "Se ejecutaron los guardados disponibles. "
            + "Revise cualquier error mostrado por EscritorDatos."
        );
    }

    void registrar(Usuario solicitante, Usuario nuevo) {
        usuarios.registrarUsuario(solicitante, nuevo);
        cuentas.add(nuevo);

        if (nuevo instanceof Socio) {
            socios.add((Socio) nuevo);
        }

        if (nuevo instanceof Empleado) {
            empleados.add((Empleado) nuevo);
        }
    }
}