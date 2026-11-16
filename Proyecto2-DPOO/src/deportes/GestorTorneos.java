package deportes;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Usuarios.GestorUsuarios;
import Usuarios.Socio;
import Usuarios.Usuario;
import Usuarios.Empleado;
import ventas.GestorTurnos;
import ventas.Turno;
import ventas.BonoDescuento;

public class GestorTorneos {

    private final Map<String, Torneo> torneos = new LinkedHashMap<>();
    private final Clock reloj;
    private final GestorUsuarios usuarios;

    public GestorTorneos(GestorUsuarios usuarios) {
        this(usuarios, Clock.systemDefaultZone());
    }

    public GestorTorneos(GestorUsuarios usuarios, Clock reloj) {
        if (usuarios == null || reloj == null) {
            throw new IllegalArgumentException("El gestor de usuarios y el reloj son obligatorios.");
        }
        this.usuarios = usuarios;
        this.reloj = reloj;
    }

    public Torneo crearTorneo(Usuario solicitante, String id, String nombre, String deporte, 
            String categoria, TipoTorneo tipo, ModalidadTorneo modalidad, 
            LocalDateTime fechaInicio, int cuposTotales, double tarifaInscripcion) {

        usuarios.exigirAdministrador(solicitante);
        String clave = normalizarId(id);

        if (torneos.containsKey(clave)) {
            throw new IllegalArgumentException("Ya existe un torneo con ese identificador.");
        }
        if (fechaInicio == null || !fechaInicio.isAfter(LocalDateTime.now(reloj))) {
            throw new IllegalArgumentException("La fecha de inicio debe ser futura.");
        }

        Torneo torneo = new Torneo(id, nombre, deporte, categoria, tipo, modalidad, fechaInicio, cuposTotales, tarifaInscripcion);
        torneos.put(clave, torneo);
        return torneo;
    }

    public boolean inscribir(Torneo torneo, ParticipacionTorneo participacion, GestorTurnos gestorTurnos) {
        if (torneo == null || participacion == null) return false;

        for (ParticipacionTorneo inscrito : torneo.getInscritos()) {
            if (inscrito.getIntegrantes().get(0).getLogin().equals(participacion.getIntegrantes().get(0).getLogin())) {
                System.out.println("El jugador ya está inscrito en este torneo.");
                return false; 
            }
        }

        boolean esPreferencial = false;
        boolean eximirPago = false;

        List<Usuario> integrantes = participacion.getIntegrantes();
        if (!integrantes.isEmpty()) {
            Usuario primero = integrantes.get(0);

            if (primero instanceof Empleado) {
                if (participacion.getModalidad() != ModalidadTorneo.INDIVIDUAL) {
                    System.out.println("Empleado solo puede inscribirse en modalidad individual.");
                    return false;
                }
                if (tieneTurnoEseDia((Empleado) primero, torneo, gestorTurnos)) {
                    System.out.println("El empleado tiene turno el dia del torneo.");
                    return false;
                }
                if (torneo.getTipo() == TipoTorneo.COMPETITIVO) {
                    eximirPago = true;
                }
            } 
            
            int sumaRanking = 0;
            int cantidadSocios = 0;
            
            for (Usuario u : integrantes) {
                if (u instanceof Socio) {
                    sumaRanking += ((Socio) u).getRanking(torneo.getDeporte());
                    cantidadSocios++;
                }
            }
            
            if (cantidadSocios > 0) {
                int promedioRanking = sumaRanking / cantidadSocios;
                if (promedioRanking > 1000) {
                    esPreferencial = true;
                }
            }
        }

        return torneo.inscribir(participacion, esPreferencial, eximirPago);
    }

    public boolean retirar(Torneo torneo, ParticipacionTorneo participacion) {
        if (torneo == null || participacion == null) return false;
        return torneo.retirar(participacion);
    }

    private boolean tieneTurnoEseDia(Empleado emp, Torneo torneo, GestorTurnos gestorTurnos) {
        if (gestorTurnos == null) return false;
        
        String diaTorneo = torneo.getFechaInicio().getDayOfWeek().toString();
        for (Turno t : emp.getAsignados()) {
            if (t.getDia().equalsIgnoreCase(diaTorneo)) {
                return true;
            }
        }
        return false;
    }

    public Torneo buscarTorneo(String id) {
        return torneos.get(normalizarId(id));
    }

    public List<Torneo> getTorneos() {
        return Collections.unmodifiableList(new ArrayList<>(torneos.values()));
    }

    private static String normalizarId(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("El identificador es obligatorio.");
        return id.trim().toLowerCase(Locale.ROOT);
    }
    

    public void otorgarPremio(Torneo torneo, ParticipacionTorneo ganador) {
        if (torneo.getEstado() != EstadoTorneo.FINALIZADO) {
            System.out.println("El torneo aún no ha finalizado.");
            return;
        }
        torneo.setGanador(ganador); 
        
        List<Usuario> integrantes = ganador.getIntegrantes();
        int cantidadGanadores = integrantes.size();

        if (torneo.getTipo() == TipoTorneo.RECREATIVO) {
            for (Usuario u : integrantes) {
                if (u instanceof Socio) {
                    ((Socio) u).agregarBono(new BonoDescuento(50000)); 
                }
            }
            System.out.println("Premio entregado: Bono de descuento a los socios ganadores.");
        } 
        else if (torneo.getTipo() == TipoTorneo.COMPETITIVO) {
            double premioTotal = torneo.getPozoRecaudado() * 0.70;
            double premioPorPersona = premioTotal / cantidadGanadores;

            for (Usuario u : integrantes) {
                if (u instanceof Empleado) {
                    System.out.println("El empleado " + u.getLogin() + " no recibe premio en efectivo.");
                } else {
                    System.out.println("Premio en efectivo para " + u.getLogin() + ": $" + premioPorPersona);
                }
            }
        }
    }
}