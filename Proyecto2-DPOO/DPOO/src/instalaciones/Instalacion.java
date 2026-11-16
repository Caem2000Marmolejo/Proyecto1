package instalaciones;

public class Instalacion {
    private String id;
    private String disciplina;
    private int capacidadDefinida;
    private boolean techada;

    public Instalacion(String id, String disciplina, int capacidadDefinida, boolean techada) {
        this.id = id;
        this.disciplina = disciplina;
        this.capacidadDefinida = capacidadDefinida;
        this.techada = techada;
    }

    public String getId() { return id; }
    public String getDisciplina() { return disciplina; }
    public int getCapacidadDefinida() { return capacidadDefinida; }
    public boolean isTechada() { return techada; }
}