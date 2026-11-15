package Usuarios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ventas.BonoDescuento;

public class Socio extends Usuario {
    private int edad;
    private String categoria;
    private double puntosFidelidad;
    private String nivelRaqueta;
    private Map<String, Integer> rankingPorDisciplina;
    private List<BonoDescuento> bonos;

    public Socio(String login, String password, int edad) {
        super(login, password);
        this.edad = edad;
        this.puntosFidelidad = 0;
        this.rankingPorDisciplina = new HashMap<>();
        this.bonos = new ArrayList<>();
        inicializarRankings();
    }

    public Socio(String login, String password, String categoria, int edad, String nivelRaqueta) {
        super(login, password);
        this.edad = edad;
        this.categoria = categoria;
        setNivelRaqueta(nivelRaqueta);
        this.puntosFidelidad = 0;
        this.rankingPorDisciplina = new HashMap<>();
        this.bonos = new ArrayList<>();
        inicializarRankings();
    }

    private void inicializarRankings() {
        rankingPorDisciplina.put("Tenis", 1000);
        rankingPorDisciplina.put("Padel", 1000);
        rankingPorDisciplina.put("Tenis de Mesa", 1000);
    }

    public void setNivelRaqueta(String nivel) {
        if (nivel == null) {
            this.nivelRaqueta = "Principiante";
            return;
        }
        if (nivel.equalsIgnoreCase("Principiante") || nivel.equalsIgnoreCase("Intermedio") || nivel.equalsIgnoreCase("Avanzado")) {
            this.nivelRaqueta = nivel;
        } else {
            this.nivelRaqueta = "Principiante";
        }
    }

    public int getRanking(String disciplina) {
        return rankingPorDisciplina.getOrDefault(disciplina, 1000);
    }

    public void setRanking(String disciplina, int nuevoRanking) {
        rankingPorDisciplina.put(disciplina, nuevoRanking);
    }

    public Map<String, Integer> getRankingPorDisciplina() {
        return rankingPorDisciplina;
    }

    public double redimirPuntos(double cantidadARedimir) {
        if (cantidadARedimir <= puntosFidelidad) {
            puntosFidelidad -= cantidadARedimir;
            return cantidadARedimir;
        }
        return 0;
    }

    public void sumarPuntosFidelidad(double montoCompra) {
        this.puntosFidelidad += (montoCompra * 0.02);
    }
    
    public void setPuntosFidelidad(double puntos) {
        this.puntosFidelidad = puntos;
    }

    public void agregarBono(BonoDescuento bono) {
        this.bonos.add(bono);
    }

    public List<BonoDescuento> getBonos() {
        return bonos;
    }

    public BonoDescuento obtenerBonoDisponible() {
        for (BonoDescuento b : bonos) {
            if (!b.isUsado()) return b;
        }
        return null;
    }

    public int getEdad() { return edad; }
    public String getCategoria() { return categoria; }
    public double getPuntosFidelidad() { return puntosFidelidad; }
    public String getNivelRaqueta() { return nivelRaqueta; }
}