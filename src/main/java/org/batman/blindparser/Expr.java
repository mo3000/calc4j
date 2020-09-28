package org.batman.blindparser;

import org.batman.blindparser.Ast.NumberNode;
import org.batman.blindparser.Ast.OpNode;

import java.util.Optional;

public class Expr {
    private Expr a;
    private Expr b;
    private OpNode operator;
    final private ExprType exprType;
    private double value;
    private String name;
    private boolean hasParen = false;

    final private int line;
    final private int beginPos;

    public Expr getA() {
        return a;
    }

    public boolean aHasParen() {
        if (a == null) return false;
        else return a.hasParen();
    }

    public Expr getB() {
        return b;
    }

    public OpNode getOperator() {
        return operator;
    }

    public Expr(Expr a, Expr b, OpNode operator, int beginPos, int line) {
        this.a = a;
        this.b = b;
        this.line = line;
        this.operator = operator;
        exprType = ExprType.Binary;
        this.beginPos = beginPos;
    }

    public boolean hasParen() { return hasParen; }
    public Expr setBracket() { hasParen = true; return this; }

    public Expr(Expr a, OpNode operator, int beginPos, int line) {
        this.a = a;
        this.operator = operator;
        this.line = line;
        exprType = ExprType.Unary;
        this.beginPos = beginPos;
    }

    public Expr(double value) {
        exprType = ExprType.Self;
        this.value = value;
        beginPos = -1;
        line = -1;
        operator = null;
    }

    public Expr(NumberNode a) {
        beginPos = a.getPos();
        line = a.getLine();
        exprType = ExprType.Self;
        operator = null;
        value = a.getValue();
        name = a.getName();
    }

    public double eval(double a, double b, OpNode type) {
        return switch (type.getType()) {
            case Add -> a + b;
            case Sub -> a - b;
            case Mul -> a * b;
            case Div -> {
                if (Double.compare(0, b) == 0) {
                    throw new RuntimeException(String.format("div by zero, expr: %s, pos: %d, line: %d", b, beginPos, line));
                }
                yield a / b;
            }
        };
    }

    private Expr reverseOp() {
        if (exprType != ExprType.Binary) return this;
        String opname = operator.getName();
        switch (opname) {
            case "+", "-" -> {
                if (opname.equals("+")) opname = "-";
                else opname = "+";
            }
            case "*", "/" -> {
                if (opname.equals("*")) opname = "/";
                else opname = "*";
            }
        }
        operator = new OpNode(opname, operator.getLine(), operator.getPos());
        return this;
    }

    public int getPos() {
        return beginPos;
    }

    public int getLine() {
        return line;
    }

    public int firstPrecedence() {
        if (exprType == ExprType.Self || getA().exprType == ExprType.Self) return getPrecedence();
        else return getA().getPrecedence();
    }

    private Expr rotate() {
        if (b.isSelfNode()) return new Expr(eval(a.eval(), b.eval(), operator));
        double av = new Expr(a, b.getA(), operator, beginPos, line).eval();
        return new Expr(new Expr(av), b.getB(), b.getOperator(), b.getPos(), b.getLine());
    }

    public boolean isSelfNode() {
        return exprType == ExprType.Self;
    }

    public double eval() {
        double result = switch (exprType) {
            case Unary -> throw new RuntimeException("not implemented");
            case Self -> value;
            case Binary -> {
                if (a.hasParen() || b.hasParen()) {
                    if (a.hasParen()) {
                        yield new Expr(new Expr(a.eval()), b, operator, a.beginPos, a.line).eval();
                    } else {
                        yield new Expr(a, new Expr(b.eval()), operator, a.beginPos, a.line).eval();
                    }
                } else if (b.aHasParen()) {
                    Expr newb = new Expr(new Expr(b.getA().eval()), b.getB(), b.getOperator(), b.getA().getPos(), b.getA().getLine());
                    yield new Expr(a, newb, operator, beginPos, line).eval();
                } else if (b.exprType == ExprType.Self) {
                    yield eval(a.eval(), b.eval(), operator);
                } else {
                    if (operator.getType().getPrecedence() >= b.firstPrecedence()) {
                        double tmp = eval(a.eval(), b.getA().eval(), operator);
                        yield new Expr(new Expr(tmp), b.getB(), b.operator, b.getPos(), b.getLine()).eval();
                    } else {
                        Expr newb = b.rotate();
                        yield new Expr(a, newb, operator, beginPos, line).eval();
                    }
                }
            }
        };
//        prettyPrint().ifPresent(s -> System.out.println(String.format("%s -------> %f", s, result)));
        return result;
    }

    public boolean isMulOrDiv() {
        if (exprType != ExprType.Binary) return false;
        else return operator.getType() == OpType.Div || operator.getType() == OpType.Mul;
    }

    public Optional<String> prettyPrint() {
        if (exprType == ExprType.Binary) return Optional.of(String.format("expr %s\t%s\t%s", a, operator, b));
        else return Optional.empty();
    }

    @Override
    public String toString() {
        String content = switch (exprType) {
            case Self -> String.valueOf(value);
            case Binary -> switch (operator.getType()) {
                case Add -> String.format("%s + %s", a.toString(), b.toString());
                case Sub -> String.format("%s - %s", a.toString(), b.toString());
                case Mul -> String.format("%s * %s", a.toString(), b.toString());
                case Div -> String.format("%s / %s", a.toString(), b.toString());
            };
            case Unary -> throw new RuntimeException("not implemented");
        };
        return hasParen ? String.format("(%s)", content) : content;
    }

    public int getPrecedence() {
        if (hasParen) return 2;
        else if (exprType == ExprType.Self) return 0;
        else if (operator.getType() == OpType.Mul || operator.getType() == OpType.Div) return 1;
        else return 0;
    }
}
