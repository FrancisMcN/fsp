package ie.francis.lisp.scanner;

import ie.francis.lisp.token.Token;
import ie.francis.lisp.token.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ScannerTest {

    private Scanner scanner;

    @BeforeEach
    void setUp() {
        scanner = new Scanner("");
    }

    @Test
    public void hasNextReturnsFalseForEmptyInput() {
        assertFalse(scanner.hasNext());
    }

    @Test
    public void peekReturnsEOFForEmptyInput() {
        assertEquals(new Token(Type.EOF, "eof", 1), scanner.peek());
    }

    @Test
    public void nextReturnsEOFForEmptyInput() {
        assertEquals(new Token(Type.EOF, "eof", 1), scanner.next());
    }

    @Test
    public void nextReturnsCorrectTokensForSimpleInput() {
        scanner = new Scanner("() . \"hello\" 123 true '");

        assertEquals(new Token(Type.LPAREN, "(", 1), scanner.next());
        assertEquals(new Token(Type.RPAREN, ")", 1), scanner.next());
        assertEquals(new Token(Type.DOT, ".", 1), scanner.next());
        assertEquals(new Token(Type.STRING, "hello", 1), scanner.next());
        assertEquals(new Token(Type.NUMBER, "123", 1), scanner.next());
        assertEquals(new Token(Type.BOOLEAN, "true", 1), scanner.next());
        assertEquals(new Token(Type.QUOTE, "'", 1), scanner.next());
        assertEquals(new Token(Type.EOF, "eof", 1), scanner.next());
    }

    @Test
    public void ignoreCommentIgnoresComments() {
        scanner = new Scanner("; This is a comment\n()");
        assertEquals(new Token(Type.LPAREN, "(", 2), scanner.next());
    }

    @Test
    public void symbolTokenRecognizesSpecialSymbols() {
        scanner = new Scanner("() ' # \"\"");

        assertEquals(new Token(Type.LPAREN, "(", 1), scanner.next());
        assertEquals(new Token(Type.RPAREN, ")", 1), scanner.next());
        assertEquals(new Token(Type.QUOTE, "'", 1), scanner.next());
        assertEquals(new Token(Type.HASH, "#", 1), scanner.next());
        assertEquals(new Token(Type.STRING, "", 1), scanner.next());
        assertEquals(new Token(Type.EOF, "eof", 1), scanner.next());
    }

    @Test
    void stringTokenParsesMultilineString() {
        scanner = new Scanner("\"Hello,\nWorld!\"");
        assertEquals(new Token(Type.STRING, "Hello,\nWorld!", 1), scanner.next());
    }

    @Test
    void numberTokenParsesDecimalNumber() {
        scanner = new Scanner("3.14");
        assertEquals(new Token(Type.NUMBER, "3.14", 1), scanner.next());
    }

    @Test
    void numberTokenParsesIntegerAndDecimal() {
        scanner = new Scanner("42.17");
        assertEquals(new Token(Type.NUMBER, "42.17", 1), scanner.next());
    }

    @Test
    void symbolTokenRecognizesBooleanKeywords() {
        scanner = new Scanner("true false");
        assertEquals(new Token(Type.BOOLEAN, "true", 1), scanner.next());
        assertEquals(new Token(Type.BOOLEAN, "false", 1), scanner.next());
    }

    @Test
    void isSpecialOrWhitespaceHandlesSpecialCharacters() {
        assertTrue(scanner.isSpecialOrWhitespace('('));
        assertTrue(scanner.isSpecialOrWhitespace('"'));
        assertTrue(scanner.isSpecialOrWhitespace('\n'));
        assertFalse(scanner.isSpecialOrWhitespace('a'));
    }

    @Test
    void hasNextReturnsTrueForNonEmptyInput() {
        scanner = new Scanner("1");
        assertTrue(scanner.hasNext());
    }

    @Test
    void ignoreCommentHandlesMultilineComment() {
        scanner = new Scanner("; Line 1\n; Line 2\n123");
        assertEquals(new Token(Type.NUMBER, "123", 3), scanner.next());
    }

    @Test
    void symbolTokenHandlesSpecialCharsInSymbol() {
        scanner = new Scanner("let*");
        assertEquals(new Token(Type.SYMBOL, "let*", 1), scanner.next());
    }

    @Test
    void peekDoesNotAdvancePointer() {
        scanner = new Scanner("(1 2)");
        assertEquals(new Token(Type.LPAREN, "(", 1), scanner.peek());
        assertEquals(new Token(Type.LPAREN, "(", 1), scanner.next());
    }

    @Test
    void numberTokenParsesInteger() {
        scanner = new Scanner("42");
        assertEquals(new Token(Type.NUMBER, "42", 1), scanner.next());
    }

    @Test
    void symbolTokenHandlesMixedCaseSymbols() {
        scanner = new Scanner("Hello World");
        assertEquals(new Token(Type.SYMBOL, "Hello", 1), scanner.next());
        assertEquals(new Token(Type.SYMBOL, "World", 1), scanner.next());
    }

    @Test
    void symbolTokenHandlesSpecialCharsInSymbolWithWhitespace() {
        scanner = new Scanner("var-name? ; Some comment\nnext-symbol");
        assertEquals(new Token(Type.SYMBOL, "var-name?", 1), scanner.next());
        assertEquals(new Token(Type.SYMBOL, "next-symbol", 2), scanner.next());
    }

    @Test
    void isSpecialOrWhitespaceHandlesWhitespace() {
        assertTrue(scanner.isSpecialOrWhitespace(' '));
        assertTrue(scanner.isSpecialOrWhitespace('\t'));
        assertTrue(scanner.isSpecialOrWhitespace('\r'));
        assertFalse(scanner.isSpecialOrWhitespace('a'));
    }

}
