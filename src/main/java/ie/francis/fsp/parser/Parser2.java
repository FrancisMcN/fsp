package ie.francis.fsp.parser;

import ie.francis.fsp.ast.*;
import ie.francis.fsp.exception.SyntaxErrorException;
import ie.francis.fsp.runtime.helper.ConsBuilder;
import ie.francis.fsp.runtime.type.*;
import ie.francis.fsp.runtime.type.Number;
import ie.francis.fsp.scanner.Scanner;
import ie.francis.fsp.token.Token;
import ie.francis.fsp.token.Type;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class Parser2 {
    private final Scanner scanner;

    public Parser2(Scanner scanner) {
        this.scanner = scanner;
    }

    // prog : sxpr*
    // sxpr : atom | list
    // list : '(' sxpr+ ['.' sxpr]? ')'
    // atom : SYMBOL | NUMBER | STRING | BOOLEAN | ε

    public List<DataType> parse() throws SyntaxErrorException {
//        return sxpr();
//        ProgramNode programNode = new ProgramNode();
        List<DataType> expressions = new ArrayList<>();
        while (scanner.peek().getType() != Type.EOF) {
            expressions.add(sxpr());
        }
        return expressions;
//        return programNode;
    }

    // sxpr : 'sxpr | atom | '(' sxpr '.' sxpr ')' | list
    public DataType sxpr() throws SyntaxErrorException {
        Token token = scanner.peek();

        if (token.getType() == Type.QUOTE) {
            scanner.next();
            ConsBuilder consBuilder = new ConsBuilder();
            consBuilder.add(new Symbol("quote"));
            consBuilder.add(sxpr());
            return consBuilder.getCons();
//            ListNode node = new ListNode();
//            node.addNode(new SymbolNode("quote"));
//            node.addNode(sxpr());
//            return node;
        }

        if (token.getType() == Type.SYMBOL
                || token.getType() == Type.NUMBER
                || token.getType() == Type.STRING
                || token.getType() == Type.BOOLEAN) {
            return atom();
        }

        Token peek = scanner.peek();
        if (peek.getType() != Type.LPAREN
                && peek.getType() != Type.SYMBOL
                && peek.getType() != Type.NUMBER
                && peek.getType() != Type.STRING
                && peek.getType() != Type.BOOLEAN) {
            throw new SyntaxErrorException(
                    String.format("expected '(', symbol, number or a string but found: %s", token.getType()));
        }
        return list();
    }

    // list : '(' sxpr+ ['.' sxpr]? ')'
    public DataType list() {
//        ListNode list = new ListNode();
        ConsBuilder consBuilder = new ConsBuilder();
        scanner.next();
        do {
            consBuilder.add(sxpr());
        } while (scanner.peek().getType() != Type.RPAREN && scanner.peek().getType() != Type.DOT);

        Token token = scanner.peek();
//        if (token.getType() == Type.DOT) {
//            scanner.next();
//            SxprNode sxprNode = new SxprNode();
//            sxprNode.setCar(list.getNodes().remove(list.getNodes().size() - 1));
//            sxprNode.setCdr(sxpr());
//            list.addNode(sxprNode);
//        }

        if (scanner.peek().getType() != Type.RPAREN) {
            throw new SyntaxErrorException(String.format("expected ')', found: %s", token.getType()));
        }

        scanner.next();
        return consBuilder.getCons();
    }

    // atom : SYMBOL | NUMBER | STRING | BOOLEAN | ε
    private DataType atom() throws SyntaxErrorException {
        Token token = scanner.peek();
        switch (token.getType()) {
            case SYMBOL:
            {
                scanner.next();
                return new Symbol(token.getValue());
            }
            case NUMBER:
            {
                scanner.next();
                String tokenValue = token.getValue();
                if (tokenValue.contains(".")) {
                    return new Number(Float.parseFloat(tokenValue));
                }
                return new Number(Integer.parseInt(tokenValue));
            }
            case STRING:
            {
                scanner.next();
                return new FspString(token.getValue());
            }
            case BOOLEAN:
            {
                scanner.next();
                return new Bool(Boolean.parseBoolean(token.getValue()));
            }
            default:
            {
                throw new SyntaxErrorException(
                        String.format("expected symbol, number, string or list. Found: %s", token.getType()));
            }
        }
    }
}
