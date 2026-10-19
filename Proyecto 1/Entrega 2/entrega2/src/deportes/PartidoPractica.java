package deportes;

import Usuarios.Socio;

public class PartidoPractica extends Partido {
    
public boolean validarNivelesAdyacentes(Socio s1, Socio s2, boolean unoEsEntrenador) {
        
        if (unoEsEntrenador == true) {
            return true; 
        }
        
        String nivel1 = s1.getNivelRaqueta();
        String nivel2 = s2.getNivelRaqueta();
        
        if (nivel1.equals(nivel2)) {
            return true;
        }
        
        if (nivel1.equals("Principiante") && nivel2.equals("Intermedio")) {
            return true;
        }
        if (nivel1.equals("Intermedio") && nivel2.equals("Principiante")) {
            return true;
        }
        
        if (nivel1.equals("Intermedio") && nivel2.equals("Avanzado")) {
            return true;
        }
        if (nivel1.equals("Avanzado") && nivel2.equals("Intermedio")) {
            return true;
        }

        return false;
    }
}