package org.batman.blindparser;

public enum OpType {
    Add(0), Sub(0), Mul(1), Div(1);

    private final int precedence;

    public int getPrecedence() { return precedence; }

    OpType(int prece) { precedence = prece; }
}
