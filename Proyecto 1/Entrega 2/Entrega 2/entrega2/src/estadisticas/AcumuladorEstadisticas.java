package estadisticas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Usuarios.Socio;

public class AcumuladorEstadisticas {
    private Map<String, Integer> golesPorJugador = new HashMap<>();
    private Map<String, Socio> socios = new HashMap<>();

    public void acumularGoles(Socio s, int goles) {
        String login = s.getLogin();
        socios.put(login, s);
        golesPorJugador.put(login, golesPorJugador.getOrDefault(login, 0) + goles);
    }

    public void mostrarGoleador() {
        String mejorLogin = null;
        int maxGoles = -1;
        for (Map.Entry<String, Integer> entry : golesPorJugador.entrySet()) {
            if (entry.getValue() > maxGoles) {
                maxGoles = entry.getValue();
                mejorLogin = entry.getKey();
            }
        }
        if (mejorLogin != null) {
            Socio s = socios.get(mejorLogin);
            System.out.println("Goleador: " + s.getLogin() + " con " + maxGoles + " goles");
        } else {
            System.out.println("No hay goles registrados.");
        }
    }

    public int getGoles(String login) {
        return golesPorJugador.getOrDefault(login, 0);
    }
}