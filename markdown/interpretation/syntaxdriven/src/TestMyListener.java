import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.*;

public class TestMyListener {
    public static void main(String[] args) throws Exception {
        calc2Lexer lexer = new calc2Lexer(CharStreams.fromStream(System.in));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        calc2Parser parser = new calc2Parser(tokens);

        ParseTree tree = parser.r();    // Start-Regel
        System.out.println(tree.toStringTree(parser));

        ParseTreeWalker walker = new ParseTreeWalker();
        MyListener eval = new MyListener();
        walker.walk(eval, tree);
        System.out.println(eval.stack.pop());
    }

    public static class MyListener extends calc2BaseListener {
        Stack<Integer> stack = new Stack<Integer>();

        public void exitMULT(calc2Parser.MULTContext ctx) {
            int right = stack.pop();
            int left = stack.pop();
            stack.push(left * right);   // {$v = $e1.v * $e2.v;}
        }

        public void exitADD(calc2Parser.ADDContext ctx) {
            int right = stack.pop();
            int left = stack.pop();
            stack.push(left + right);   // {$v = $e1.v + $e2.v;}
        }

        public void exitZAHL(calc2Parser.ZAHLContext ctx) {
            stack.push(Integer.valueOf(ctx.DIGIT().getText()));
        }
    }
}
