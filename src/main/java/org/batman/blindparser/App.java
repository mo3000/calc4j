package org.batman.blindparser;

import org.batman.blindparser.Ast.NumberNode;

public class App {
    public static void main(String[] args) {
        Parser parser = new Parser();
//        for (Expr expr : parser.parseString("(2 + 6) * (5 - (3 + (10 - 3 * 5 / 6 - (6 / 3) - 3)) * 12)")) {
//            System.out.println(expr);
//            System.out.println(expr.eval());
//        }
        Expr expr = parser.parseString("(2 + 6) * (5 - (3 + (10 - 3 * 5 / 6 - (6 / 3) - 3)) * 12)").get(0);
//        Expr expr = parser.parseString("10.0 - 3.0 * 5.0 / 6.0 - (6.0 / 3.0) - 3.0").get(0);
//        Expr expr = parser.parseString("10.0 - 3.0 * 5.0 - 2.0").get(0);
//        Expr expr = parser.parseString("8 + 3 * 6 * -3 - 2 / 5 / 6").get(0);
//        Expr expr = parser.parseString("1 -(3 - 2 )+ 5 *6").get(0);
//        Expr expr = parser.parseString("(3.0 - 2.0) + 5.0 * 6.0").get(0);
        System.out.println(expr);
        System.out.println(expr.getA());
        System.out.println(expr.getB());
        System.out.println(expr.eval());
    }
}
