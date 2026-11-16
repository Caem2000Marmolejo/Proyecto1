package deportes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TablaPosiciones {
    
    private static class RegistroEquipo {
        int puntos = 0;
        int partidosJugados = 0;
        int golesFavor = 0;
        int golesContra = 0;
    }

    private List<Equipo> equipos;
    private Map<Equipo, RegistroEquipo> tabla;
    private Map<String, Integer> resultadosDirectos;

    public TablaPosiciones(List<Equipo> equipos) {
        this.equipos = new ArrayList<>(equipos);
        this.tabla = new HashMap<>();
        this.resultadosDirectos = new HashMap<>();
        for (Equipo e : equipos) {
            tabla.put(e, new RegistroEquipo());
        }
    }

    public void registrarResultado(Equipo equipo, int puntosGanados, int golesFavor, int golesContra) {
        RegistroEquipo reg = tabla.get(equipo);
        if (reg != null) {
            reg.puntos += puntosGanados;
            reg.partidosJugados++;
            reg.golesFavor += golesFavor;
            reg.golesContra += golesContra;
        }
    }

    public void registrarEnfrentamientoDirecto(Equipo ganador, Equipo perdedor) {
        String llave = ganador.getNombre() + "_vs_" + perdedor.getNombre();
        resultadosDirectos.put(llave, 1);
    }

    public void mostrar() {
        System.out.println("Tabla de posiciones:");
        List<Equipo> ordenados = new ArrayList<>(equipos);
        
        ordenados.sort((e1, e2) -> {
            int puntosE1 = tabla.get(e1).puntos;
            int puntosE2 = tabla.get(e2).puntos;
            
            if (puntosE1 != puntosE2) {
                return Integer.compare(puntosE2, puntosE1);
            }
            
            String llave1 = e1.getNombre() + "_vs_" + e2.getNombre();
            String llave2 = e2.getNombre() + "_vs_" + e1.getNombre();
            
            if (resultadosDirectos.containsKey(llave1)) return -1;
            if (resultadosDirectos.containsKey(llave2)) return 1;
            
            return 0;
        });

        for (Equipo e : ordenados) {
            RegistroEquipo reg = tabla.get(e);
            System.out.println("  " + e.getNombre() + " - Puntos: " + reg.puntos
                    + " - PJ: " + reg.partidosJugados + " - GF: " + reg.golesFavor + " - GC: " + reg.golesContra);
        }
    }

    public int getPuntos(Equipo equipo) {
        RegistroEquipo reg = tabla.get(equipo);
        return reg != null ? reg.puntos : 0;
    }
}