package ie.francis.lisp.parser;

import ie.francis.lisp.exception.SyntaxErrorException;
import ie.francis.lisp.scanner.Scanner;
import ie.francis.lisp.type.Cons;
import ie.francis.lisp.type.Symbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private Parser parser;

    @BeforeEach
    void setUp() {
        Scanner scanner = new Scanner("");
        parser = new Parser(scanner);
    }

    @Test
    void parseEmptyInputReturnsEmptyList() throws SyntaxErrorException {
//        Object exprs = parser.parse();
//        assertTrue(exprs.isEmpty());
    }

    @Test
    void parseSingleSymbolExpression() throws SyntaxErrorException {
        Scanner scanner = new Scanner("mySymbol");
        parser = new Parser(scanner);

        Object expr = parser.parse();
        assertTrue(expr instanceof Symbol);
        assertEquals("mySymbol", ((Symbol) expr).getValue());
    }

    @Test
    void parseListWithAtoms() throws SyntaxErrorException {
        Scanner scanner = new Scanner("(1 \"hello\" true)");
        parser = new Parser(scanner);

        Object expr = parser.parse();

        assertTrue(expr instanceof Cons);

        Cons cons = (Cons) expr;

        assertTrue(cons.getCar() instanceof Integer);
        cons = cons.getCdr();
        assertTrue(cons.getCar() instanceof String);
        cons = cons.getCdr();
        assertTrue(cons.getCar() instanceof Boolean);
        cons = cons.getCdr();
        Assertions.assertNull(cons);

    }

    @Test
    void parseSingleIntegerExpression() throws SyntaxErrorException {
        Scanner scanner = new Scanner("42");
        parser = new Parser(scanner);

        Object expr = parser.parse();
        assertTrue(expr instanceof Integer);
        assertEquals(42, ((Integer) expr));
    }

    @Test
    void parseSingleStringExpression() throws SyntaxErrorException {
        Scanner scanner = new Scanner("\"hello\"");
        parser = new Parser(scanner);

        Object expr = parser.parse();
        assertTrue(expr instanceof String);
        assertEquals("hello", ((String) expr));
    }

    @Test
    void parseSingleBooleanExpression() throws SyntaxErrorException {
        Scanner scanner = new Scanner("true");
        parser = new Parser(scanner);

        Object expr = parser.parse();
        assertTrue(expr instanceof Boolean);
        assertEquals(true, expr);
    }

    @Test
    void parseConsExpression() throws SyntaxErrorException {
        Scanner scanner = new Scanner("(cons 1 2)");
        parser = new Parser(scanner);

        Object expr = parser.parse();
        assertTrue(expr instanceof Cons);
        assertEquals(new Symbol("cons"), ((Cons) expr).getCar());

        Cons cdr = ((Cons) expr).getCdr();
        assertEquals(1, (cdr.getCar()));

        Assertions.assertNotNull(cdr.getCdr());
        assertEquals(2, (cdr.getCdr().getCar()));

        Assertions.assertNull(cdr.getCdr().getCdr());
    }

    @Test
    void parseNestedConsExpressions() throws SyntaxErrorException {
        Scanner scanner = new Scanner("(cons (cons 1 2) 3)");
        parser = new Parser(scanner);

        Object expr = parser.parse();
        assertTrue(expr instanceof Cons);
        assertEquals(new Symbol("cons"), ((Cons) expr).getCar());

        Cons cdr = ((Cons) expr).getCdr();
        assertTrue(cdr.getCar() instanceof Cons);
        assertEquals(1, ((Cons) cdr.getCar()).getCdr().getCar());

        Assertions.assertNotNull(((Cons) cdr.getCar()).getCdr());
        assertEquals(2, ((Cons) cdr.getCar()).getCdr().getCdr().getCar());

        Assertions.assertNotNull(cdr.getCdr());
        assertEquals(3, cdr.getCdr().getCar());

        Assertions.assertNull(cdr.getCdr().getCdr());
    }

    @Test
    void parseQuoteReaderMacro() throws SyntaxErrorException {
        Scanner scanner = new Scanner("'42");
        parser = new Parser(scanner);

        Object expr = parser.parse();
        Cons cons = (Cons) expr;
        assertEquals(new Symbol("quote"), cons.getCar());

        Cons cdr = ((Cons) expr).getCdr();
        assertEquals(42, cdr.getCar());
        Assertions.assertNull(cdr.getCdr());
    }

    @Test
    void testParseNestedLists() throws SyntaxErrorException {
        Scanner scanner = new Scanner("((1 2) (3 4))");
        parser = new Parser(scanner);
        Object expr = parser.parse();
        assertTrue(expr instanceof Cons);
        Cons outerCons = (Cons) expr;
        assertTrue(outerCons.getCar() instanceof Cons);
        assertNotNull(outerCons.getCdr());
    }

    @Test
    void testMissingClosingParentheses() {
        Scanner scanner = new Scanner("(+ 1 2");
        parser = new Parser(scanner);
        assertThrows(SyntaxErrorException.class, () -> parser.parse());
    }

    @Test
    void testParseFloatingPointNumber() throws SyntaxErrorException {
        Scanner scanner = new Scanner("(3.14)");
        parser = new Parser(scanner);
        Object expr = parser.parse();
        assertEquals(3.14f, ((Cons) expr).getCar());
    }

    @Test
    void testParseDotExpression() throws SyntaxErrorException {
        Scanner scanner = new Scanner("(. x toUpperCase)");
        parser = new Parser(scanner);
        Object expr = parser.parse();
        assertEquals(new Symbol("."), ((Cons) expr).getCar());
        assertEquals(new Symbol("x"), ((Cons) expr).getCdr().getCar());
        assertEquals(new Symbol("toUpperCase"), ((Cons) expr).getCdr().getCdr().getCar());
    }

}
