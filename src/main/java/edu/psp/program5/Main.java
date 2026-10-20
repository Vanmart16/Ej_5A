package edu.psp.program5;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

/**
 * Programa 5: integra la distribucion t de 0 a x con la regla de Simpson.
 * Uso: java -jar program5.jar [x dof]
 * Sin argumentos ejecuta los casos de la Tabla 1 y despues pide valores de
 * x y dof por teclado hasta que se deje x vacio.
 */
public final class Main {

    static final String USAGE = "Uso: java -jar program5.jar [x dof]";

    private static final double[][] TABLE1 = {{1.1, 9}, {1.1812, 10}, {2.750, 30}};

    private Main() {
    }

    public static void main(String[] args) {
        int status = run(args, new Scanner(System.in), System.out, System.err);
        if (status != 0) {
            System.exit(status);
        }
    }

    /**
     * Ejecuta el programa leyendo de {@code in} y escribiendo en {@code out}/{@code err}.
     *
     * @return codigo de salida (0 si todo salio bien)
     */
    static int run(String[] args, Scanner in, PrintStream out, PrintStream err) {
        if (args.length == 2) {
            try {
                printResult(out, parseX(args[0]), parseDof(args[1]));
                return 0;
            } catch (IllegalArgumentException e) {
                // NumberFormatException tambien es IllegalArgumentException
                err.println("Entrada invalida: " + e.getMessage());
                err.println(USAGE);
                return 1;
            }
        }
        if (args.length != 0) {
            err.println(USAGE);
            return 1;
        }

        out.println("Tabla 1");
        printHeader(out);
        for (double[] c : TABLE1) {
            printRow(out, c[0], (int) c[1], compute(c[0], (int) c[1]));
        }

        out.println();
        out.println("Ingresa tus valores (deja x vacio para salir)");
        while (true) {
            String x = prompt(in, out, "x: ");
            if (x == null || x.isEmpty()) {
                return 0;
            }
            String dof = prompt(in, out, "dof: ");
            if (dof == null) {
                return 0;
            }
            try {
                printResult(out, parseX(x), parseDof(dof));
            } catch (IllegalArgumentException e) {
                err.println("Entrada invalida: " + e.getMessage());
            }
            out.println();
        }
    }

    private static String prompt(Scanner in, PrintStream out, String label) {
        out.print(label);
        out.flush();
        return in.hasNextLine() ? in.nextLine().trim() : null;
    }

    /** Acepta coma o punto como separador decimal (1,1 o 1.1). */
    private static double parseX(String text) {
        return Double.parseDouble(text.trim().replace(',', '.'));
    }

    private static int parseDof(String text) {
        return Integer.parseInt(text.trim());
    }

    private static double compute(double x, int dof) {
        return new SimpsonIntegrator().integrate(new TDistribution(dof), x);
    }

    private static void printResult(PrintStream out, double x, int dof) {
        double p = compute(x, dof);
        printHeader(out);
        printRow(out, x, dof, p);
    }

    private static void printHeader(PrintStream out) {
        out.println("     x   dof         p");
    }

    private static void printRow(PrintStream out, double x, int dof, double p) {
        out.println(String.format(Locale.US, "%6.4f  %4d  %.5f", x, dof, p));
    }
}
