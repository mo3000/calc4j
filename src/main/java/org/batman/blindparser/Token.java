package org.batman.blindparser;

import org.batman.blindparser.Ast.NumberNode;

import java.util.Objects;

public class Token {

    private final String t;
    private final int line;
    private final int pos;
    protected boolean isNumber;
    protected boolean isOp;

    public Token(String t, int line, int pos) {
        this.t = t;
        this.line = line;
        this.pos = pos;
        isNumber = false;
        isOp = false;
    }

    public String getName() {
        return t;
    }

    public int getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        return Objects.equals(t, token.t);
    }

    @Override
    public int hashCode() {
        return t != null ? t.hashCode() : 0;
    }

    public static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    public boolean isNumberNode() { return isNumber; }

    public boolean isOpNode() { return isOp; }

    @Override
    public String toString() {
        return t;
    }
}
