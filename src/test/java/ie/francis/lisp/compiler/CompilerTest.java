package ie.francis.lisp.compiler;

import ie.francis.lisp.Buffer;
import ie.francis.lisp.Environment;
import ie.francis.lisp.function.Apply;
import ie.francis.lisp.function.Car;
import ie.francis.lisp.function.Cdr;
import ie.francis.lisp.function.Compile;
import ie.francis.lisp.function.Equal;
import ie.francis.lisp.function.Eval;
import ie.francis.lisp.function.GreaterThan;
import ie.francis.lisp.function.LessThan;
import ie.francis.lisp.function.List;
import ie.francis.lisp.function.MacroExpand1;
import ie.francis.lisp.function.Minus;
import ie.francis.lisp.function.Plus;
import ie.francis.lisp.function.Print;
import ie.francis.lisp.function.Read;
import ie.francis.lisp.function.Type;
import ie.francis.lisp.function.macro.Func;
import ie.francis.lisp.type.Cons;
import ie.francis.lisp.type.Symbol;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompilerTest {

    @Test
    void testBuiltinPlusWithZeroArgs() {
        Object output = eval("(+)");
        assertEquals(0, output);
    }

    @Test
    void testBuiltinPlusWithOneArgument() {
        Object output = eval("(+ 5)");
        assertEquals(5, output);
    }

    @Test
    void testBuiltinMinusWithOneArgument() {
        Object output = eval("(- 5)");
        assertEquals(-5, output);
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

    @Test
    void testArgmentsAreNotEvaluatedForMacro() {
        eval("(def identity-macro (macro (x) x))");
        Object output = eval("(macroexpand-1 '(identity-macro (+ 1 2 3)))");
        assertEquals("(+ 1 2 3)", String.format("%s", output));
    }

    @Test
    void testArgmentsAreEvaluatedForLambda() {
        eval("(def identity-lambda (lambda (x) x))");
        Object output = eval("(identity-lambda (+ 1 2 3))");
        assertEquals(6, output);
    }

    @Test
    void testTypeFunctionReturnsCorrectTypeForInteger() {
        Object output = eval("(type 100)");
        assertEquals("java.lang.Integer", output);
    }

    @Test
    void testTypeFunctionReturnsCorrectTypeForString() {
        Object output = eval("(type \"Hello World\")");
        assertEquals("java.lang.String", output);
    }

    @Test
    void testTypeFunctionReturnsCorrectTypeForSymbol() {
        Object output = eval("(type 'a)");
        assertEquals("ie.francis.lisp.type.Symbol", output);
    }

    @Test
    void testTypeFunctionReturnsCorrectTypeForBooleanTrue() {
        Object output = eval("(type true)");
        assertEquals("java.lang.Boolean", output);
    }

    @Test
    void testTypeFunctionReturnsCorrectTypeForBooleanFalse() {
        Object output = eval("(type false)");
        assertEquals("java.lang.Boolean", output);
    }

    @Test
    void testTypeFunctionReturnsCorrectTypeForLambda() {
        Object output = eval("(type (lambda () ()))");
        assertEquals("Lambda", output.toString().substring(0, 6));
    }

    @Test
    void testTypeFunctionReturnsCorrectTypeForMacro() {
        Object output = eval("(type (macro () ()))");
        assertEquals("Macro", output.toString().substring(0, 5));
    }

    @Test
    void testEmptyLambdaIsCallable() {
        assertDoesNotThrow(() -> eval("((lambda () ()))"));
    }

    @Test
    void testEmptyLambdaReturnsNull() {
        Object output = eval("((lambda () ()))");
        assertNull(output);
    }

    @Test
    void testInlineLambdaIsExecutedSuccessfully() {
        Object output = eval("((lambda (x) (+ x 1)) 5)");
        assertEquals(6, output);
    }

    @Test
    void testInlineCompileIsExecutedSuccessfully() {
        Object output = eval("((compile 5))");
        assertEquals(5, output);
    }

    @Test
    void testEmptyListisNull() {
        Object output = eval("()");
        assertNull(output);
    }

    @Test
    void testEmptyListComparisonIsCorrect() {
        Object output = eval("(= () nil)");
        assertTrue((Boolean) output);
    }

    @Test
    void testQuotedNilEqualsNil() {
        Object output = eval("(= 'nil nil)");
        assertTrue((Boolean) output);
    }

    @Test
    void testTwoSymbolsAreEqual() {
        Object output = eval("(= (quote a) 'a)");
        assertTrue((Boolean) output);
    }

    @Test
    void testTwoDifferentSymbolsAreNotEqual() {
        Object output = eval("(= 'b 'c)");
        assertFalse((Boolean) output);
    }

    @Test
    void testDoSpecialFormWithSingleArgument() {
        Object output = eval("(do 1)");
        assertEquals(1, output);
    }

    @Test
    void testDoSpecialFormWithMultipleArguments() {
        Object output = eval("(do 1 2 3 4 5 6 7 8 9)");
        assertEquals(9, output);
    }

    @Test
    void testDoSpecialFormEvaluatesEachExpression() {
        Object output = eval("(do (def x 2) (def y 3) (+ x y))");
        assertEquals(5, output);
    }

    @Test
    void testExprAssignmentToSymbolSucceeds() {
        eval("(def x (+ 1 2 3))");
        assertEquals(eval("x"), 6);
    }

    @Test
    void testDotSpecialFormWithString() {
        assertEquals("HELLO", eval("(. \"hello\" toUpperCase)"));
    }

    @Test
    void testDotSpecialFormWithLocalSymbol() {
        assertEquals("WORLD", eval("(do (let (x \"world\") (. x toUpperCase)))"));
    }

    @Test
    void testDotSpecialFormWithGlobalSymbol() {
        assertEquals("HELLO WORLD", eval("(do (def x \"hello world\") (. x toUpperCase))"));
    }

    @Test
    void testDotSpecialFormWithNesting() {
        assertEquals(true, eval("(do (def x \"hello world\") (. (. x toUpperCase) startsWith \"HELLO\"))"));
    }

    @Test
    void testDotSpecialFormReturnsInteger() {
        assertEquals(11, eval("(. \"hello world\" length)"));
    }

    @Test
    void testDotSpecialFormWithFunctionArguments() {
        assertEquals("ll", eval("(. \"hello world\" substring 2 4)"));
    }

    @Test
    void testDotSpecialFormCanReturnPrimitiveType() {
        assertEquals(false, eval("(. \"hello world\" startsWith \"francis\")"));
    }

    @Test
    void testDotSpecialFormCanBeQuoted() {
        Object output = eval("'(. \"francis\" toUpperCase)");
        assertEquals("(. francis toUpperCase)", String.format("%s", output));
    }

    @Test
    void testDotSpecialFormOnFloatingPointType() {
        assertEquals(1, eval("(. 1.23 intValue)"));
    }

    Object eval(String input) {
        Read reader = new Read();
        Eval eval = new Eval();

        Object output = null;
        Buffer buff = new Buffer(input);
        while (!buff.complete()) {
            output = eval.call(reader.call(buff));
        }
        return output;
    }

    @BeforeAll
    static void addBuiltinsToEnvironment() {
        Environment.put(new Symbol("nil"), null);
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
