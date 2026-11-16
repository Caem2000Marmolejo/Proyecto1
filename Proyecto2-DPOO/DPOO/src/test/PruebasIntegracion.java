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
import ventas.BonoDescuento;
import ventas.GestorTurnos;
import ventas.Snack;
import ventas.SolicitudCambioTurno;
import ventas.SugerenciaEmpleado;
import ventas.Turno;
import ventas.Venta;
import deportes.Partido;

public class PruebasIntegracion {

    private Socio socio1;
    private Socio socio2;
    private Administrador admin;
    private Entrenador entrenador;
    private Fisioterapeuta fisio;
    private GestorUsuarios gestorUsuarios;
    private GestorTurnos gestorTurnos;
    private GestorTorneos gestorTorneos;

    @BeforeEach
    public void setUp() {
        socio1 = new Socio("s1@mail.com", "123", "Mayores", 20, "Intermedio");
        socio2 = new Socio("s2@mail.com", "123", "Mayores", 20, "Intermedio");
        admin = new Administrador("admin", "admin123");
        entrenador = new Entrenador("profe@mail.com", "123", "DESC-1");
        fisio = new Fisioterapeuta("fisio@mail.com", "123", "DESC-2");
        
        List<Usuario> listaUsuarios = new ArrayList<>(Arrays.asList(socio1, socio2, admin, entrenador, fisio));
        gestorUsuarios = new GestorUsuarios(listaUsuarios);
        gestorUsuarios.autenticar("admin", "admin123");
        
        gestorTurnos = new GestorTurnos();
        gestorTorneos = new GestorTorneos(gestorUsuarios);
    }

    

    @Test
    public void H1_Admin_CrearTorneoConCuposPreferencialesYSimultaneos() {
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(5);
        Torneo t1 = gestorTorneos.crearTorneo(admin, "T1", "Copa Recreativa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 0.0);
        
        assertEquals(2, t1.getCuposPreferencialesTotales(), "El 20% de 10 es 2");
        assertEquals(8, t1.getCuposRegularesTotales());
        
        Torneo t2 = gestorTorneos.crearTorneo(admin, "T2", "Copa Competitiva", "Tenis", "Intermedio", TipoTorneo.COMPETITIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 50000.0);
        assertNotNull(t2, "Pueden existir múltiples torneos simultáneos");
        
        assertThrows(IllegalArgumentException.class, () -> {
            gestorTorneos.crearTorneo(admin, "T3", "Copa Error", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 1000.0);
        }, "Rechaza recreativo con tarifa > 0");
        
        assertThrows(SecurityException.class, () -> {
            gestorTorneos.crearTorneo(socio1, "T4", "Copa Error", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 0.0);
        }, "Rechaza porque el socio no es administrador");
    }

    @Test
    public void H2_Admin_LiberarCuposPreferencialesEIniciarTorneo() {
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(5);
        Torneo torneo = gestorTorneos.crearTorneo(admin, "T5", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 0.0);
        
        assertFalse(torneo.iniciarTorneo(), "No se puede iniciar sin al menos 2 inscritos");
        
        ParticipacionTorneo p1 = ParticipacionTorneo.individual("P1", socio1);
        ParticipacionTorneo p2 = ParticipacionTorneo.individual("P2", socio2);
        gestorTorneos.inscribir(torneo, p1, gestorTurnos);
        gestorTorneos.inscribir(torneo, p2, gestorTurnos);
        
        torneo.liberarCuposPreferenciales();
        assertEquals(0, torneo.getCuposPreferencialesTotales() - torneo.getCuposPreferencialesOcupados(), "Cupos preferenciales vacantes pasan a regulares");
        
        assertTrue(torneo.iniciarTorneo(), "Con 2 inscritos, el torneo inicia");
        assertEquals("EN_CURSO", torneo.getEstado().toString());
        
        assertFalse(torneo.retirar(p1), "No se puede retirar si el torneo ya inició");
    }

    @Test
    public void H3_Admin_RegistrarResultadosYOtotgarPremios() {
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(5);
        Torneo torneo = gestorTorneos.crearTorneo(admin, "T6", "Copa", "Tenis", "Intermedio", TipoTorneo.COMPETITIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 50000.0);
        
        ParticipacionTorneo pSocio = ParticipacionTorneo.individual("P1", socio1);
        ParticipacionTorneo pEmpleado = ParticipacionTorneo.individual("P2", entrenador);
        
        gestorTorneos.inscribir(torneo, pSocio, gestorTurnos);
        gestorTorneos.inscribir(torneo, pEmpleado, gestorTurnos);
        torneo.iniciarTorneo();
        
        Partido partido = new PartidoInterno("2025", "2-1", "Tenis");
        torneo.registrarEncuentro(partido);
        
        torneo.finalizarTorneo();
        gestorTorneos.otorgarPremio(torneo, pEmpleado);
        
        assertEquals(pEmpleado, torneo.getGanador(), "Empleado queda registrado como campeón");
        assertEquals(50000.0, torneo.getPozoRecaudado(), 0.01, "El empleado no pagó, solo el socio");
    }

    @Test
    public void H4_Socio_InscripcionEnTorneoValidandoCuposYModalidades() {
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(5);
        Torneo torneo = gestorTorneos.crearTorneo(admin, "T7", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 0.0);
        
        ParticipacionTorneo p1 = ParticipacionTorneo.individual("P1", socio1);
        assertTrue(gestorTorneos.inscribir(torneo, p1, gestorTurnos), "Inscripción exitosa");
        assertFalse(gestorTorneos.inscribir(torneo, p1, gestorTurnos), "Rechaza si ya está inscrito");
        
        ParticipacionTorneo p2 = ParticipacionTorneo.individual("P2", socio2);
        assertTrue(gestorTorneos.inscribir(torneo, p2, gestorTurnos), "Inscripción exitosa del segundo");
        torneo.iniciarTorneo();
        
        Socio socio3 = new Socio("s3@mail.com", "123", "Mayores", 20, "Intermedio");
        ParticipacionTorneo p3 = ParticipacionTorneo.individual("P3", socio3);
        assertFalse(torneo.inscribir(p3, false, false), "Rechaza si el torneo ya inició");
    }

    @Test
    public void H5_Socio_SalirseDeTorneoYLiberarCupo() {
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(5);
        Torneo torneo = gestorTorneos.crearTorneo(admin, "T8", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 0.0);
        
        ParticipacionTorneo p1 = ParticipacionTorneo.individual("P1", socio1);
        gestorTorneos.inscribir(torneo, p1, gestorTurnos);
        assertEquals(1, torneo.getCuposRegularesOcupados() + torneo.getCuposPreferencialesOcupados());
        
        assertTrue(gestorTorneos.retirar(torneo, p1), "Se puede retirar antes de iniciar");
        assertEquals(0, torneo.getCuposRegularesOcupados() + torneo.getCuposPreferencialesOcupados(), "El cupo se libera");
    }

    @Test
    public void H6_Socio_ConsultarHistorialDeTorneo() {
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(5);
        Torneo torneo = gestorTorneos.crearTorneo(admin, "T9", "Copa Historial", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 0.0);
        assertEquals("INSCRIPCIONES_ABIERTAS", torneo.getEstado().toString());
        
        ParticipacionTorneo p1 = ParticipacionTorneo.individual("P1", socio1);
        ParticipacionTorneo p2 = ParticipacionTorneo.individual("P2", socio2);
        gestorTorneos.inscribir(torneo, p1, gestorTurnos);
        gestorTorneos.inscribir(torneo, p2, gestorTurnos);
        
        torneo.iniciarTorneo();
        assertEquals("EN_CURSO", torneo.getEstado().toString());
        torneo.finalizarTorneo();
        assertEquals("FINALIZADO", torneo.getEstado().toString());
        
        assertNull(gestorTorneos.buscarTorneo("ID-FALSO"), "Retorna null si no existe");
    }

    @Test
    public void H7_Socio_UsarBonoSinAcumularDescuentos() {
        socio1.agregarBono(new BonoDescuento(20000));
        
        Venta venta1 = new Venta();
        venta1.setRealizaCompra(socio1);
        venta1.agregarArticulo(new ArticuloTienda("Raqueta", "Dep", 100000, 10, "B1"));
        assertTrue(venta1.usarBonoDescuento(), "Usa bono si no hay otros descuentos");
        
        Venta venta2 = new Venta();
        venta2.setRealizaCompra(socio1);
        venta2.agregarArticulo(new ArticuloTienda("Raqueta", "Dep", 100000, 10, "B1"));
        venta2.validarCodigoEmpleado("DESC-1", entrenador);
        assertFalse(venta2.usarBonoDescuento(), "Rechaza bono si ya tiene código de empleado");
    }

    @Test
    public void H8_Empleado_InscripcionGratisSinTurno() {
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(5);
        String diaTorneo = fechaFutura.getDayOfWeek().toString();
        
        Torneo torneo = gestorTorneos.crearTorneo(admin, "T10", "Copa", "Tenis", "Avanzado", TipoTorneo.COMPETITIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 50000.0);
        ParticipacionTorneo p = ParticipacionTorneo.individual("P1", entrenador);
        
        gestorTurnos.asignarTurno(entrenador, new Turno(diaTorneo, "Mañana"));
        assertFalse(gestorTorneos.inscribir(torneo, p, gestorTurnos), "Rechaza por tener turno ese día");
        
        Entrenador entrenador2 = new Entrenador("profe2@mail.com", "123", "DESC-2");
        ParticipacionTorneo p2 = ParticipacionTorneo.individual("P2", entrenador2);
        
        GestorTurnos gestorTurnosVacio = new GestorTurnos();
        assertTrue(gestorTorneos.inscribir(torneo, p2, gestorTurnosVacio), "Inscripción exitosa si no tiene turno");
        assertEquals(0.0, torneo.getPozoRecaudado(), "Empleado no paga tarifa");
    }

 
    
    
    
    @Test
    public void RF_S01_AutenticacionSocio() {
        assertNotNull(gestorUsuarios.autenticar("s1@mail.com", "123"));
    }

    @Test
    public void RF_S02_InscripcionAcademiaIndividual() {
        assertEquals(1000, socio1.getRanking("Tenis"), "Ranking base ELO");
        socio1.setRanking("Tenis", 1020);
        assertEquals(1020, socio1.getRanking("Tenis"));
    }

    @Test
    public void RF_S03_ReservaInstalacionDeportiva() {
        Instalacion cancha = new Instalacion("TEN-1", "Tenis", 4, false);
        GestorReservas gestor = new GestorReservas(2, 2, 2);
        Reserva r = new Reserva("10:00", 2, "Sencillos", cancha, false);
        assertTrue(gestor.registrarReserva(r));
    }

    @Test
    public void RF_S04_ProgramacionPartidoPractica() {
        PartidoPractica partido = new PartidoPractica("2025", "Tenis", socio1, socio2);
        assertTrue(partido.validarNivelesAdyacentes());
    }

    @Test
    public void RF_S05_ControlSeguridadBebidaCaliente() {
        Instalacion padel = new Instalacion("PAD-1", "Padel", 4, false);
        Reserva r = new Reserva("10:00", 4, "Dobles", padel, true);
        r.agregarParticipante(socio1);
        
        Venta v = new Venta();
        v.setRealizaCompra(socio1);
        v.agregarBebida(new Bebida("Cafe", 5000, true));
        
        assertFalse(v.verificarRestriccionSeguridadBebidas(Arrays.asList(r)));
    }

    @Test
    public void RF_S06_ConsultaRankingELO() {
        assertEquals(1000, socio1.getRanking("Tenis"));
    }

    @Test
    public void RF_S07_HistorialPartidosPractica() {
        PartidoPractica partido = new PartidoPractica("2025", "Tenis", socio1, socio2);
        assertNotNull(partido);
    }

    @Test
    public void RF_S08_ConsultaConvocatorias() {
        SesionEntrenamiento sesion = new SesionEntrenamiento("2025", "10:00", 60, entrenador, new Instalacion("T1", "Tenis", 4, false));
        sesion.convocarJugador(socio1);
        assertTrue(sesion.estaConvocado(socio1));
    }

    @Test
    public void RF_S09_ConsultaAsistenciasEvaluaciones() {
        SesionEntrenamiento sesion = new SesionEntrenamiento("2025", "10:00", 60, entrenador, new Instalacion("T1", "Tenis", 4, false));
        sesion.convocarJugador(socio1);
        sesion.registrarAsistencia(socio1, "Asistio", 5, "Excelente");
        
        AnaliticaDesempeno analitica = new AnaliticaDesempeno();
        analitica.agregarRegistro(sesion.getGeneraRegistros().get(0));
        
        assertEquals(1, analitica.contarAsistencias(socio1));
    }

    @Test
    public void RF_S10_ConsultaEstadisticasEquipo() {
        Equipo equipo = new Equipo("Futbol FC", "Futbol", "Mayores", 15, 7);
        equipo.agregarJugador(new InscripcionEquipo(socio1, 10));
        assertEquals(1, equipo.getNominaJugadores().size());
    }

    @Test
    public void RF_S11_ConsultaTablaPosicionesFases() {
        Equipo equipo = new Equipo("A", "Futbol", "Mayores", 15, 7);
        
        for (int i = 0; i < 7; i++) {
            Socio s = new Socio("s" + i + "@mail.com", "123", "Mayores", 20, "Intermedio");
            equipo.agregarJugador(new InscripcionEquipo(s, i + 1));
        }
        
        Competencia competencia = new Competencia("ASCUN", "ASCUN", "2025", "Futbol", "Mayores", "Fases", 7);
        competencia.inscribirEquipo(equipo);
        
        assertEquals(1, competencia.getEquiposInscritos().size());
    }

    @Test
    public void RF_S12_ConsultaGoleadores() {
        AcumuladorEstadisticas acumulador = new AcumuladorEstadisticas();
        EstadisticaFutbol est = new EstadisticaFutbol(socio1, 2, 0, 0, 0);
        acumulador.acumularGoles(est.getSocio(), est.getGoles());
        assertEquals(2, acumulador.getGoles("s1@mail.com"));
    }

    @Test
    public void RF_S13_CompraArticulosTienda() {
        Venta venta = new Venta();
        venta.setRealizaCompra(socio1);
        venta.agregarArticulo(new ArticuloTienda("Balón", "Dep", 100000, 10, "B1"));
        assertTrue(venta.calcularTotal(-1) > 0);
    }

    @Test
    public void RF_S14_CompraCafeteriaAvisosPropina() {
        Venta venta = new Venta();
        venta.setRealizaCompra(socio1);
        venta.agregarBebida(new Bebida("Jugo", 5000, false));
        venta.agregarSnack(new Snack("Papas", 2000, Arrays.asList("Gluten")));
        assertFalse(venta.obtenerAvisoAlergenos().isEmpty());
    }

    @Test
    public void RF_S15_AcumulacionPuntosFidelidad() {
        Venta venta = new Venta();
        venta.setRealizaCompra(socio1);
        venta.agregarArticulo(new ArticuloTienda("Balón", "Dep", 100000, 10, "B1"));
        venta.procesarPago(-1);
        assertTrue(socio1.getPuntosFidelidad() > 0);
    }

    @Test
    public void RF_S16_RedencionPuntosFidelidad() {
        socio1.setPuntosFidelidad(500);
        Venta venta = new Venta();
        venta.setRealizaCompra(socio1);
        venta.agregarArticulo(new ArticuloTienda("Medias", "Dep", 1000, 5, "B1"));
        assertTrue(venta.redimirPuntos(500));
    }

    @Test
    public void RF_S17_UsoCodigoDescuentoEmpleado() {
        Venta venta = new Venta();
        venta.setRealizaCompra(socio1);
        venta.agregarArticulo(new ArticuloTienda("Balón", "Dep", 100000, 10, "B1"));
        assertTrue(venta.validarCodigoEmpleado("DESC-1", entrenador));
    }

    // --- ROL EMPLEADO ---

    @Test
    public void RF_E01_AutenticacionEmpleado() {
        assertNotNull(gestorUsuarios.autenticar("profe@mail.com", "123"));
    }

    @Test
    public void RF_E02_ConsultaTurnosSemanales() {
        Turno manana = new Turno("Lunes", "Mañana");
        gestorTurnos.asignarTurno(entrenador, manana);
        assertEquals(1, entrenador.getAsignados().size());
    }

    @Test
    public void RF_E03_SolicitudCambioTurno() {
        Turno manana = new Turno("Lunes", "Mañana");
        Turno tarde = new Turno("Lunes", "Tarde");
        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(entrenador, manana, tarde);
        assertEquals("PENDIENTE", solicitud.getEstadoAprobacion());
    }

    @Test
    public void RF_E04_ConsultaEstadoSolicitudes() {
        Turno manana = new Turno("Lunes", "Mañana");
        Turno tarde = new Turno("Lunes", "Tarde");
        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(entrenador, manana, tarde);
        assertEquals("PENDIENTE", solicitud.getEstadoAprobacion());
    }

    @Test
    public void RF_E05_SugerenciaNuevoArticulo() {
        SugerenciaEmpleado sug = new SugerenciaEmpleado("Snack nuevo", entrenador);
        assertEquals("PENDIENTE", sug.getEstado());
    }

    @Test
    public void RF_E06_CompraCodigoPropioEmpleado() {
        Venta venta = new Venta();
        venta.setRealizaCompra(entrenador);
        venta.agregarArticulo(new ArticuloTienda("Balón", "Dep", 100000, 10, "B1"));
        assertTrue(venta.calcularTotal(-1) > 0);
    }

    // --- ROL ENTRENADOR ---

    @Test
    public void RF_T01_PlaneacionSesionEntrenamiento() {
        SesionEntrenamiento sesion = new SesionEntrenamiento("2025", "10:00", 60, entrenador, new Instalacion("T1", "Tenis", 4, false));
        sesion.convocarJugador(socio1);
        assertEquals(1, sesion.getListaJugadoresConvocados().size());
    }

    @Test
    public void RF_T02_RegistroAsistenciaSesion() {
        SesionEntrenamiento sesion = new SesionEntrenamiento("2025", "10:00", 60, entrenador, new Instalacion("T1", "Tenis", 4, false));
        sesion.convocarJugador(socio1);
        assertNotNull(sesion.registrarAsistencia(socio1, "Asistio", 5, "Bien"));
    }

    @Test
    public void RF_T03_RegistroEvaluacionDesempeno() {
        SesionEntrenamiento sesion = new SesionEntrenamiento("2025", "10:00", 60, entrenador, new Instalacion("T1", "Tenis", 4, false));
        sesion.convocarJugador(socio1);
        assertNotNull(sesion.registrarAsistencia(socio1, "Asistio", 5, "Bien"));
    }

    @Test
    public void RF_T04_ProgramacionPartidoInterno() {
        Equipo equipoA = new Equipo("A", "Futbol", "Mayores", 15, 7);
        Equipo equipoB = new Equipo("B", "Futbol", "Mayores", 15, 7);
        PartidoInterno partido = new PartidoInterno("2025", "0-0", "Futbol");
        partido.agregarContrincante(equipoA);
        partido.agregarContrincante(equipoB);
        assertFalse(partido.validarParaDisputarse()); 
    }

    @Test
    public void RF_T05_DefinicionAlineacionPartido() {
        Equipo equipo = new Equipo("A", "Futbol", "Mayores", 15, 7);
        InscripcionEquipo ficha = new InscripcionEquipo(socio1, 10);
        equipo.agregarJugador(ficha);
        assertFalse(ficha.isSuspendido());
    }

    @Test
    public void RF_T06_RegistroResultadoPartidoInterno() {
        PartidoInterno partido = new PartidoInterno("2025", "2-1", "Futbol");
        assertEquals("2-1", partido.getMarcadorFinal());
    }

    @Test
    public void RF_T07_RegistroEstadisticasIndividuales() {
        EstadisticaFutbol est = new EstadisticaFutbol(socio1, 2, 1, 0, 0);
        assertEquals(2, est.getGoles());
    }

    @Test
    public void RF_T08_RegistroTarjetasSuspensiones() {
        InscripcionEquipo ficha = new InscripcionEquipo(socio1, 10);
        EstadisticaFutbol roja = new EstadisticaFutbol(socio1, 0, 0, 0, 1);
        roja.procesarSanciones(ficha);
        assertTrue(ficha.isSuspendido());
    }

    @Test
    public void RF_T09_RegistroResultadoCompetencia() {
        Equipo equipo = new Equipo("A", "Futbol", "Mayores", 15, 7);
        PartidoCompetencia partido = new PartidoCompetencia("2025", "", "Rival", "Estadio", "Fase 1", equipo);
        partido.setMarcador(2, 1);
        assertEquals(3, partido.calcularPuntos());
    }

    @Test
    public void RF_T10_RegistroResultadoELO() {
        PartidoPractica partido = new PartidoPractica("2025", "Tenis", socio1, socio2);
        int eloAntes = socio1.getRanking("Tenis");
        partido.actualizarRanking(socio1, socio2);
        assertEquals(eloAntes + 20, socio1.getRanking("Tenis"));
    }

    // --- ROL ADMINISTRADOR ---

    @Test
    public void RF_A01_AutenticacionAdministrador() {
        assertNotNull(gestorUsuarios.autenticar("admin", "admin123"));
    }

    @Test
    public void RF_A02_RegistroNuevoSocio() {
        Socio nuevo = new Socio("nuevo@mail.com", "123", "Sub-15", 14, "Intermedio");
        assertTrue(admin.crearJugador(new ArrayList<>(), nuevo));
    }

    @Test
    public void RF_A03_CreacionEquipo() {
        Equipo nuevoEquipo = new Equipo("Nuevo", "Futbol", "Sub-15", 15, 7);
        assertTrue(admin.crearEquipo(new ArrayList<>(), nuevoEquipo));
    }

    @Test
    public void RF_A04_InscripcionSocioEquipo() {
        Equipo equipo = new Equipo("A", "Futbol", "Mayores", 15, 7);
        GestorEquipos gestor = new GestorEquipos(Arrays.asList(equipo));
        assertTrue(gestor.inscribirJugador(equipo, new InscripcionEquipo(socio1, 10)));
    }

    @Test
    public void RF_A05_BajaJugadorEquipo() {
        Equipo equipo = new Equipo("A", "Futbol", "Mayores", 15, 7);
        equipo.agregarJugador(new InscripcionEquipo(socio1, 10));
        assertTrue(equipo.retirarJugador("s1@mail.com"));
    }

    @Test
    public void RF_A06_BajaEquipo() {
        List<Equipo> equipos = new ArrayList<>();
        Equipo nuevoEquipo = new Equipo("Nuevo", "Futbol", "Sub-15", 15, 7);
        equipos.add(nuevoEquipo);
        assertTrue(admin.darDeBajaEquipo(equipos, "Nuevo"));
    }

    @Test
    public void RF_A07_ConsultaEstadoInventario() {
        List<Instalacion> instalaciones = new ArrayList<>();
        List<ArticuloTienda> articulos = new ArrayList<>();
        assertDoesNotThrow(() -> admin.verInventario(instalaciones, articulos));
    }

    @Test
    public void RF_A08_MovimientoArticulosUbicaciones() {
        ArticuloTienda art = new ArticuloTienda("Raqueta", "Dep", 100000, 5, "B1");
        admin.moverArticuloUbicacion(art, "Bodega 2");
        assertEquals("Bodega 2", art.getUbicacionInventario());
    }

    @Test
    public void RF_A09_ReabastecimientoTienda() {
        ArticuloTienda art = new ArticuloTienda("Raqueta", "Dep", 100000, 5, "B1");
        admin.reabastecerTienda(art, 10);
        assertEquals(15, art.getCantidadInventario());
    }

    @Test
    public void RF_A10_AprobacionSugerenciasEmpleados() {
        SugerenciaEmpleado sug = new SugerenciaEmpleado("Snack de proteína", entrenador);
        admin.aprobarSugerencia(sug, new ArrayList<>(), new ArrayList<>());
        assertEquals("APROBADA", sug.getEstado());
    }

    @Test
    public void RF_A11_ProgramacionTurnosSemanales() {
        Turno manana = new Turno("Lunes", "Mañana");
        Entrenador e2 = new Entrenador("p2@mail.com", "123", "DESC-3");
        gestorTurnos.asignarTurno(entrenador, manana);
        gestorTurnos.asignarTurno(e2, manana);
        gestorTurnos.asignarTurno(fisio, manana);
        assertTrue(gestorTurnos.cumpleMinimo(manana));
    }

    @Test
    public void RF_A12_ResolucionSolicitudesCambioTurno() {
        Turno manana = new Turno("Lunes", "Mañana");
        Turno tarde = new Turno("Lunes", "Tarde");
        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(entrenador, manana, tarde);
        assertFalse(gestorTurnos.aprobarCambio(solicitud)); 
    }

    @Test
    public void RF_A13_ConsultaHistorialGeneralOperacion() {
        assertDoesNotThrow(() -> {
            admin.verHistorialReservas(new ArrayList<>());
            admin.verHistorialPartidos(new ArrayList<>());
            admin.verHistorialSesiones(new ArrayList<>());
        });
    }

    @Test
    public void RF_A14_ConsultaReportesVentas() {
        assertDoesNotThrow(() -> admin.verReporteVentas(new ArrayList<>(), "Tienda", "DIARIO", java.time.LocalDate.now()));
    }

    @Test
    public void RF_A15_ConsultaReportesDeportivos() {
        assertDoesNotThrow(() -> admin.verReporteDeportivo(Arrays.asList(socio1), new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    public void RF_A17_RegistroCompetenciaExterna() {
        Competencia competencia = new Competencia("Cerros", "Uni", "2025", "Futbol", "Mayores", "Liga", 7);
        assertNotNull(competencia);
    }

    @Test
    public void RF_A18_InscripcionEquipoCompetencia() {
        Competencia competencia = new Competencia("Cerros", "Uni", "2025", "Futbol", "Mayores", "Liga", 7);
        Equipo equipo = new Equipo("A", "Futbol", "Mayores", 15, 7);
        
        for (int i = 0; i < 7; i++) {
            Socio s = new Socio("a" + i + "@mail.com", "123", "Mayores", 20, "Intermedio");
            equipo.agregarJugador(new InscripcionEquipo(s, i + 1));
        }
        
        assertTrue(competencia.inscribirEquipo(equipo));
    }

    @Test
    public void RF_A19_ProgramacionPartidoOficialCompetencia() {
        Equipo equipo = new Equipo("A", "Futbol", "Mayores", 15, 7);
        PartidoCompetencia partido = new PartidoCompetencia("2025", "", "Rival", "Estadio", "Fase 1", equipo);
        assertNotNull(partido);
    }

    @Test
    public void RF_A20_RestriccionUsoPersonalAdministrador() {
        assertFalse(admin.puedeReservar());
        assertFalse(admin.puedeInscribirse());
    }

    // --- NUEVOS REQUERIMIENTOS PROYECTO 2 ---

    @Test
    public void RF_P2_01_CreacionTorneosInternos() {
        Torneo t1 = gestorTorneos.crearTorneo(admin, "T1", "Copa Recreativa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, LocalDateTime.now().plusDays(5), 10, 0.0);
        assertEquals(2, t1.getCuposPreferencialesTotales());
    }

    @Test
    public void RF_P2_02_LiberacionCuposInicioTorneo() {
        Torneo t1 = gestorTorneos.crearTorneo(admin, "T2", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, LocalDateTime.now().plusDays(5), 10, 0.0);
        ParticipacionTorneo p1 = ParticipacionTorneo.individual("P1", socio1);
        ParticipacionTorneo p2 = ParticipacionTorneo.individual("P2", socio2);
        gestorTorneos.inscribir(t1, p1, gestorTurnos);
        gestorTorneos.inscribir(t1, p2, gestorTurnos);
        assertTrue(t1.iniciarTorneo());
    }

    @Test
    public void RF_P2_03_RegistroEncuentrosFinalizacionPremiacion() {
        Torneo t1 = gestorTorneos.crearTorneo(admin, "T3", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, LocalDateTime.now().plusDays(5), 10, 0.0);
        ParticipacionTorneo p1 = ParticipacionTorneo.individual("P1", socio1);
        ParticipacionTorneo p2 = ParticipacionTorneo.individual("P2", socio2);
        gestorTorneos.inscribir(t1, p1, gestorTurnos);
        gestorTorneos.inscribir(t1, p2, gestorTurnos);
        t1.iniciarTorneo();
        t1.registrarEncuentro(new PartidoInterno("2025", "2-1", "Tenis"));
        t1.finalizarTorneo();
        gestorTorneos.otorgarPremio(t1, p1);
        assertNotNull(socio1.obtenerBonoDisponible());
    }

    @Test
    public void RF_P2_04_InscripcionSociosEstudiantesExternos() {
        Torneo t1 = gestorTorneos.crearTorneo(admin, "T4", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, LocalDateTime.now().plusDays(5), 10, 0.0);
        ParticipacionTorneo p1 = ParticipacionTorneo.individual("P1", socio1);
        assertTrue(gestorTorneos.inscribir(t1, p1, gestorTurnos));
    }

    @Test
    public void RF_P2_05_DesinscripcionLibreLiberacionCupos() {
        Torneo t1 = gestorTorneos.crearTorneo(admin, "T5", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, LocalDateTime.now().plusDays(5), 10, 0.0);
        ParticipacionTorneo p1 = ParticipacionTorneo.individual("P1", socio1);
        gestorTorneos.inscribir(t1, p1, gestorTurnos);
        assertTrue(gestorTorneos.retirar(t1, p1));
    }

    @Test
    public void RF_P2_06_ConsultaEstadoHistorialTorneos() {
        Torneo t1 = gestorTorneos.crearTorneo(admin, "T6", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, LocalDateTime.now().plusDays(5), 10, 0.0);
        assertEquals("INSCRIPCIONES_ABIERTAS", t1.getEstado().toString());
    }

    @Test
    public void RF_P2_07_AlmacenamientoRedencionBono() {
        socio1.agregarBono(new BonoDescuento(20000));
        Venta venta = new Venta();
        venta.setRealizaCompra(socio1);
        venta.agregarArticulo(new ArticuloTienda("Raqueta", "Dep", 100000, 10, "B1"));
        assertTrue(venta.usarBonoDescuento());
    }

    @Test
    public void RF_P2_08_InscripcionEmpleadosTorneosIndividuales() {
        Torneo t1 = gestorTorneos.crearTorneo(admin, "T7", "Copa", "Tenis", "Avanzado", TipoTorneo.COMPETITIVO, ModalidadTorneo.INDIVIDUAL, LocalDateTime.now().plusDays(5), 10, 50000.0);
        ParticipacionTorneo p1 = ParticipacionTorneo.individual("P1", entrenador);
        assertTrue(gestorTorneos.inscribir(t1, p1, gestorTurnos));
    }
    
    @Test
    public void RF_A16_RegistroPagoMensualidad() {
        List<Venta> ventas = new ArrayList<>();
        double monto = 150000.0;
        String periodo = "Mayo 2025";
        
        admin.registrarPagoMensualidad(socio1, monto, periodo, ventas);
        
        assertEquals(1, ventas.size(), "Debe registrarse exactamente una venta");
        
        Venta ventaGenerada = ventas.get(0);
        assertEquals(socio1, ventaGenerada.getRealizaCompra(), "La venta debe estar a nombre del socio");
        assertTrue(ventaGenerada.getDiscriminacionRubros().contains("Mensualidades"), "El rubro debe ser Mensualidades");
        assertTrue(ventaGenerada.getDiscriminacionRubros().contains("150000"), "El monto debe estar registrado en el rubro");
    }
    
    @Test
    public void HU_A_RechazosCreacionDeTorneos() {
        LocalDateTime fechaPasada = LocalDateTime.now().minusDays(1);
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(5);
        
        assertThrows(IllegalArgumentException.class, () -> {
            gestorTorneos.crearTorneo(admin, "T-ERROR1", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, fechaPasada, 10, 0.0);
        }, "Rechaza fecha pasada");

        assertThrows(IllegalArgumentException.class, () -> {
            gestorTorneos.crearTorneo(admin, "T-ERROR2", "Copa", "Tenis", "Intermedio", TipoTorneo.COMPETITIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 0.0);
        }, "Rechaza competitivo con tarifa 0");
        
        assertThrows(SecurityException.class, () -> {
            gestorTorneos.crearTorneo(socio1, "T-ERROR3", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, fechaFutura, 10, 0.0);
        }, "Rechaza si no es administrador");
    }

    @Test
    public void HU_S_RechazosInscripcionYRetiroEnTorneos() {
        Torneo torneo = gestorTorneos.crearTorneo(admin, "T-INS", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, LocalDateTime.now().plusDays(5), 10, 0.0);
        
        ParticipacionTorneo p1 = ParticipacionTorneo.individual("P1", socio1);
        ParticipacionTorneo p2 = ParticipacionTorneo.individual("P2", socio2);
        
        gestorTorneos.inscribir(torneo, p1, gestorTurnos);
        gestorTorneos.inscribir(torneo, p2, gestorTurnos);
        torneo.iniciarTorneo();
        
        assertFalse(gestorTorneos.retirar(torneo, p1), "No se puede retirar si el torneo está EN_CURSO");
        
        Socio externo = new Socio("ext@mail.com", "123", "Mayores", 20, "Intermedio");
        ParticipacionTorneo p3 = ParticipacionTorneo.individual("P3", externo);
        assertFalse(torneo.inscribir(p3, false, false), "No se puede inscribir si ya inició");
    }

    @Test
    public void HU_E_RechazosEmpleadoEnTorneos() {
        gestorTorneos.crearTorneo(admin, "T-EQ", "Copa", "Futbol", "Mayores", TipoTorneo.RECREATIVO, ModalidadTorneo.EQUIPOS, LocalDateTime.now().plusDays(5), 10, 0.0);
        
        List<Usuario> equipoEmp = new ArrayList<>();
        equipoEmp.add(entrenador);
        equipoEmp.add(new Entrenador("p2@mail.com", "123", "DESC-2"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            ParticipacionTorneo.equipo("PE", "Equipo Empleados", "Futbol", "Mayores", equipoEmp);
        }, "El sistema debe rechazar la creación de un equipo con empleados");
        
        Torneo torneoInd = gestorTorneos.crearTorneo(admin, "T-IND", "Copa", "Tenis", "Intermedio", TipoTorneo.RECREATIVO, ModalidadTorneo.INDIVIDUAL, LocalDateTime.now().plusDays(5), 10, 0.0);
        String diaTorneo = torneoInd.getFechaInicio().getDayOfWeek().toString();
        
        gestorTurnos.asignarTurno(entrenador, new Turno(diaTorneo, "Mañana"));
        ParticipacionTorneo pIndividual = ParticipacionTorneo.individual("PI", entrenador);
        
        assertFalse(gestorTorneos.inscribir(torneoInd, pIndividual, gestorTurnos), "Empleado no puede inscribirse si está de turno");
    }
}