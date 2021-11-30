import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.*;

public class TestMyVisitor {
    public static void main(String[] args) throws Exception {
        calc2Lexer lexer = new calc2Lexer(CharStreams.fromStream(System.in));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        calc2Parser parser = new calc2Parser(tokens);

        ParseTree tree = parser.r();    // Start-Regel
        System.out.println(tree.toStringTree(parser));

        MyVisitor eval = new MyVisitor();
        System.out.println(eval.visit(tree));
    }

    public static class MyVisitor extends calc2BaseVisitor<Integer> {

        public Integer visitMULT(calc2Parser.MULTContext ctx) {
            return visit(ctx.e1) * visit(ctx.e2);   // {$v = $e1.v * $e2.v;}
        }

        public Integer visitADD(calc2Parser.ADDContext ctx) {
            return visit(ctx.e1) + visit(ctx.e2);   // {$v = $e1.v + $e2.v;}
        }

        public Integer visitZAHL(calc2Parser.ZAHLContext ctx) {
            return Integer.valueOf(ctx.DIGIT().getText());
        }
    }
}
