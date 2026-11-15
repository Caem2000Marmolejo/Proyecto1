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
import Usuarios.Usuario;

public class GestorTorneos {

    private final Map<String, Torneo> torneos =
        new LinkedHashMap<>();

    private final Clock reloj;
    private final GestorUsuarios usuarios;

    public GestorTorneos(GestorUsuarios usuarios) {
        this(usuarios, Clock.systemDefaultZone());
    }

    public GestorTorneos(
            GestorUsuarios usuarios,
            Clock reloj) {

        if (usuarios == null || reloj == null) {
            throw new IllegalArgumentException(
                "El gestor de usuarios y el reloj son obligatorios."
            );
        }

        this.usuarios = usuarios;
        this.reloj = reloj;
    }

    public Torneo crearTorneo(
            Usuario solicitante,
            String id,
            String nombre,
            String deporte,
            String categoria,
            TipoTorneo tipo,
            ModalidadTorneo modalidad,
            LocalDateTime fechaInicio,
            int cuposTotales,
            double tarifaInscripcion) {

        usuarios.exigirAdministrador(solicitante);

        String clave = normalizarId(id);

        if (torneos.containsKey(clave)) {
            throw new IllegalArgumentException(
                "Ya existe un torneo con ese identificador."
            );
        }

        if (fechaInicio == null
                || !fechaInicio.isAfter(
                    LocalDateTime.now(reloj))) {
            throw new IllegalArgumentException(
                "La fecha de inicio debe ser futura."
            );
        }

        Torneo torneo = new Torneo(
            id,
            nombre,
            deporte,
            categoria,
            tipo,
            modalidad,
            fechaInicio,
            cuposTotales,
            tarifaInscripcion
        );

        torneos.put(clave, torneo);

        return torneo;
    }

    public Torneo buscarTorneo(String id) {
        return torneos.get(normalizarId(id));
    }

    public List<Torneo> getTorneos() {
        return Collections.unmodifiableList(
            new ArrayList<>(torneos.values())
        );
    }

    private static String normalizarId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                "El identificador es obligatorio."
            );
        }

        return id.trim().toLowerCase(Locale.ROOT);
    }
}