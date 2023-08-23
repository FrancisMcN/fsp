package parser;

import ie.francis.fsp.exception.SyntaxErrorException;
import ie.francis.fsp.parser.Parser;
import ie.francis.fsp.runtime.rmacro.DerefReaderMacro;
import ie.francis.fsp.runtime.type.Atom;
import ie.francis.fsp.runtime.type.Cons;
import ie.francis.fsp.runtime.type.DataType;
import ie.francis.fsp.runtime.type.Symbol;
import ie.francis.fsp.scanner.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        List<DataType> expressions = parser.parse();
        assertTrue(expressions.isEmpty());
    }

    @Test
    void parseSingleSymbolExpression() throws SyntaxErrorException {
        Scanner scanner = new Scanner("mySymbol");
        parser = new Parser(scanner);

        List<DataType> expressions = parser.parse();
        assertEquals(1, expressions.size());
        assertTrue(expressions.get(0) instanceof Atom);
        assertEquals(new Symbol("mySymbol"), ((Atom) expressions.get(0)).getSymbol());
    }

    @Test
    void parseListWithAtoms() throws SyntaxErrorException {
        Scanner scanner = new Scanner("(1 \"hello\" true)");
        parser = new Parser(scanner);

        List<DataType> expressions = parser.parse();
        assertEquals(1, expressions.size());

        DataType expression = expressions.get(0);
        assertTrue(expression instanceof Cons);

        Cons cons = (Cons) expression;

        assertTrue(cons.getCar() instanceof Integer);
        cons = cons.getCdr();
        assertTrue(cons.getCar() instanceof String);
        cons = cons.getCdr();
        assertTrue(cons.getCar() instanceof Boolean);
        cons = cons.getCdr();
        assertNull(cons);

    }

    @Test
    void parseSingleIntegerExpression() throws SyntaxErrorException {
        Scanner scanner = new Scanner("42");
        parser = new Parser(scanner);

        DataType expression = parser.sxpr();
        assertTrue(expression instanceof Atom);
        assertEquals(42, ((Atom) expression).getInteger());
    }

    @Test
    void parseSingleStringExpression() throws SyntaxErrorException {
        Scanner scanner = new Scanner("\"hello\"");
        parser = new Parser(scanner);

        DataType expression = parser.sxpr();
        assertTrue(expression instanceof Atom);
        assertEquals("hello", ((Atom) expression).getString());
    }

    @Test
    void parseSingleBooleanExpression() throws SyntaxErrorException {
        Scanner scanner = new Scanner("true");
        parser = new Parser(scanner);

        DataType expression = parser.sxpr();
        assertTrue(expression instanceof Atom);
        assertEquals(true, ((Atom) expression).getBool());
    }

    @Test
    void parseConsExpression() throws SyntaxErrorException {
        Scanner scanner = new Scanner("(cons 1 2)");
        parser = new Parser(scanner);

        DataType expression = parser.sxpr();
        assertTrue(expression instanceof Cons);
        assertEquals(new Symbol("cons"), ((Cons) expression).getCar());

        Cons cdr = ((Cons) expression).getCdr();
        assertEquals(1, (cdr.getCar()));

        assertNotNull(cdr.getCdr());
        assertEquals(2, (cdr.getCdr().getCar()));

        assertNull(cdr.getCdr().getCdr());
    }

    @Test
    void parseNestedConsExpressions() throws SyntaxErrorException {
        Scanner scanner = new Scanner("(cons (cons 1 2) 3)");
        parser = new Parser(scanner);

        DataType expression = parser.sxpr();
        assertTrue(expression instanceof Cons);
        assertEquals(new Symbol("cons"), ((Cons) expression).getCar());

        Cons cdr = ((Cons) expression).getCdr();
        assertTrue(cdr.getCar() instanceof Cons);
        assertEquals(1, ((Cons) cdr.getCar()).getCdr().getCar());

        assertNotNull(((Cons) cdr.getCar()).getCdr());
        assertEquals(2, ((Cons) cdr.getCar()).getCdr().getCdr().getCar());

        assertNotNull(cdr.getCdr());
        assertEquals(3, cdr.getCdr().getCar());

        assertNull(cdr.getCdr().getCdr());
    }

    @Test
    void parseQuoteReaderMacro() throws SyntaxErrorException {
        Scanner scanner = new Scanner("'42");
        parser = new Parser(scanner);

        DataType expression = parser.sxpr();
        Cons cons = (Cons) expression;
        assertEquals(new Symbol("quote"), cons.getCar());

        Cons cdr = ((Cons) expression).getCdr();
        assertEquals(42, cdr.getCar());
        assertNull(cdr.getCdr());
    }

    @Test
    void parseDerefReaderMacro() throws SyntaxErrorException {
        Scanner scanner = new Scanner("@myVar");
        parser = new Parser(scanner);
        parser.getReaderMacros().put("@", new DerefReaderMacro());

        DataType expression = parser.sxpr();
        Cons cons = (Cons) expression;
        assertEquals(new Symbol("quote"), cons.getCar());

        assertEquals(new Symbol("deref"), ((Cons)cons.getCdr().getCar()).getCar());

        Cons cdr = ((Cons)((Cons) expression).getCdr().getCar()).getCdr();
        assertEquals(new Symbol("myVar"), cdr.getCar());
        assertNull(cdr.getCdr());
    }

    @Test
    void parseNestedReaderMacro() throws SyntaxErrorException {
        Scanner scanner = new Scanner("'@42");
        parser = new Parser(scanner);
        parser.getReaderMacros().put("@", new DerefReaderMacro());

        DataType expression = parser.sxpr();
        assertEquals(new Symbol("quote"), ((Cons) expression).getCar());

        Cons cdr = ((Cons) expression).getCdr();
        assertTrue(cdr.getCar() instanceof Cons);

        Cons innerCons = (Cons) cdr.getCar();
        assertEquals(new Symbol("quote"), innerCons.getCar());

        innerCons = (Cons) ((Cons) cdr.getCar()).getCdr().getCar();
        assertEquals(new Symbol("deref"), innerCons.getCar());

        Cons innerCdr = innerCons.getCdr();
        assertEquals(42, innerCdr.getCar());

        assertNull(innerCdr.getCdr());
        assertNull(cdr.getCdr());
    }

}