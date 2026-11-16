package view;

import java.util.Scanner;
import java.util.NoSuchElementException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EntradaConsola {
    private final Scanner scanner = new Scanner(System.in);

    public String texto(String mensaje) {
        while (true) {
            System.out.print(mensaje + ": ");
            if (!scanner.hasNextLine()) {
                throw new NoSuchElementException("Fin de la entrada.");
            }

            String valor = scanner.nextLine().trim();

            if (!valor.isEmpty() && !valor.contains(";")) {
                return valor;
            }

            System.out.println("Escriba un valor no vacio, sin punto y coma.");
        }
    }

    public int entero(String mensaje, int minimo, int maximo) {
        while (true) {
            try {
                int valor = Integer.parseInt(texto(mensaje));

                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un numero entero.");
                continue;
            }

            System.out.println(
                "El valor debe estar entre " + minimo + " y " + maximo + "."
            );
        }
    }

    public double decimal(String mensaje, double minimo) {
        while (true) {
            try {
                double valor = Double.parseDouble(
                    texto(mensaje).replace(',', '.')
                );

                if (Double.isFinite(valor) && valor >= minimo) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un numero.");
                continue;
            }

            System.out.println(
                "Escriba un valor finito mayor o igual a " + minimo + "."
            );
        }
    }

    public boolean confirmar(String mensaje) {
        return entero(mensaje + " (1: si, 0: no)", 0, 1) == 1;
    }

    public String fecha() {
        while (true) {
            try {
                return LocalDate.parse(
                    texto("Fecha AAAA-MM-DD")
                ).toString();
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("La fecha no es valida.");
            }
        }
    }

    public LocalDateTime fechaHora() {
        while (true) {
            try {
                return LocalDateTime.parse(
                    texto("Fecha y hora AAAA-MM-DDTHH:MM")
                );
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("La fecha y hora no son validas.");
            }
        }
    }

    public int menu(String titulo, String[] opciones) {
        System.out.println("\n=== " + titulo + " ===");

        for (int i = 0; i < opciones.length; i++) {
            System.out.println((i + 1) + ". " + opciones[i]);
        }

        System.out.println("0. Volver / salir");

        return entero("Opcion", 0, opciones.length);
    }
}