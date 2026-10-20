package edu.psp.program5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

class MainTest {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private int run(String input, String... args) {
        Scanner in = new Scanner(input);
        return Main.run(args, in,
                new PrintStream(out, true, StandardCharsets.UTF_8),
                new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    private String out() {
        return out.toString(StandardCharsets.UTF_8);
    }

    private String err() {
        return err.toString(StandardCharsets.UTF_8);
    }

    @Test
    void withoutArgsPrintsTable1AndExitsOnEmptyX() {
        assertEquals(0, run("\n"));
        assertTrue(out().contains("1.1000     9  0.35006"));
        assertTrue(out().contains("1.1812    10  0.36757"));
        assertTrue(out().contains("2.7500    30  0.49500"));
        assertTrue(out().contains("x: "));
    }

    @Test
    void exitsWhenInputEnds() {
        assertEquals(0, run(""));
        assertTrue(out().contains("0.49500"));
    }

    @Test
    void computesUserValues() {
        assertEquals(0, run("1.1\n9\n2.750\n30\n\n"));
        String output = out();
        // Cada caso aparece dos veces: en la Tabla 1 y como respuesta del usuario
        assertEquals(2, output.split("1.1000     9  0.35006", -1).length - 1);
        assertEquals(2, output.split("2.7500    30  0.49500", -1).length - 1);
        assertEquals("", err());
    }

    @Test
    void acceptsCommaAsDecimalSeparator() {
        assertEquals(0, run("1,1812\n10\n"));
        assertEquals(2, out().split("1.1812    10  0.36757", -1).length - 1);
    }

    @Test
    void reportsInvalidInputAndKeepsAsking() {
        assertEquals(0, run("abc\n9\n1.1\n0\n1.1\n9\n\n"));
        assertEquals(2, err().split("Entrada invalida", -1).length - 1);
        assertEquals(2, out().split("0.35006", -1).length - 1);
    }

    @Test
    void exitsWhenInputEndsBeforeDof() {
        assertEquals(0, run("1.1\n"));
        assertTrue(out().contains("dof: "));
    }

    @Test
    void computesCaseFromArguments() {
        assertEquals(0, run("", "1.1", "9"));
        assertTrue(out().contains("1.1000     9  0.35006"));
        assertFalse(out().contains("Tabla 1"));
    }

    @Test
    void invalidArgumentsReturnError() {
        assertEquals(1, run("", "1.1", "-3"));
        assertTrue(err().contains("Entrada invalida"));
        assertTrue(err().contains(Main.USAGE));
    }

    @Test
    void wrongNumberOfArgumentsPrintsUsage() {
        assertEquals(1, run("", "1.1"));
        assertEquals(Main.USAGE, err().trim());
        assertEquals("", out());
    }

    @Test
    void mainWithValidArgumentsDoesNotExit() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        try {
            System.setIn(new ByteArrayInputStream(new byte[0]));
            System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
            Main.main(new String[] {"1.1812", "10"});
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        assertTrue(out().contains("0.36757"));
    }
}
