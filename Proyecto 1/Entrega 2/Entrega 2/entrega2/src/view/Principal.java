package view;

import java.util.ArrayList;
import java.util.List;

import Usuarios.Socio;
import Usuarios.Empleado;
import Usuarios.Entrenador;
import deportes.AsistenciaEvaluacion;
import deportes.Competencia;
import deportes.Equipo;
import deportes.Fase;
import deportes.InscripcionEquipo;
import deportes.PartidoCompetencia;
import deportes.PartidoInterno;
import deportes.PartidoPractica;
import deportes.SesionEntrenamiento;
import deportes.TablaPosiciones;
import estadisticas.AcumuladorEstadisticas;
import estadisticas.AnaliticaDesempeno;
import estadisticas.EstadisticaFutbol;
import estadisticas.EstadisticaTenisPadel;
import instalaciones.GestorReservas;
import instalaciones.Instalacion;
import instalaciones.Reserva;
import persistencia.EscritorDatos;
import ventas.ArticuloTienda;
import ventas.Bebida;
import ventas.Snack;
import ventas.Venta;
import Usuarios.Administrador;
import ventas.GestorTurnos;
import ventas.SolicitudCambioTurno;
import ventas.SugerenciaEmpleado;
import ventas.Turno;

public class Principal {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA CLUB DEPORTIVO SENECA ===\n");

        List<Socio> socios = LectorDatos.cargarSocios("data/socios.txt");
        List<Equipo> equipos = LectorDatos.cargarEquipos("data/equipos.txt");
        List<Empleado> empleados = LectorDatos.cargarEmpleados("data/empleados.txt");
        List<Instalacion> instalaciones = LectorDatos.cargarInstalaciones("data/instalaciones.txt");
        List<Object> productos = LectorDatos.cargarProductos("data/productos.txt");
        List<Reserva> reservas = new ArrayList<>();
        List<Venta> ventas = new ArrayList<>();
        
        System.out.println("Socios cargados: " + socios.size());
        System.out.println("Equipos cargados: " + equipos.size());
        System.out.println("Empleados cargados: " + empleados.size());
        System.out.println("Instalaciones cargadas: " + instalaciones.size());
        System.out.println("Productos cargados: " + productos.size());
        System.out.println();

        probarInscripcionUnicaPorDeporte(socios, equipos);
        probarPartidoInterno(equipos, socios);
        probarSancionYHabilitados(equipos, socios);
        probarReserva(instalaciones, socios);
        probarVenta(productos, socios, empleados, instalaciones);
        probarCompetenciaLiga(equipos, socios);
        probarCompetenciaFases(equipos, socios);
        probarGoleador(equipos, socios);
        probarDeportesIndividuales(instalaciones, socios);
        probarSesionEntrenamiento(instalaciones, socios, equipos, empleados);
        probarAdministrador(socios, equipos, instalaciones, productos, empleados, reservas, ventas);

        EscritorDatos.guardarSocios(socios, "data/socios.txt");
        EscritorDatos.guardarEquipos(equipos, "data/equipos.txt");
        EscritorDatos.guardarNominas(equipos, "data/nominas.txt");

        System.out.println("--- PERSISTENCIA ---");
        System.out.println("Datos guardados en data/socios.txt, data/equipos.txt, data/nominas.txt, data/ventas.txt, data/reservas.txt, data/sesiones.txt y data/asistencias.txt");
        System.out.println();
    }

    private static void probarInscripcionUnicaPorDeporte(List<Socio> socios, List<Equipo> equipos) {
        System.out.println("--- PRUEBA: Un jugador no puede estar en dos equipos del mismo deporte ---");
        Socio socio = socios.get(0);
        Equipo equipoA = equipos.get(0);
        Equipo equipoB = equipos.get(1);

        InscripcionEquipo insA = new InscripcionEquipo(socio, 50);
        boolean inscritoEnA = equipoA.agregarJugador(insA);
        System.out.println("Inscrito en " + equipoA.getNombre() + ": " + inscritoEnA);

        boolean yaEstaEnMismoDeporte = false;
        for (Equipo eq : equipos) {
            if (eq.getDeporte().equalsIgnoreCase(equipoB.getDeporte())) {
                for (InscripcionEquipo ins : eq.getNominaJugadores()) {
                    if (ins.getSocio().getLogin().equals(socio.getLogin())) {
                        yaEstaEnMismoDeporte = true;
                    }
                }
            }
        }

        if (yaEstaEnMismoDeporte) {
            System.out.println("El socio ya esta en un equipo de " + equipoB.getDeporte() + ". No se puede inscribir en otro.");
        } else {
            equipoB.agregarJugador(new InscripcionEquipo(socio, 99));
            System.out.println("Socio inscrito en " + equipoB.getNombre());
        }
        System.out.println();
    }

    private static void probarPartidoInterno(List<Equipo> equipos, List<Socio> socios) {
        System.out.println("--- PRUEBA: Partido interno con minimo de jugadores ---");
        Equipo equipoA = equipos.get(0);
        Equipo equipoB = equipos.get(1);

        int dorsal = 1;
        for (Socio s : socios) {
            if (equipoA.validarEdad(s.getEdad())) {
                InscripcionEquipo insA = new InscripcionEquipo(s, dorsal);
                insA.setPosicion("Mediocampista");
                equipoA.agregarJugador(insA);

                InscripcionEquipo insB = new InscripcionEquipo(s, dorsal);
                insB.setPosicion("Defensa");
                equipoB.agregarJugador(insB);
                dorsal++;
            }
        }

        System.out.println("Jugadores habilitados equipo A: " + equipoA.contarJugadoresHabilitados());
        System.out.println("Jugadores habilitados equipo B: " + equipoB.contarJugadoresHabilitados());

        PartidoInterno partido = new PartidoInterno("2025-05-20", "3-1", "Futbol");
        partido.agregarContrincante(equipoA);
        partido.agregarContrincante(equipoB);
        System.out.println("Misma categoria y deporte: " + partido.validarMismaCategoriaYDeporte());
        System.out.println("El partido se puede disputar: " + partido.validarParaDisputarse());
        System.out.println();
    }

    private static void probarSancionYHabilitados(List<Equipo> equipos, List<Socio> socios) {
        System.out.println("--- PRUEBA: Sancion por roja y conteo de habilitados ---");
        Equipo equipoA = equipos.get(0);
        Socio sancionado = socios.get(0);

        InscripcionEquipo ficha = null;
        for (InscripcionEquipo ins : equipoA.getNominaJugadores()) {
            if (ins.getSocio().getLogin().equals(sancionado.getLogin())) {
                ficha = ins;
            }
        }

        if (ficha == null) {
            System.out.println("El socio no esta inscrito en el equipo.");
            return;
        }

        int antes = equipoA.contarJugadoresHabilitados();
        EstadisticaFutbol est = new EstadisticaFutbol(sancionado, 0, 0, 1);
        est.procesarSanciones(ficha);
        int despues = equipoA.contarJugadoresHabilitados();

        System.out.println("Habilitados antes de la roja: " + antes);
        System.out.println("Habilitados despues de la roja: " + despues);
        System.out.println("El jugador quedo suspendido: " + ficha.isSuspendido());

        ficha.setSuspendido(false);
        System.out.println("Jugador reactivado para las siguientes pruebas.");
        System.out.println();
    }

    private static void probarReserva(List<Instalacion> instalaciones, List<Socio> socios) {
        System.out.println("--- PRUEBA: Reserva de instalacion ---");
        Instalacion cancha = instalaciones.get(1);
        Reserva reserva = new Reserva("2025-05-21 10:00", 4, "Dobles", cancha);
        reserva.agregarParticipante(socios.get(0));
        reserva.agregarParticipante(socios.get(1));
        reserva.agregarParticipante(socios.get(2));
        reserva.agregarParticipante(socios.get(3));
        boolean valida = reserva.validarCapacidad();
        System.out.println("Capacidad valida: " + valida);

        String lineaReserva = reserva.getBloqueHorario() + ";" + reserva.getCantidadJugadores() + ";"
                + reserva.getModalidad() + ";" + reserva.getInstalacion().getDisciplina();
        EscritorDatos.agregarLineaReserva(lineaReserva, "data/reservas.txt");
        System.out.println();
    }

    private static void probarVenta(List<Object> productos, List<Socio> socios, List<Empleado> empleados, List<Instalacion> instalaciones) {
        System.out.println("--- PRUEBA: Venta tienda y cafeteria ---");

        Socio comprador = socios.get(0);
        Empleado empleado = null;
        for (Empleado e : empleados) {
            if (e instanceof Entrenador) {
                empleado = (Entrenador) e;
            }
        }

        Venta venta1 = new Venta();
        venta1.setRealizaCompra(comprador);
        venta1.setFecha("2025-05-22");
        for (Object p : productos) {
            if (p instanceof ArticuloTienda) {
                venta1.agregarArticulo((ArticuloTienda) p);
            } else if (p instanceof Bebida) {
                venta1.agregarBebida((Bebida) p);
            } else if (p instanceof Snack) {
                venta1.agregarSnack((Snack) p);
            }
        }
        double total1 = venta1.calcularTotal(-1);
        System.out.println("Venta sin descuento: " + total1);
        System.out.println("Rubros: " + venta1.getDiscriminacionRubros());
        System.out.println("Avisos de alergenos: " + venta1.obtenerAvisoAlergenos());
        System.out.println("Puntos de fidelidad de " + comprador.getLogin() + ": " + comprador.getPuntosFidelidad());

        System.out.println();

        Venta venta2 = new Venta();
        venta2.setRealizaCompra(comprador);
        venta2.setRealizaCompraDescuento(empleado);
        venta2.setFecha("2025-05-23");
        if (empleado != null) {
            boolean codigoValido = venta2.validarCodigoEmpleado(empleado.getCodigoDescuento());
            System.out.println("Codigo de empleado valido: " + codigoValido);
        }
        venta2.agregarArticulo(new ArticuloTienda("Raqueta Pro", "Equipamiento", 150000, 10, "Bodega"));
        double total2 = venta2.calcularTotal(-1);
        System.out.println("Venta con descuento de empleado (15%): " + total2);

        System.out.println();

        Venta venta3 = new Venta();
        venta3.setRealizaCompra(comprador);
        venta3.setFecha("2025-05-24");
        venta3.agregarBebida(new Bebida(5000, false));
        venta3.agregarSnack(new Snack(3000, java.util.Arrays.asList("Gluten")));
        double total3 = venta3.calcularTotal(2000);
        System.out.println("Venta con propina modificada: " + total3);
        System.out.println("Propina: " + venta3.getPropina());

        System.out.println();

        Venta venta4 = new Venta();
        venta4.setRealizaCompra(comprador);
        venta4.setFecha("2025-05-25");
        venta4.agregarArticulo(new ArticuloTienda("Balon", "Equipamiento", 50000, 10, "Bodega"));
        boolean redimido = venta4.redimirPuntos(100);
        System.out.println("Puntos redimidos: " + redimido);
        double total4 = venta4.calcularTotal(-1);
        System.out.println("Venta con puntos redimidos: " + total4);
        System.out.println("Puntos restantes de " + comprador.getLogin() + ": " + comprador.getPuntosFidelidad());

        System.out.println();

        Venta venta5 = new Venta();
        venta5.setRealizaCompra(comprador);
        venta5.setFecha("2025-05-26");
        venta5.agregarBebida(new Bebida(5000, true));
        Instalacion padel = null;
        for (Instalacion i : instalaciones) {
            if (i.getDisciplina().equalsIgnoreCase("Padel")) padel = i;
        }
        Reserva reservaPadel = new Reserva("2025-05-26 10:00", 4, "Dobles", padel);
        reservaPadel.agregarParticipante(comprador);
        List<Reserva> reservasActivas = new ArrayList<>();
        reservasActivas.add(reservaPadel);
        boolean permitido = venta5.verificarRestriccionSeguridadBebidas(reservasActivas);
        System.out.println("Venta de bebida caliente en padel permitida: " + permitido);

        System.out.println();

        String lineaVenta = venta1.getFecha() + ";" + venta1.getDiscriminacionRubros() + ";" + total1;
        EscritorDatos.agregarLineaVenta(lineaVenta, "data/ventas.txt");
        System.out.println();
    }

    private static void probarCompetenciaLiga(List<Equipo> equipos, List<Socio> socios) {
        System.out.println("--- PRUEBA: Competencia formato LIGA (Torneo de los Cerros) ---");
        Equipo equipoA = equipos.get(0);
        Equipo equipoB = equipos.get(1);

        Competencia torneo = new Competencia("Torneo de los Cerros", "Universidades", "2025",
                "Futbol", "Sub-15", "Liga", 7);

        boolean inscA = torneo.inscribirEquipo(equipoA);
        boolean inscB = torneo.inscribirEquipo(equipoB);
        System.out.println("Equipo A inscrito: " + inscA);
        System.out.println("Equipo B inscrito: " + inscB);
        System.out.println("Jugadores en nomina de competencia: " + torneo.getNominaCompetencia().size());

        TablaPosiciones tabla = new TablaPosiciones(torneo.getEquiposInscritos());

        PartidoCompetencia p1 = new PartidoCompetencia("2025-06-01", "", "Universidad Nacional",
                "Estadio Central", "Fecha 1", equipoA);
        p1.setMarcador(3, 1);
        tabla.registrarResultado(equipoA, p1.calcularPuntos());
        p1.agregarEstadisticaJugador(new EstadisticaFutbol(socios.get(0), 2, 0, 0));
        p1.agregarEstadisticaJugador(new EstadisticaFutbol(socios.get(1), 1, 0, 0));

        PartidoCompetencia p2 = new PartidoCompetencia("2025-06-08", "", "Universidad Javeriana",
                "Estadio Central", "Fecha 2", equipoB);
        p2.setMarcador(0, 0);
        tabla.registrarResultado(equipoB, p2.calcularPuntos());

        tabla.mostrar();

        boolean puedeJugar = torneo.jugadorPuedeJugar(socios.get(0).getLogin());
        System.out.println("Jugador " + socios.get(0).getLogin() + " puede jugar: " + puedeJugar);
        System.out.println();
    }

    private static void probarCompetenciaFases(List<Equipo> equipos, List<Socio> socios) {
        System.out.println("--- PRUEBA: Competencia formato FASES (ASCUN) ---");
        Equipo equipoA = equipos.get(0);

        Competencia ascun = new Competencia("ASCUN", "ASCUN", "2025",
                "Futbol", "Sub-15", "Fases", 7);

        boolean insc = ascun.inscribirEquipo(equipoA);
        System.out.println("Equipo inscrito en ASCUN: " + insc);

        Fase grupos = new Fase("Fase de grupos", "Grupos");
        grupos.clasificarEquipo(equipoA);
        grupos.mostrarClasificados();

        Fase eliminacion = new Fase("Eliminacion directa", "Eliminacion");
        eliminacion.clasificarEquipo(equipoA);
        eliminacion.mostrarClasificados();
        System.out.println();
    }

    private static void probarGoleador(List<Equipo> equipos, List<Socio> socios) {
        System.out.println("--- PRUEBA: Acumulacion de estadisticas (goleador) ---");
        AcumuladorEstadisticas acumulador = new AcumuladorEstadisticas();

        EstadisticaFutbol e1 = new EstadisticaFutbol(socios.get(0), 2, 0, 0);
        EstadisticaFutbol e2 = new EstadisticaFutbol(socios.get(1), 1, 0, 0);
        EstadisticaFutbol e3 = new EstadisticaFutbol(socios.get(0), 1, 0, 0);

        acumulador.acumularGoles(e1.getSocio(), e1.getGoles());
        acumulador.acumularGoles(e2.getSocio(), e2.getGoles());
        acumulador.acumularGoles(e3.getSocio(), e3.getGoles());

        acumulador.mostrarGoleador();
        System.out.println();
    }

    private static void probarDeportesIndividuales(List<Instalacion> instalaciones, List<Socio> socios) {
        System.out.println("--- PRUEBA: Deportes individuales ---");

        Socio juan = socios.get(0);
        Socio pedro = socios.get(1);
        juan.setNivelRaqueta("Intermedio");
        pedro.setNivelRaqueta("Intermedio");

        PartidoPractica practica = new PartidoPractica("2025-07-01", "Tenis", juan, pedro);
        System.out.println("Niveles adyacentes: " + practica.validarNivelesAdyacentes());

        EstadisticaTenisPadel estJuan = new EstadisticaTenisPadel(juan);
        EstadisticaTenisPadel estPedro = new EstadisticaTenisPadel(pedro);
        practica.agregarEstadistica(estJuan);
        practica.agregarEstadistica(estPedro);
        System.out.println("Estadisticas registradas en el partido: " + practica.getRegistra().size());

        System.out.println("Ranking Juan antes: " + juan.getRanking("Tenis"));
        System.out.println("Ranking Pedro antes: " + pedro.getRanking("Tenis"));
        practica.actualizarRanking(juan, pedro);
        System.out.println("Ranking Juan despues: " + juan.getRanking("Tenis"));
        System.out.println("Ranking Pedro despues: " + pedro.getRanking("Tenis"));

        GestorReservas gestor = new GestorReservas(1, 1, 1);

        Instalacion canchaTenis = null;
        Instalacion canchaPadel = null;
        Instalacion mesaTenis = null;
        for (Instalacion i : instalaciones) {
            if (i.getDisciplina().equalsIgnoreCase("Tenis")) canchaTenis = i;
            if (i.getDisciplina().equalsIgnoreCase("Padel")) canchaPadel = i;
            if (i.getDisciplina().equalsIgnoreCase("Tenis de Mesa")) mesaTenis = i;
        }

        Reserva r1 = new Reserva("2025-07-02 10:00", 2, "Sencillos", canchaTenis);
        Reserva r2 = new Reserva("2025-07-02 10:00", 4, "Dobles", canchaPadel);
        Reserva r3 = new Reserva("2025-07-02 10:00", 2, "Sencillos", mesaTenis);

        System.out.println("Reserva tenis aceptada: " + gestor.registrarReserva(r1));
        System.out.println("Reserva padel aceptada: " + gestor.registrarReserva(r2));
        System.out.println("Reserva tenis de mesa aceptada: " + gestor.registrarReserva(r3));
        System.out.println("Reservas totales: " + gestor.getReservas().size());
        System.out.println();
    }

    private static void probarSesionEntrenamiento(List<Instalacion> instalaciones, List<Socio> socios,
                                                   List<Equipo> equipos, List<Empleado> empleados) {
        System.out.println("--- PRUEBA: Sesion de entrenamiento ---");

        Entrenador entrenador = null;
        for (Empleado e : empleados) {
            if (e instanceof Entrenador) {
                entrenador = (Entrenador) e;
            }
        }
        if (entrenador == null) {
            System.out.println("No hay entrenador cargado.");
            return;
        }

        Instalacion canchaTenis = null;
        for (Instalacion i : instalaciones) {
            if (i.getDisciplina().equalsIgnoreCase("Tenis")) canchaTenis = i;
        }

        SesionEntrenamiento sesion = new SesionEntrenamiento("2025-07-10", "09:00", 90,
                entrenador, canchaTenis);

        for (int i = 0; i < 4; i++) {
            sesion.convocarJugador(socios.get(i));
        }
        System.out.println("Jugadores convocados: " + sesion.getListaJugadoresConvocados().size());

        sesion.registrarAsistencia(socios.get(0), "Asistio", 4, "Buen desempeno");
        sesion.registrarAsistencia(socios.get(1), "Llego tarde", 3, "Llego 10 minutos tarde");
        sesion.registrarAsistencia(socios.get(2), "Falto", 0, "");

        System.out.println("Asistencias: " + sesion.contarAsistencias());
        System.out.println("Faltas: " + sesion.contarFaltas());

        AnaliticaDesempeno analitica = new AnaliticaDesempeno();
        for (AsistenciaEvaluacion a : sesion.getGeneraRegistros()) {
            analitica.agregarRegistro(a);
        }
        analitica.mostrarResumen(socios.get(0));
        analitica.mostrarResumen(socios.get(1));
        analitica.mostrarResumen(socios.get(2));

        String lineaSesion = sesion.getFecha() + ";" + sesion.getHora() + ";" + sesion.getDuracion()
                + ";" + sesion.getPlanea().getLogin() + ";" + sesion.getUtiliza().getDisciplina()
                + ";" + sesion.getListaJugadoresConvocados().size();
        EscritorDatos.agregarLineaSesion(lineaSesion, "data/sesiones.txt");

        for (AsistenciaEvaluacion a : sesion.getGeneraRegistros()) {
            String lineaAsistencia = sesion.getFecha() + ";" + a.getAsiste().getLogin() + ";"
                    + a.getEstadoAsistencia() + ";" + a.getNotaDesempeno() + ";" + a.getComentario();
            EscritorDatos.agregarLineaAsistencia(lineaAsistencia, "data/asistencias.txt");
        }

        System.out.println("Sesion y asistencias guardadas.");
        System.out.println();
    }
    private static void probarAdministrador(List<Socio> socios, List<Equipo> equipos,
            List<Instalacion> instalaciones, List<Object> productos,
            List<Empleado> empleados, List<Reserva> reservas,
            List<Venta> ventas) {
System.out.println("--- PRUEBA: Administrador ---");

Administrador admin = new Administrador("admin", "admin123");

List<ArticuloTienda> articulos = new ArrayList<>();
for (Object p : productos) {
if (p instanceof ArticuloTienda) articulos.add((ArticuloTienda) p);
}
admin.verInventario(instalaciones, articulos);

admin.verHistorialReservas(reservas);

Equipo nuevoEquipo = new Equipo("Los Nuevos", "Futbol", "Sub-17", 15, 7);
boolean creado = admin.crearEquipo(equipos, nuevoEquipo);
System.out.println("Equipo creado: " + creado);
boolean eliminado = admin.darDeBajaEquipo(equipos, "Los Nuevos");
System.out.println("Equipo eliminado: " + eliminado);

Socio nuevoSocio = new Socio("nuevo@mail.com", "1234", "Sub-17", 16, "Principiante");
boolean creadoJugador = admin.crearJugador(socios, nuevoSocio);
System.out.println("Jugador creado: " + creadoJugador);
boolean eliminadoJugador = admin.darDeBajaJugador(socios, equipos, "nuevo@mail.com");
System.out.println("Jugador eliminado: " + eliminadoJugador);

Empleado empleado = empleados.get(0);
SugerenciaEmpleado sugerencia = new SugerenciaEmpleado("Snack de mani nuevo", empleado);
admin.aprobarSugerencia(sugerencia, productos);
System.out.println("Estado de la sugerencia: " + sugerencia.getEstado());

Turno manana = new Turno("Lunes", "Manana");
Turno tarde = new Turno("Lunes", "Tarde");
GestorTurnos gestor = new GestorTurnos();
gestor.asignarTurno(empleados.get(0), manana);
gestor.asignarTurno(empleados.get(1), manana);

SolicitudCambioTurno solicitud = new SolicitudCambioTurno(empleados.get(0), manana, tarde);
boolean aprobado = gestor.aprobarCambio(solicitud);
System.out.println("Cambio de turno aprobado: " + aprobado);
System.out.println("Estado: " + solicitud.getEstadoAprobacion());

admin.verReporteVentas(ventas, "Tienda");

List<SesionEntrenamiento> sesiones = new ArrayList<>();
admin.verReporteDeportivo(socios, sesiones);

System.out.println("Administrador puede inscribirse: " + admin.puedeInscribirse());
System.out.println("Administrador puede reservar: " + admin.puedeReservar());

String lineaSugerencia = sugerencia.getDescripcion() + ";" + sugerencia.getHaceSugerencia().getLogin() + ";" + sugerencia.getEstado();
EscritorDatos.agregarLineaSugerencia(lineaSugerencia, "data/sugerencias.txt");

String lineaTurno = solicitud.getSolicita().getLogin() + ";" + solicitud.getTurnoActual().getDia() + ";" + solicitud.getTurnoActual().getHora()
+ ";" + solicitud.getTurnoNuevo().getDia() + ";" + solicitud.getTurnoNuevo().getHora() + ";" + solicitud.getEstadoAprobacion();
EscritorDatos.agregarLineaTurno(lineaTurno, "data/turnos.txt");

System.out.println();
}
}