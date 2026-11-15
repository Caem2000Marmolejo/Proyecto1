package deportes;

import java.time.LocalDateTime;

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

    Torneo(
            String id,
            String nombre,
            String deporte,
            String categoria,
            TipoTorneo tipo,
            ModalidadTorneo modalidad,
            LocalDateTime fechaInicio,
            int cuposTotales,
            double tarifaInscripcion) {

        this.id = validarTexto(id, "identificador");
        this.nombre = validarTexto(nombre, "nombre");
        this.deporte = validarTexto(deporte, "deporte");
        this.categoria = validarTexto(categoria, "categoria o nivel");

        if (tipo == null || modalidad == null || fechaInicio == null) {
            throw new IllegalArgumentException(
                "Tipo, modalidad y fecha son obligatorios."
            );
        }

        if (cuposTotales <= 0) {
            throw new IllegalArgumentException(
                "El torneo debe tener al menos un cupo."
            );
        }

        if (!Double.isFinite(tarifaInscripcion)
                || tarifaInscripcion < 0) {
            throw new IllegalArgumentException(
                "La tarifa debe ser un numero no negativo."
            );
        }

        if (tipo == TipoTorneo.RECREATIVO
                && tarifaInscripcion != 0) {
            throw new IllegalArgumentException(
                "Un torneo recreativo debe ser gratuito."
            );
        }

        if (tipo == TipoTorneo.COMPETITIVO
                && tarifaInscripcion <= 0) {
            throw new IllegalArgumentException(
                "Un torneo competitivo debe tener una tarifa positiva."
            );
        }

        this.tipo = tipo;
        this.modalidad = modalidad;
        this.fechaInicio = fechaInicio;
        this.cuposTotales = cuposTotales;
        this.tarifaInscripcion = tarifaInscripcion;
        this.estado = EstadoTorneo.INSCRIPCIONES_ABIERTAS;
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                "Debe indicar " + campo + "."
            );
        }

        return valor.trim();
    }

    public int getCuposPreferencialesIniciales() {
        return cuposTotales / 5
            + (cuposTotales % 5 == 0 ? 0 : 1);
    }

    public int getCuposRegularesIniciales() {
        return cuposTotales - getCuposPreferencialesIniciales();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDeporte() {
        return deporte;
    }

    public String getCategoria() {
        return categoria;
    }

    public TipoTorneo getTipo() {
        return tipo;
    }

    public ModalidadTorneo getModalidad() {
        return modalidad;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public int getCuposTotales() {
        return cuposTotales;
    }

    public double getTarifaInscripcion() {
        return tarifaInscripcion;
    }

    public EstadoTorneo getEstado() {
        return estado;
    }
}