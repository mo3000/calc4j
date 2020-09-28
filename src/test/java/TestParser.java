import org.batman.blindparser.Expr;
import org.batman.blindparser.Parser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TestParser {
    final double delta = 0.0000000001;
    private Parser parser;

    public TestParser() {
        parser = new Parser();
    }

    @Test
    public void testOneLineString() {
        assertEqualOne(30, "1 -(3 - 2 )+ 5 *6;");
        assertEqualOne(0, "1+ (3 - 2)- 6 / 3;");
        assertEqualOne(-4.2, "-51 *(3 - 2 )/ 5 +6;");
        assertEqualOne(-920, "(2 + 6) * ( 5 - (3 + 7) * 12);");
        assertEqualOne(-920, "(2 + 6) * ( 5 - (3 + (10 - 3 + 5 - (6 / 3) - 3) ) * 12);");
        assertEqualOne(-488, "(2 + 6) * ((( 5 - (3 + (10 - 3 * 5 / 6- (6 / 3) - 3) ) * 12)));");
        assertEqualOne(1.4785, "((5.0/8.0)*1.0-3.668+6.0)/ 2.0;");
        assertEqualOne(-33.3675, "((5.0/8.0)*1.0-3.668* 20+6.0)/ 2.0;");
        assertEqualOne(21.25, "2 /2+3 * 4.75- -6");
    }

    @Test
    public void testMultiLineString() {
        assertEqualMany(List.of(30.0, 0.0, -4.2),
            "1 -(3 - 2 )+ 5 *6; 1+ (3 - 2)- 6 / 3;" +
                    "-51 *(3 - 2 )/ 5 +6;");
    }

    @Test
    public void testShouldThrow() {
        assertThrows(RuntimeException.class, () -> parser.parseString("(3 + 2) / (4 - 4"));
        assertThrows(RuntimeException.class, () -> parser.parseString("(3 + 2) / (4 - 4))"));
        assertThrows(RuntimeException.class, () ->
            parser.parseString("(3 + 2) / (4 - 4);").get(0).eval());
    }

    public void equal(double target, double v) {
        assertEquals(target, v, delta);
    }

    public void assertEqualOne(double target, String expr) {
        equal(target, parser.parseString(expr).get(0).eval());
    }

    public void assertEqualMany(List<Double> target, String exprs) {
        List<Expr> result = parser.parseString(exprs);
        assertEquals(target.size(), result.size());
        for (int i = 0; i < target.size(); i++) {
            equal(target.get(i), result.get(i).eval());
        }
    }

}
