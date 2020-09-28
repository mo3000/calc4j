package org.batman.blindparser;

import org.batman.blindparser.Ast.NumberNode;
import org.batman.blindparser.Ast.OpNode;

import java.util.*;

public class Parser {
    private int pos;
    private int line;
    int leftBracket;

    public Parser() {
        pos = 0;
        line = 0;
    }

    public List<Expr> parseString(String s) {
        line = 0;
        List<Expr> exprs = new ArrayList<>();
        for (String eachLine : s.split(";")) {
            pos = 0;
            if (hasNext(eachLine)) {
                exprs.add(parseLine(eachLine));
            }
        }
        return exprs;
    }

    private Expr parseLine(String content) {
        leftBracket = 0;
        pos = 0;
        if (hasNext(content)) {
            Expr e = nextExpr(content);
            if (leftBracket > 0) {
                throw new RuntimeException("paren not closed");
            } else if (leftBracket < 0) {
                throw new RuntimeException("paren doesn't match");
            }
            return e;
        } else {
            throw new RuntimeException("empty line at line: " + line);
        }
    }

    public boolean isBlank(char c) {
        if (c == '\n') {
            line++;
        }
        return c == ' ' || c == '\t' || c == '\n';
    }

    private Expr parseParen(final String content) {
        assertNotEmpty(content);
        int parenCountBefore = leftBracket;
        Expr a = nextExpr(content).setBracket();
        if (leftBracket < 0) {
            throw new RuntimeException(String.format("paren doesn't match at %s before %d after %d", a, parenCountBefore, leftBracket));
        }
        return a;
    }

    private void assertNotEmpty(final String content) {
        int beginLine = line;
        if (! hasNext(content)) {
            throw new RuntimeException("content empty start at line " + beginLine);
        }
    }

    private Expr nextExpr(final String content) {
        char c = content.charAt(pos);
        Expr a;
        int beginPos = pos;
        int currentLine = line;

        if (c == '(') {
            pos++;
            leftBracket++;
            a = parseParen(content);
        } else {
            a = nextNumber(content).toExpr();
        }
        if (! hasNext(content)) {
            return a;
        }
        if (content.charAt(pos) == ')') {
            leftBracket--;
            pos++;
            return a;
        }
        OpNode operator = nextOp(content);
        Expr b;
        if (hasNextParen(content)) {
            hasNext(content);
            final int beginPosB = pos;
            final int bline = line;
            pos++;
            leftBracket++;
            b = parseParen(content);
            if (hasNext(content)) {
                if (content.charAt(pos) == ')') {
                    leftBracket--;
                    pos++;
                    return new Expr(a, b, operator, beginPos, bline);
                }
                OpNode op;
                try {
                    op = nextOp(content);
                } catch (RuntimeException e) {
                    System.out.println(String.format("error in nextExpr a: '%s', op: %s b: %s", a, operator, b));
                    throw e;
                }
                assertNotEmpty(content);
                b = new Expr(b, nextExpr(content), op, beginPosB, bline);
            }
        } else {
            assertNotEmpty(content);
            b = nextExpr(content);
        }
        return new Expr(a, b, operator, beginPos, currentLine);
    }

    private boolean hasNextParen(String content) {
        int current = pos;
        int currentLine = line;
        boolean result = hasNext(content) && content.charAt(pos) == '(';
        pos = current;
        line = currentLine;
        return result;
    }


    public boolean hasNext(final String content) {
        while (pos < content.length() && isBlank(content.charAt(pos)))
            pos++;
        return pos < content.length();
    }

    private NumberNode nextNumber(final String content) {
        char firstChar = content.charAt(pos);
        int begin = pos;
        if ((firstChar == '+' || firstChar == '-') && Character.isDigit(content.charAt(pos + 1))
            || Character.isDigit(firstChar)) {
            pos++;
            if (pos < content.length()) {
                char c = content.charAt(pos);
                while (Character.isDigit(c) || c == '.') {
                    pos += 1;
                    if (pos == content.length())
                        break;
                    c = content.charAt(pos);
                }
            }
            return new NumberNode(content.substring(begin, pos), line, begin);
        }
        throw new RuntimeException(String.format("number expected but got '%s' at line %d", content.substring(begin, pos), line));
    }

    private OpNode nextOp(final String content) {
        char c = content.charAt(pos);
        if (! Token.isOperator(c)) {
            throw new RuntimeException(String.format("expected operator, but got '%c'", c));
        }
        pos++;
        return new OpNode(String.valueOf(c), line, pos - 1);
    }

    public void fromFile(String filename) {
    }

}
