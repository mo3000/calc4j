package org.batman.blindparser.Ast;

import org.batman.blindparser.Expr;
import org.batman.blindparser.Token;

public class NumberNode extends Token {

    private final double value;

    public NumberNode(String t, int line, int pos) {
        super(t, line, pos);
        this.value = Double.parseDouble(t);
        isNumber = true;
    }

    public double getValue() {
        return value;
    }

    public Expr toExpr() {
        return new Expr(new NumberNode(getName(), getLine(), getPos()));
    }
}
