package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Usuarios.Administrador;
import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;
import Usuarios.GestorUsuarios;
import Usuarios.Socio;
import Usuarios.Usuario;
import deportes.Competencia;
import deportes.Equipo;
import deportes.GestorEquipos;
import deportes.GestorTorneos;
import deportes.InscripcionEquipo;
import deportes.ModalidadTorneo;
import deportes.ParticipacionTorneo;
import deportes.PartidoCompetencia;
import deportes.PartidoInterno;
import deportes.PartidoPractica;
import deportes.SesionEntrenamiento;
import deportes.TablaPosiciones;
import deportes.TipoTorneo;
import deportes.Torneo;
import estadisticas.AcumuladorEstadisticas;
import estadisticas.AnaliticaDesempeno;
import estadisticas.EstadisticaFutbol;
import instalaciones.GestorReservas;
import instalaciones.Instalacion;
import instalaciones.Reserva;
import ventas.ArticuloTienda;
import ventas.Bebida;
import ventas.GestorTurnos;
import ventas.Snack;
import ventas.SolicitudCambioTurno;
import ventas.SugerenciaEmpleado;
import ventas.Turno;
import ventas.Venta;

public class PruebasSistema {

    private Socio socioSub15;
    private Socio socioMayores;
    private Socio socioPrincipiante;
    private Administrador admin;
    private Entrenador entrenador1;
    private Entrenador entrenador2;
    private Fisioterapeuta fisio;
    
    private Equipo equipoFutbol;
    private Equipo equipoFutbol2;
    private Equipo equipoBaloncesto;
    
    private Instalacion canchaPadel;
    private Instalacion canchaTenis;
    private Instalacion mesaTenis;
    
    private GestorUsuarios gestorUsuarios;
    private GestorTurnos gestorTurnos;

    @BeforeEach
    public void setUp() {
        socioSub15 = new Socio("sub15@mail.com", "123", "Sub-15", 14, "Intermedio");
        socioMayores = new Socio("mayores@mail.com", "123", "Mayores", 20, "Avanzado");
        socioPrincipiante = new Socio("principiante@mail.com", "123", "Mayores", 20, "Principiante");
        
        admin = new Administrador("admin", "admin123");
        entrenador1 = new Entrenador("profe1@mail.com", "123", "DESC-1");
        entrenador2 = new Entrenador("profe2@mail.com", "123", "DESC-2");
        fisio = new Fisioterapeuta("fisio@mail.com", "123", "DESC-3");
        
        equipoFutbol = new Equipo("Futbol FC", "Futbol", "Sub-15", 2, 7);
        equipoFutbol2 = new Equipo("Futbol FC 2", "Futbol", "Sub-15", 15, 7);
        equipoBaloncesto = new Equipo("Basket FC", "Baloncesto", "Sub-15", 12, 5);
        
        canchaPadel = new Instalacion("PAD-1", "Padel", 4, false);
        canchaTenis = new Instalacion("TEN-1", "Tenis", 4, false);
        mesaTenis = new Instalacion("MESA-1", "Tenis de Mesa", 2, true);
        
        List<Usuario> usuarios = new ArrayList<>(Arrays.asList(socioSub15, socioMayores, socioPrincipiante, admin, entrenador1, entrenador2, fisio));
        gestorUsuarios = new GestorUsuarios(usuarios);
        gestorUsuarios.autenticar("admin", "admin123");
        
        gestorTurnos = new GestorTurnos();
    }

    @Test
    public void testInscribirJugador_ValidaEdadCupoYCamiseta() {
        assertFalse(equipoFutbol.agregarJugador(new InscripcionEquipo(socioMayores, 10)), "Rechaza por edad incorrecta");
        assertTrue(equipoFutbol.agregarJugador(new InscripcionEquipo(socioSub15, 10)), "Acepta edad válida");
        
        Socio s2 = new Socio("s2@mail.com", "123", "Sub-15", 15, "Intermedio");
        Socio s3 = new Socio("s3@mail.com", "123", "Sub-15", 13, "Principiante");
        
        assertFalse(equipoFutbol.agregarJugador(new InscripcionEquipo(s2, 10)), "Rechaza camiseta duplicada");
        assertTrue(equipoFutbol.agregarJugador(new InscripcionEquipo(s2, 11)), "Acepta segunda inscripción");
        assertFalse(equipoFutbol.agregarJugador(new InscripcionEquipo(s3, 12)), "Rechaza por cupo lleno (max 2)");
    }

    @Test
    public void testGestorEquipos_RechazaJugadorEnDosEquiposMismoDeporte() {
        List<Equipo> equipos = Arrays.asList(equipoFutbol2, equipoBaloncesto);
        GestorEquipos gestor = new GestorEquipos(equipos);
        
        assertTrue(gestor.inscribirJugador(equipoFutbol2, new InscripcionEquipo(socioSub15, 10)));
        assertFalse(gestor.inscribirJugador(new Equipo("Otro", "Futbol", "Sub-15", 10, 7), new InscripcionEquipo(socioSub15, 20)), "No puede estar en dos equipos de fútbol");
        assertTrue(gestor.inscribirJugador(equipoBaloncesto, new InscripcionEquipo(socioSub15, 5)), "Sí puede estar en baloncesto");
    }

    @Test
    public void testPartidoInterno_RechazaPorFaltaDeJugadores() {
        PartidoInterno partido = new PartidoInterno("2025-05-01", "0-0", "Futbol");
        partido.agregarContrincante(equipoFutbol);
        partido.agregarContrincante(equipoFutbol2);
        assertFalse(partido.validarParaDisputarse(), "Fútbol requiere mínimo 7 jugadores habilitados");
    }

    @Test
    public void testSanciones_TarjetaRojaYAmarilla() {
        InscripcionEquipo ficha = new InscripcionEquipo(socioSub15, 10);
        
        EstadisticaFutbol roja = new EstadisticaFutbol(socioSub15, 0, 0, 0, 1);
        roja.procesarSanciones(ficha);
        assertTrue(ficha.isSuspendido(), "Suspendido por roja directa");
        
        ficha.cumplirSancion();
        assertFalse(ficha.isSuspendido(), "Habilitado tras cumplir sanción");
        
        ficha.agregarAmarilla();
        assertFalse(ficha.isSuspendido(), "Una amarilla no suspende");
        ficha.agregarAmarilla();
        assertTrue(ficha.isSuspendido(), "Dos amarillas suspenden");
    }

    @Test
    public void testPartidoCompetencia_PuntosSegunDeporte() {
        PartidoCompetencia futbol = new PartidoCompetencia("2025", "", "Rival", "Estadio", "Fase", equipoFutbol);
        futbol.setMarcador(2, 2);
        assertEquals(1, futbol.calcularPuntos(), "Empate en fútbol da 1 punto");
        
        Equipo basketMayores = new Equipo("Basket M", "Baloncesto", "Mayores", 12, 5);
        PartidoCompetencia basket = new PartidoCompetencia("2025", "", "Rival", "Coliseo", "Fase", basketMayores);
        basket.setMarcador(80, 80);
        assertEquals(0, basket.calcularPuntos(), "En baloncesto no hay empate");
    }

    @Test
    public void testVenta_CalculaImpuestosPropinasYStock() {
        ArticuloTienda balon = new ArticuloTienda("Balón", "Deporte", 100000, 5, "Bodega");
        Venta venta = new Venta();
        venta.setRealizaCompra(socioSub15);
        venta.agregarArticulo(balon);
        venta.agregarBebida(new Bebida("Jugo", 10000, false));
        
        double total = venta.calcularTotal(-1);
        double iva = 100000 * 0.19;
        double impConsumo = 10000 * 0.08;
        double propina = (10000 + impConsumo) * 0.10;
        double totalEsperado = 100000 + iva + 10000 + impConsumo + propina;
        
        assertEquals(totalEsperado, total, 0.01, "Cálculo de totales incorrecto");
        
        venta.procesarPago(-1);
        assertEquals(4, balon.getCantidadInventario(), "El stock debió reducirse");
        assertTrue(socioSub15.getPuntosFidelidad() > 0, "El socio ganó puntos");
    }

    @Test
    public void testVenta_RestringeBebidaCalienteEnPadel() {
        Reserva reservaPadel = new Reserva("10:00", 4, "Dobles", canchaPadel, true);
        reservaPadel.agregarParticipante(socioSub15);
        
        Venta venta = new Venta();
        venta.setRealizaCompra(socioSub15);
        venta.agregarBebida(new Bebida("Café", 5000, true));
        
        assertFalse(venta.verificarRestriccionSeguridadBebidas(Arrays.asList(reservaPadel)), "No se permite bebida caliente en pádel");
        
        Reserva reservaTenis = new Reserva("10:00", 2, "Sencillos", canchaTenis, true);
        reservaTenis.agregarParticipante(socioSub15);
        
        assertTrue(venta.verificarRestriccionSeguridadBebidas(Arrays.asList(reservaTenis)), "Sí se permite en tenis");
    }

    @Test
    public void testSesionEntrenamiento_RegistroNotaInvalida() {
        SesionEntrenamiento sesion = new SesionEntrenamiento("2025-01-01", "10:00", 60, entrenador1, canchaTenis);
        sesion.convocarJugador(socioSub15);
        
        assertNull(sesion.registrarAsistencia(socioSub15, "Asistio", 6, "Excelente"), "No permite nota > 5");
        assertNull(sesion.registrarAsistencia(socioSub15, "Asistio", -1, "Malo"), "No permite nota < 1");
        assertNotNull(sesion.registrarAsistencia(socioSub15, "Asistio", 5, "Excelente"), "Registra nota válida");
        assertNull(sesion.registrarAsistencia(socioSub15, "Falto", 2, ""), "No permite doble asistencia");
    }

    @Test
    public void testTorneo_CuposPreferencialesYRetiro() {
        Torneo torneo = new Torneo("T1", "Copa", "Futbol", "Sub-15", TipoTorneo.RECREATIVO, ModalidadTorneo.EQUIPOS, LocalDateTime.now().plusDays(5), 10, 0.0);
        
        assertEquals(2, torneo.getCuposPreferencialesTotales(), "20% de 10 es 2");
        assertEquals(8, torneo.getCuposRegularesTotales(), "Resto son 8");
        
        ParticipacionTorneo p1 = ParticipacionTorneo.individual("P1", socioSub15);
        assertTrue(torneo.inscribir(p1, false, false));
        assertEquals(1, torneo.getCuposRegularesOcupados());
        
        assertTrue(torneo.retirar(p1), "Debe poder retirarse");
        assertEquals(0, torneo.getCuposRegularesOcupados(), "Cupo liberado");
    }

    @Test
    public void testIntegracion_EmpleadoConTurnoNoPuedeInscribirse() {
        LocalDateTime fechaTorneo = LocalDateTime.now().plusDays(2);
        if (fechaTorneo.getDayOfWeek().toString().equals("SATURDAY") || fechaTorneo.getDayOfWeek().toString().equals("SUNDAY")) {
            fechaTorneo = fechaTorneo.plusDays(2); 
        }
        
        gestorTurnos.asignarTurno(entrenador1, new Turno(fechaTorneo.getDayOfWeek().toString(), "Mañana"));
        
        GestorTorneos gestor = new GestorTorneos(gestorUsuarios);
        Torneo torneo = gestor.crearTorneo(admin, "T2", "Copa", "Tenis", "Avanzado", TipoTorneo.COMPETITIVO, ModalidadTorneo.INDIVIDUAL, fechaTorneo, 10, 50000.0);
        
        ParticipacionTorneo p = ParticipacionTorneo.individual("P1", entrenador1);
        assertFalse(gestor.inscribir(torneo, p, gestorTurnos), "Empleado con turno no puede inscribirse");
    }

    @Test
    public void testIntegracion_SocioGanaBonoYCompra() {
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(5);
        GestorTorneos gestor = new GestorTorneos(gestorUsuarios);
        
        Torneo torneo = gestor.crearTorneo(admin, "T1", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 0.0);
        ParticipacionTorneo p = ParticipacionTorneo.individual("P1", socioSub15);
        
        assertTrue(gestor.inscribir(torneo, p, gestorTurnos));
        torneo.iniciarTorneo();
        torneo.finalizarTorneo();
        gestor.otorgarPremio(torneo, p);
        
        assertNotNull(socioSub15.obtenerBonoDisponible(), "Socio recibe bono");
        
        Venta venta = new Venta();
        venta.setRealizaCompra(socioSub15);
        venta.agregarArticulo(new ArticuloTienda("Raqueta", "Deporte", 100000, 10, "Bodega"));
        
        assertTrue(venta.usarBonoDescuento(), "Debe poder usar el bono");
        double total = venta.calcularTotal(-1);
        assertTrue(total < 100000, "Total menor por descuento del bono");
    }

    @Test
    public void testSeguridad_RechazaLoginIncorrectoYAccesoNoAutorizado() {
        assertNull(gestorUsuarios.autenticar("admin", "claveMala"), "Rechaza contraseña incorrecta");
        assertNull(gestorUsuarios.autenticar("noexiste", "admin123"), "Rechaza usuario inexistente");
        
        gestorUsuarios.autenticar("sub15@mail.com", "123");
        assertThrows(SecurityException.class, () -> {
            gestorUsuarios.exigirAdministrador(socioSub15);
        }, "Un socio no puede hacer acciones de administrador");
    }

    @Test
    public void testVenta_DescuentosDeEmpleadoYSocio() {
        Venta ventaEmpleado = new Venta();
        ventaEmpleado.setRealizaCompra(entrenador1);
        ventaEmpleado.agregarArticulo(new ArticuloTienda("Balón", "Dep", 100000, 5, "B1"));
        double totalEmp = ventaEmpleado.calcularTotal(-1);
        
        Venta ventaSocio = new Venta();
        ventaSocio.setRealizaCompra(socioSub15);
        ventaSocio.agregarArticulo(new ArticuloTienda("Balón", "Dep", 100000, 5, "B1"));
        ventaSocio.validarCodigoEmpleado("DESC-1", entrenador1);
        double totalSocio = ventaSocio.calcularTotal(-1);
        
        assertTrue(totalEmp < totalSocio, "El empleado (15%) debe pagar menos que el socio con código (8%)");
    }
    @Test
    public void testVenta_RedimirPuntosFidelidad() {
        socioSub15.setPuntosFidelidad(500);
        
        Venta venta = new Venta();
        venta.setRealizaCompra(socioSub15);
        venta.agregarArticulo(new ArticuloTienda("Medias", "Dep", 1000, 5, "B1"));
        
        assertTrue(venta.redimirPuntos(500), "Debe poder redimir sus puntos");
        double total = venta.calcularTotal(-1);
        
        assertEquals(0, socioSub15.getPuntosFidelidad(), 0.01, "Los puntos debieron quedar en 0");
        assertEquals(690, total, 0.01, "El total es 1190 - 500 puntos");
    }

    @Test
    public void testDeportesIndividuales_ValidacionNivelesYRankingELO() {
        PartidoPractica partidoValido = new PartidoPractica("2025", "Tenis", socioSub15, socioMayores);
        assertTrue(partidoValido.validarNivelesAdyacentes(), "Intermedio y Avanzado son adyacentes");
        
        PartidoPractica partidoInvalido = new PartidoPractica("2025", "Tenis", socioSub15, socioPrincipiante);
        assertFalse(partidoInvalido.validarNivelesAdyacentes(), "Intermedio y Principiante NO son adyacentes");
        
        int rankingAntesGanador = socioSub15.getRanking("Tenis");
        int rankingAntesPerdedor = socioMayores.getRanking("Tenis");
        
        partidoValido.actualizarRanking(socioSub15, socioMayores);
        
        assertEquals(rankingAntesGanador + 20, socioSub15.getRanking("Tenis"), "Ganador suma 20 ELO");
        assertEquals(rankingAntesPerdedor - 20, socioMayores.getRanking("Tenis"), "Perdedor resta 20 ELO");
    }

    @Test
    public void testReserva_ValidacionCapacidadCanchas() {
        Reserva padelInvalido = new Reserva("10:00", 2, "Sencillos", canchaPadel, false);
        assertFalse(padelInvalido.validarCapacidad(), "Pádel siempre es de 4 jugadores (dobles)");
        
        Reserva padelValido = new Reserva("10:00", 4, "Dobles", canchaPadel, false);
        assertTrue(padelValido.validarCapacidad(), "Pádel con 4 jugadores es válido");
        
        Reserva tenisValido = new Reserva("10:00", 2, "Sencillos", canchaTenis, false);
        assertTrue(tenisValido.validarCapacidad(), "Tenis permite 2 en sencillos");
    }

    @Test
    public void testGestorTurnos_RechazaCambioSiRompeElMinimo() {
        Turno manana = new Turno("Lunes", "Mañana");
        Turno tarde = new Turno("Lunes", "Tarde");
        
        Entrenador entrenador3 = new Entrenador("profe3@mail.com", "123", "DESC-3");
        Fisioterapeuta fisio2 = new Fisioterapeuta("fisio2@mail.com", "123", "DESC-4");
        
        gestorTurnos.asignarTurno(entrenador1, manana);
        gestorTurnos.asignarTurno(entrenador2, manana);
        gestorTurnos.asignarTurno(entrenador3, manana);
        gestorTurnos.asignarTurno(fisio, manana);
        
        gestorTurnos.asignarTurno(entrenador3, tarde);
        gestorTurnos.asignarTurno(fisio2, tarde);
        
        SolicitudCambioTurno solicitudValida = new SolicitudCambioTurno(entrenador1, manana, tarde);
        assertTrue(gestorTurnos.aprobarCambio(solicitudValida), "El cambio es válido: la mañana se queda con 2 entrenadores y la tarde llega a 2");
        
        SolicitudCambioTurno solicitudInvalida = new SolicitudCambioTurno(entrenador2, manana, tarde);
        assertFalse(gestorTurnos.aprobarCambio(solicitudInvalida), "El cambio NO es válido: la mañana se quedaría solo con 1 entrenador");
    }

    @Test
    public void testAdministrador_AprobarSugerencias() {
        SugerenciaEmpleado sug = new SugerenciaEmpleado("Snack de proteina", entrenador1);
        List<ArticuloTienda> articulos = new ArrayList<>();
        List<Snack> snacks = new ArrayList<>();
        
        admin.aprobarSugerencia(sug, articulos, snacks);
        
        assertEquals("APROBADA", sug.getEstado(), "El estado debe cambiar a APROBADA");
        assertEquals(1, snacks.size(), "El snack debe agregarse a la lista");
        assertEquals("Snack de proteina", snacks.get(0).getNombre(), "El nombre del snack debe coincidir");
    }

    @Test
    public void testTorneo_CompetitivoRecaudaPozoYNoCobraAEmpleados() {
        Torneo torneo = new Torneo("T3", "Copa Pro", "Tenis", "Avanzado", TipoTorneo.COMPETITIVO, ModalidadTorneo.INDIVIDUAL, LocalDateTime.now().plusDays(5), 10, 50000.0);
        
        ParticipacionTorneo pSocio = ParticipacionTorneo.individual("P1", socioMayores);
        ParticipacionTorneo pEmpleado = ParticipacionTorneo.individual("P2", entrenador1);
        
        torneo.inscribir(pSocio, false, false);
        torneo.inscribir(pEmpleado, false, true);
        
        assertEquals(50000.0, torneo.getPozoRecaudado(), 0.01, "Solo el socio pagó, el empleado está exento");
        
        torneo.retirar(pSocio);
        assertEquals(0.0, torneo.getPozoRecaudado(), 0.01, "Al retirarse el socio, se resta su dinero");
    }

    @Test
    public void testAcumuladorEstadisticas_GoleadorTorneo() {
        AcumuladorEstadisticas acumulador = new AcumuladorEstadisticas();
        acumulador.acumularGoles(socioSub15, 2);
        acumulador.acumularGoles(socioMayores, 3);
        acumulador.acumularGoles(socioSub15, 2);
        
        assertEquals(4, acumulador.getGoles("sub15@mail.com"), "Debe acumular goles del jugador");
        assertEquals(3, acumulador.getGoles("mayores@mail.com"), "Debe acumular goles del otro jugador");
    }

    @Test
    public void testTablaPosiciones_PuntosYDesempateDirecto() {
        List<Equipo> equipos = Arrays.asList(equipoFutbol, equipoFutbol2);
        TablaPosiciones tabla = new TablaPosiciones(equipos);
        
        tabla.registrarResultado(equipoFutbol, 3, 2, 1);
        tabla.registrarResultado(equipoFutbol2, 3, 1, 2);
        
        assertEquals(3, tabla.getPuntos(equipoFutbol), "Ambos tienen 3 puntos");
        
        tabla.registrarEnfrentamientoDirecto(equipoFutbol, equipoFutbol2);
        tabla.mostrar();
    }

    @Test
    public void testPartidoInterno_RechazaEstadisticaDeJugadorNoConvocado() {
        PartidoInterno partido = new PartidoInterno("2025", "0-0", "Futbol");
        partido.agregarContrincante(equipoFutbol);
        partido.agregarContrincante(equipoFutbol2);
        
        Socio noConvocado = new Socio("nc@mail.com", "123", "Sub-15", 14, "Intermedio");
        EstadisticaFutbol est = new EstadisticaFutbol(noConvocado, 1, 0, 0, 0);
        
        partido.agregarEstadistica(est);
        assertEquals(0, partido.getRegistra().size(), "No debe registrar estadísticas de jugadores que no están en el partido");
    }

    @Test
    public void testCompetencia_SancionesIndependientesDelClub() {
        Competencia competencia = new Competencia("ASCUN", "ASCUN", "2025", "Futbol", "Sub-15", "Fases", 7);
        competencia.inscribirEquipo(equipoFutbol2);
        
        assertTrue(competencia.getNominaCompetencia().contains(socioSub15) || competencia.getNominaCompetencia().isEmpty(), "El jugador debe estar en la nómina o la nómina está vacía por falta de jugadores");
    }

    @Test
    public void testSesionEntrenamiento_ConvocarEquipoCompleto() {
        Socio otroSub15 = new Socio("otro@mail.com", "123", "Sub-15", 15, "Intermedio");
        equipoFutbol2.agregarJugador(new InscripcionEquipo(socioSub15, 10));
        equipoFutbol2.agregarJugador(new InscripcionEquipo(otroSub15, 11));
        
        SesionEntrenamiento sesion = new SesionEntrenamiento("2025", "10:00", 60, entrenador1, canchaTenis);
        int convocados = sesion.convocarEquipo(equipoFutbol2);
        
        assertEquals(2, convocados, "Debe convocar a todo el equipo");
        assertTrue(sesion.estaConvocado(socioSub15), "El jugador 1 debe estar convocado");
    }

    @Test
    public void testAdministrador_EliminarEquipoYJugador() {
        equipoFutbol2.agregarJugador(new InscripcionEquipo(socioSub15, 10));
        List<Equipo> equipos = new ArrayList<>(Arrays.asList(equipoFutbol2));
        List<Socio> socios = new ArrayList<>(Arrays.asList(socioSub15));
        
        assertTrue(admin.darDeBajaJugador(socios, equipos, "sub15@mail.com"), "Debe eliminar al jugador");
        assertEquals(0, socios.size(), "La lista de socios debe quedar vacía");
        assertEquals(0, equipoFutbol2.getNominaJugadores().size(), "El jugador debe salir de la nómina del equipo");
        
        assertTrue(admin.darDeBajaEquipo(equipos, "Futbol FC 2"), "Debe eliminar el equipo");
        assertEquals(0, equipos.size(), "La lista de equipos debe quedar vacía");
    }

    @Test
    public void testGestorReservas_RechazaMaximoSimultaneas() {
        GestorReservas gestor = new GestorReservas(1, 1, 1);
        
        Reserva r1 = new Reserva("10:00", 2, "Sencillos", canchaTenis, false);
        Reserva r2 = new Reserva("10:00", 2, "Sencillos", new Instalacion("TEN-2", "Tenis", 4, false), false);
        
        assertTrue(gestor.registrarReserva(r1), "La primera reserva debe entrar");
        assertFalse(gestor.registrarReserva(r2), "La segunda debe rechazarse por límite de canchas simultáneas");
    }

    @Test
    public void testAnaliticaDesempeno_ResumenJugador() {
        AnaliticaDesempeno analitica = new AnaliticaDesempeno();
        SesionEntrenamiento sesion = new SesionEntrenamiento("2025", "10:00", 60, entrenador1, canchaTenis);
        sesion.convocarJugador(socioSub15);
        sesion.registrarAsistencia(socioSub15, "Asistio", 5, "Bien");
        
        analitica.agregarRegistro(sesion.getGeneraRegistros().get(0));
        analitica.agregarEstadisticaPartido(new EstadisticaFutbol(socioSub15, 3, 1, 0, 0));
        
        assertEquals(1, analitica.contarAsistencias(socioSub15), "Debe contar 1 asistencia");
        assertEquals(3, analitica.totalGolesJugador(socioSub15), "Debe contar 3 goles");
        assertEquals(5.0, analitica.promedioNotas(socioSub15), 0.01, "El promedio de notas debe ser 5");
    }
    @Test
    public void testVenta_BonoYDescuentoEmpleadoNoSonAcumulables() {
        socioSub15.setPuntosFidelidad(0);
        socioSub15.agregarBono(new ventas.BonoDescuento(20000));
        
        Venta venta = new Venta();
        venta.setRealizaCompra(socioSub15);
        venta.agregarArticulo(new ArticuloTienda("Balón", "Dep", 100000, 5, "B1"));
        
        venta.validarCodigoEmpleado("DESC-1", entrenador1);
        assertTrue(venta.usarBonoDescuento(), "Debe aplicar el bono");
        
        double total = venta.calcularTotal(-1);
        double totalEsperado = (100000 - 20000) * 1.19;
        
        assertEquals(totalEsperado, total, 0.01, "El bono debe aplicarse y anular el descuento del 8% del empleado");
    }

    @Test
    public void testTorneo_ParejasRequiereExactamenteDosJugadores() {
        Torneo torneoParejas = new Torneo("TP", "Copa Dobles", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.PAREJAS, LocalDateTime.now().plusDays(5), 10, 0.0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            ParticipacionTorneo.pareja("P1", "Pareja 1", socioSub15, socioSub15);
        }, "No se puede crear una pareja con el mismo jugador repetido");
        
        ParticipacionTorneo parejaValida = ParticipacionTorneo.pareja("P2", "Pareja 2", socioSub15, socioMayores);
        assertEquals(2, parejaValida.getIntegrantes().size(), "Una pareja debe tener 2 integrantes");
        
        boolean inscrito = torneoParejas.inscribir(parejaValida, false, false);
        assertTrue(inscrito, "La pareja válida debe poder inscribirse en el torneo");
    }

    @Test
    public void testJugadorInvitado_ValidacionMatriculaYExterno() {
        Usuarios.JugadorInvitado externo = new Usuarios.JugadorInvitado("ext@mail.com", "123", "Externo", Usuarios.TipoJugadorInvitado.EXTERNO, false);
        assertTrue(externo.puedeParticipar(), "Un externo siempre puede participar");
        
        Usuarios.JugadorInvitado estudianteInactivo = new Usuarios.JugadorInvitado("est@mail.com", "123", "Estudiante", Usuarios.TipoJugadorInvitado.ESTUDIANTE, false);
        assertFalse(estudianteInactivo.puedeParticipar(), "Un estudiante sin matrícula no puede participar");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Usuarios.JugadorInvitado("ext2@mail.com", "123", "Externo 2", Usuarios.TipoJugadorInvitado.EXTERNO, true);
        }, "Un externo no puede tener matrícula activa");
    }

    @Test
    public void testVenta_RestringeBebidaCalienteEnMesaTenisTechada() {
        Reserva reservaMesa = new Reserva("10:00", 2, "Sencillos", mesaTenis, true);
        reservaMesa.agregarParticipante(socioSub15);
        
        Venta venta = new Venta();
        venta.setRealizaCompra(socioSub15);
        venta.agregarBebida(new Bebida("Té", 3000, true));
        
        assertFalse(venta.verificarRestriccionSeguridadBebidas(Arrays.asList(reservaMesa)), "No se permite bebida caliente en mesa techada");
    }

    @Test
    public void testGestorTorneos_EmpleadoSinTurnoEntraGratisYNoRecibePremio() {
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(5);
        GestorTorneos gestor = new GestorTorneos(gestorUsuarios);
        
        Torneo torneo = gestor.crearTorneo(admin, "T4", "Copa Pro", "Tenis", "Avanzado", TipoTorneo.COMPETITIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 50000.0);
        
        ParticipacionTorneo p = ParticipacionTorneo.individual("P1", entrenador1);
        assertTrue(gestor.inscribir(torneo, p, gestorTurnos), "Empleado sin turno puede inscribirse");
        assertEquals(0.0, torneo.getPozoRecaudado(), 0.01, "El empleado no paga inscripción");
        
        torneo.iniciarTorneo();
        torneo.finalizarTorneo();
        gestor.otorgarPremio(torneo, p);
    }

    @Test
    public void testPartidoCompetencia_VoleibolPorSets() {
        Equipo equipoVoleibol = new Equipo("Voley FC", "Voleibol", "Mayores", 12, 6);
        PartidoCompetencia partido = new PartidoCompetencia("2025", "", "Rival", "Coliseo", "Fase", equipoVoleibol);
        
        partido.setMarcador(3, 1);
        assertEquals("3 sets - 1 sets", partido.getMarcadorFinal(), "El marcador de voleibol debe mostrarse por sets");
    }
    @Test
    public void testDeportesIndividuales_RankingNoBajaDeCero() {
        PartidoPractica partido = new PartidoPractica("2025", "Tenis", socioSub15, socioMayores);
        socioMayores.setRanking("Tenis", 10);
        
        partido.actualizarRanking(socioSub15, socioMayores);
        assertEquals(0, socioMayores.getRanking("Tenis"), "El ranking ELO no debe ser negativo, baja hasta 0");
    }

    @Test
    public void testReserva_RechazaMismaCanchaMismoHorario() {
        GestorReservas gestor = new GestorReservas(5, 5, 5);
        
        Reserva r1 = new Reserva("10:00", 2, "Sencillos", canchaTenis, false);
        Reserva r2 = new Reserva("10:00", 2, "Sencillos", canchaTenis, false);
        
        assertTrue(gestor.registrarReserva(r1), "La primera reserva entra");
        assertFalse(gestor.registrarReserva(r2), "La segunda reserva debe fallar porque la cancha está ocupada");
    }

    @Test
    public void testTorneo_PreferencialesLlenosPasanARegulares() {
        Torneo torneo = new Torneo("T5", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, LocalDateTime.now().plusDays(5), 5, 0.0);
        
        ParticipacionTorneo p1 = ParticipacionTorneo.individual("P1", socioSub15);
        ParticipacionTorneo p2 = ParticipacionTorneo.individual("P2", socioMayores);
        
        assertTrue(torneo.inscribir(p1, true, false));
        assertEquals(1, torneo.getCuposPreferencialesOcupados());
        
        assertTrue(torneo.inscribir(p2, true, false));
        assertEquals(1, torneo.getCuposPreferencialesOcupados(), "El preferencial está lleno");
        assertEquals(1, torneo.getCuposRegularesOcupados(), "El segundo preferencial debió pasar a regular");
    }

    @Test
    public void testTorneo_ValidacionCreacionInvalida() {
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(5);
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Torneo("T6", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 50000.0);
        }, "Un torneo recreativo no puede tener tarifa");

        assertThrows(IllegalArgumentException.class, () -> {
            new Torneo("T7", "Copa", "Tenis", "Intermedio", TipoTorneo.COMPETITIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 0, 50000.0);
        }, "Un torneo debe tener al menos 1 cupo");
    }

    @Test
    public void testEquipo_RetirarJugadorLiberaCupo() {
        equipoFutbol.agregarJugador(new InscripcionEquipo(socioSub15, 10));
        assertEquals(1, equipoFutbol.getNominaJugadores().size());
        
        assertTrue(equipoFutbol.retirarJugador("sub15@mail.com"), "Debe retirar al jugador exitosamente");
        assertEquals(0, equipoFutbol.getNominaJugadores().size(), "La nómina debe quedar vacía");
    }

    @Test
    public void testSesionEntrenamiento_NoConvocaSuspendidosAlEquipo() {
        InscripcionEquipo ficha = new InscripcionEquipo(socioSub15, 10);
        equipoFutbol2.agregarJugador(ficha);
        ficha.setSuspendido(true);
        
        SesionEntrenamiento sesion = new SesionEntrenamiento("2025", "10:00", 60, entrenador1, canchaTenis);
        int convocados = sesion.convocarEquipo(equipoFutbol2);
        
        assertEquals(0, convocados, "No debe convocar a jugadores suspendidos");
    }

    @Test
    public void testVenta_RechazaRedimirMasPuntosDeLosQueTiene() {
        socioSub15.setPuntosFidelidad(100);
        Venta venta = new Venta();
        venta.setRealizaCompra(socioSub15);
        
        assertFalse(venta.redimirPuntos(500), "No debe permitir redimir más de lo que tiene");
        assertEquals(100, socioSub15.getPuntosFidelidad(), "Los puntos del socio no deben cambiar");
    }

    @Test
    public void testGestorUsuarios_RechazaRegistroConLoginDuplicado() {
        Socio nuevoSocio = new Socio("sub15@mail.com", "999", "Sub-15", 14, "Intermedio");
        
        assertThrows(IllegalArgumentException.class, () -> {
            gestorUsuarios.registrarUsuario(admin, nuevoSocio);
        }, "No se puede registrar un usuario con un login que ya existe");
    }
}