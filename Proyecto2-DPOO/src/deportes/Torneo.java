package deportes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Torneo {
    private final String id;
    private final String nombre;
    private final String deporte;
    private final String categoria;
    private final TipoTorneo tipo;
    private final ModalidadTorneo modalidad;
    private final LocalDateTime fechaInicio;
    private final int cuposTotales;
    private final double tarifaInscripcion;
    
    private EstadoTorneo estado;
    private int cuposPreferencialesTotales;
    private int cuposRegularesTotales;
    private int cuposPreferencialesOcupados;
    private int cuposRegularesOcupados;
    private double pozoRecaudado;
    
    private List<ParticipacionTorneo> inscritos;
    private List<Partido> encuentros;
    private TablaPosiciones tablaPosiciones;

    public Torneo(String id, String nombre, String deporte, String categoria, TipoTorneo tipo, 
           ModalidadTorneo modalidad, LocalDateTime fechaInicio, int cuposTotales, double tarifaInscripcion) {
        this.id = validarTexto(id, "identificador");
        this.nombre = validarTexto(nombre, "nombre");
        this.deporte = validarTexto(deporte, "deporte");
        this.categoria = validarTexto(categoria, "categoria o nivel");
        
        if (tipo == null || modalidad == null || fechaInicio == null) {
            throw new IllegalArgumentException("Tipo, modalidad y fecha son obligatorios.");
        }
        if (cuposTotales <= 0) {
            throw new IllegalArgumentException("El torneo debe tener al menos un cupo.");
        }
        if (!Double.isFinite(tarifaInscripcion) || tarifaInscripcion < 0) {
            throw new IllegalArgumentException("La tarifa debe ser un numero no negativo.");
        }
        if (tipo == TipoTorneo.RECREATIVO && tarifaInscripcion != 0) {
            throw new IllegalArgumentException("Un torneo recreativo debe ser gratuito.");
        }
        if (tipo == TipoTorneo.COMPETITIVO && tarifaInscripcion <= 0) {
            throw new IllegalArgumentException("Un torneo competitivo debe tener una tarifa positiva.");
        }

        this.tipo = tipo;
        this.modalidad = modalidad;
        this.fechaInicio = fechaInicio;
        this.cuposTotales = cuposTotales;
        this.tarifaInscripcion = tarifaInscripcion;
        this.estado = EstadoTorneo.INSCRIPCIONES_ABIERTAS;
        
        this.cuposPreferencialesTotales = (int) Math.ceil(cuposTotales * 0.20);
        this.cuposRegularesTotales = cuposTotales - cuposPreferencialesTotales;
        this.cuposPreferencialesOcupados = 0;
        this.cuposRegularesOcupados = 0;
        this.pozoRecaudado = 0;
        
        this.inscritos = new ArrayList<>();
        this.encuentros = new ArrayList<>();
        this.tablaPosiciones = new TablaPosiciones(new ArrayList<>());
    }

    public void registrarEncuentro(Partido partido) {
        this.encuentros.add(partido);
    }

    public void configurarTablaPosiciones(List<Equipo> equiposParticipantes) {
        if (modalidad == ModalidadTorneo.EQUIPOS) {
            this.tablaPosiciones = new TablaPosiciones(equiposParticipantes);
        }
    }

    public boolean inscribir(ParticipacionTorneo participacion, boolean esPreferencial, boolean eximirPago) {
        if (estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS) return false;
        
        if (esPreferencial) {
            if (cuposPreferencialesOcupados < cuposPreferencialesTotales) {
                cuposPreferencialesOcupados++;
            } else if (cuposRegularesOcupados < cuposRegularesTotales) {
                cuposRegularesOcupados++;
            } else {
                return false;
            }
        } else {
            if (cuposRegularesOcupados < cuposRegularesTotales) {
                cuposRegularesOcupados++;
            } else if (cuposPreferencialesOcupados < cuposPreferencialesTotales) {
                cuposPreferencialesOcupados++;
            } else {
                return false;
            }
        }
        
        inscritos.add(participacion);
        if (!eximirPago) {
            pozoRecaudado += tarifaInscripcion;
        }
        return true;
    }

    public boolean retirar(ParticipacionTorneo participacion) {
        if (estado != EstadoTorneo.INSCRIPCIONES_ABIERTAS) return false;
        
        if (inscritos.remove(participacion)) {
            if (cuposPreferencialesOcupados > 0) cuposPreferencialesOcupados--; 
            else if (cuposRegularesOcupados > 0) cuposRegularesOcupados--;
            
            if (tipo == TipoTorneo.COMPETITIVO) {
                pozoRecaudado -= tarifaInscripcion;
            }
            return true;
        }
        return false;
    }

    public boolean iniciarTorneo() {
        if (estado == EstadoTorneo.INSCRIPCIONES_ABIERTAS && inscritos.size() >= 2) {
            estado = EstadoTorneo.EN_CURSO;
            return true;
        }
        return false;
    }

    public void liberarCuposPreferenciales() {
        if (estado == EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            int vacantes = cuposPreferencialesTotales - cuposPreferencialesOcupados;
            cuposRegularesTotales += vacantes;
            cuposPreferencialesTotales = cuposPreferencialesOcupados;
        }
    }
    
    public void finalizarTorneo() {
        if (estado == EstadoTorneo.EN_CURSO) estado = EstadoTorneo.FINALIZADO;
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("Debe indicar " + campo + ".");
        return valor.trim();
    }
    
    public void consultarHistorialDetallado() {
        System.out.println("=== HISTORIAL DEL TORNEO: " + this.nombre + " ===");
        System.out.println("Estado actual: " + this.estado);
        System.out.println("Tipo: " + this.tipo + " | Modalidad: " + this.modalidad);
        System.out.println("Cupos: " + (this.cuposPreferencialesOcupados + this.cuposRegularesOcupados) + "/" + this.cuposTotales);
        System.out.println("Recaudado: $" + this.pozoRecaudado);
        
        if (this.ganador != null) {
            System.out.println("Ganador declarado: " + this.ganador.getNombre());
        } else {
            System.out.println("Ganador: Aún no definido.");
        }

        System.out.println("Participantes inscritos:");
        for (ParticipacionTorneo p : this.inscritos) {
            System.out.println("  - " + p.getNombre());
        }

        System.out.println("Encuentros disputados:");
        for (Partido p : this.encuentros) {
            System.out.println("  * Fecha: " + p.getFecha() + " | Marcador: " + p.getMarcadorFinal());
        }

        if (this.modalidad == ModalidadTorneo.EQUIPOS) {
            System.out.println("Tabla de Posiciones:");
            this.tablaPosiciones.mostrar();
        }
    }

    private ParticipacionTorneo ganador;
    public void setGanador(ParticipacionTorneo ganador) { this.ganador = ganador; }
    public ParticipacionTorneo getGanador() { return ganador; }
    
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDeporte() { return deporte; }
    public String getCategoria() { return categoria; }
    public TipoTorneo getTipo() { return tipo; }
    public ModalidadTorneo getModalidad() { return modalidad; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public int getCuposTotales() { return cuposTotales; }
    public double getTarifaInscripcion() { return tarifaInscripcion; }
    public EstadoTorneo getEstado() { return estado; }
    public int getCuposPreferencialesTotales() { return cuposPreferencialesTotales; }
    public int getCuposRegularesTotales() { return cuposRegularesTotales; }
    public int getCuposPreferencialesOcupados() { return cuposPreferencialesOcupados; }
    public int getCuposRegularesOcupados() { return cuposRegularesOcupados; }
    public double getPozoRecaudado() { return pozoRecaudado; }
    public List<ParticipacionTorneo> getInscritos() { return inscritos; }
    public List<Partido> getEncuentros() { return encuentros; }
    public TablaPosiciones getTablaPosiciones() { return tablaPosiciones; }
}