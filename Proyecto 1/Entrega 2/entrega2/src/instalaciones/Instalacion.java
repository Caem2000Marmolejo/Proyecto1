package instalaciones;

public  class Instalacion {
	private String disciplina;
    private int capacidadDefinida;
    private boolean techada;
    public Instalacion(String disciplina, int capacidadDefinida,  boolean techada) {
        this.disciplina = disciplina;
        this.techada = techada;
    }
    public String getDisciplina() { 
    	return disciplina;
    	}
    public boolean isTechada() { 
    	return techada;
    	}
}


