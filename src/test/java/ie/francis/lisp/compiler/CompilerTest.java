package ie.francis.lisp.compiler;

import ie.francis.lisp.Environment;
import ie.francis.lisp.function.*;
import ie.francis.lisp.function.macro.Func;
import ie.francis.lisp.type.Cons;
import ie.francis.lisp.type.Symbol;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CompilerTest {

    @Test
    void testBuiltinPlusWithZeroArgs() {
        Object output = eval("(+)");
        assertEquals(0, output);
    }

    @Test
    void testBuiltinPlus() {
        Object output = eval("(+ 1 2 3)");
        assertEquals(6, output);
    }

    @Test
    void testBuiltinPlusWithMoreThanFiveArgs() {
        Object output = eval("(+ 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20)");
        assertEquals(210, output);
    }

    @Test
    void testCallingPlusUsingApply() {
        Object output = eval("(apply + 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20)");
        assertEquals(210, output);
    }

    @Test
    void testCarOfList() {
        Object output = eval("(car '(a b c))");
        assertEquals(new Symbol("a"), output);
    }

    @Test
    void testCarOfNonList() {
        Object output = eval("(car 123)");
        assertNull(output);
    }

    @Test
    void testCdrOfList() {
        Object output = eval("(cdr '(a b c))");
        Cons expected = new Cons()
                    .setCar(new Symbol("b")).setCdr(new Cons()
                            .setCar(new Symbol("c")));
        assertEquals(String.format("%s", expected), String.format("%s", output));
    }

    @Test
    void testCdrOfNonList() {
        Object output = eval("(cdr 123)");
        assertNull(output);
    }

    @Test
    void testCallingReadOfNumber() {
        Object output = eval("(read \"123\")");
        assertEquals(123, output);
    }

    @Test
    void testCallingReadOfSymbol() {
        Object output = eval("(read \"hello\")");
        assertEquals(new Symbol("hello"), output);
    }

    @Test
    void testCallingReadOfBooleanTrue() {
        Object output = eval("(read \"true\")");
        assertEquals(true, output);
    }

    @Test
    void testCallingReadOfBooleanFalse() {
        Object output = eval("(read \"false\")");
        assertEquals(false, output);
    }

    @Test
    void testShorthandQuoteEqualsLonghandQuote() {
        Object shorthand = eval("'abc");
        Object expected = eval("(quote abc)");
        assertEquals(expected, shorthand);
    }

    @Test
    void testShorthandQuoteEqualsLonghandQuoteWhenNested() {
        Object shorthand = eval("'''''abc");
        Object expected = eval("(quote (quote (quote (quote (quote abc)))))");
        assertEquals(String.format("%s", expected), String.format("%s", shorthand));
    }

    @Test
    void testQuoteOfLambdaExpression() {
        Object lambda = eval("'(lambda (x y) (+ x y))");
        assertEquals("(lambda (x y) (+ x y))", String.format("%s", lambda));
    }

    Object eval(String input) {
        Read reader = new Read();
        Eval eval = new Eval();

        Object output = null;
        do {
            output = eval.call(reader.call(input));
        } while (!reader.isComplete());
        return output;
    }

    @BeforeAll
    static void addBuiltinsToEnvironment() {
        Environment.put(new Symbol("apply"), new Apply());
        Environment.put(new Symbol("car"), new Car());
        Environment.put(new Symbol("cdr"), new Cdr());
        Environment.put(new Symbol("compile"), new Compile());
        Environment.put(new Symbol("eval"), new Eval());
        Environment.put(new Symbol("print"), new Print());
        Environment.put(new Symbol("read"), new Read());
        Environment.put(new Symbol("type"), new Type());
        Environment.put(new Symbol("list"), new List());
        Environment.put(new Symbol("macroexpand-1"), new MacroExpand1());
        Environment.put(new Symbol("func"), new Func());
        Environment.put(new Symbol("+"), new Plus());
        Environment.put(new Symbol("-"), new Minus());
        Environment.put(new Symbol("="), new Equal());
        Environment.put(new Symbol("<"), new LessThan());
        Environment.put(new Symbol(">"), new GreaterThan());
    }

}
