package org.batman.blindparser.Ast;

import org.batman.blindparser.Expr;
import org.batman.blindparser.OpType;
import org.batman.blindparser.Token;

public class OpNode extends Token {

    private final OpType type;

    public OpNode(String t, int line, int pos) {
        super(t, line, pos);
        isOp = true;
        type = switch (t) {
            case "+" -> OpType.Add;
            case "-" -> OpType.Sub;
            case "*" -> OpType.Mul;
            case "/" -> OpType.Div;
            default -> throw new IllegalStateException(String.format("Unexpected value: '%s'", t));
        };
    }

    public OpType getType() {
        return type;
    }
}
