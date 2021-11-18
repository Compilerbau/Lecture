---
type: lecture-cg
title: "Syntaxgesteuerte Interpreter"
author: "Carsten Gips (FH Bielefeld)"
weight: 1
readings:
  - key: "Levine2009: Bison"
    comment: "Kapitel 6"
  - key: "Parr2014"
    comment: "Kapitel 6.4 und 8.4"
  - key: "Nystrom2021"
    comment: "Kapitel 'A Tree-Walk Interpreter' "
  - key: "Parr2010"
    comment: "Kapitel 8 und 9"
  - key: "Grune2012"
    comment: "Kapitel 6"
  - key: "Mogensen2017"
    comment: "Kapitel 4"
assignments:
  - topic: sheet05
youtube:
  - id: TODO
fhmedia:
  - link: "TODO"
    name: "Direktlink FH-Medienportal: CB Syntaxgesteuerte Interpreter"
---


## Überblick Interpreter

::: center
![](images/interpreter.png){width="80%"}
:::

::: notes
Beim Interpreter durchläuft der Sourcecode nur das Frontend, also die Analyse.
Es wird kein Code erzeugt, stattdessen führt der Interpreter die Anweisungen
im AST bzw. IC aus. Dazu muss der Interpreter mit den Eingabedaten beschickt
werden.

Es gibt verschiedene Varianten, beispielsweise:
:::

\bigskip

*   Syntaxgesteuerte Interpreter

    ::: notes
    *   Einfachste Variante, wird direkt im Parser mit abgearbeitet
    *   Keine Symboltabellen, d.h. auch keine Typprüfung oder Vorwärtsdeklarationen o.ä.
        (d.h. erlaubt nur vergleichsweise einfache Sprachen)
    *   Beispiel: siehe nächste Folie
	:::

*   AST-basierte Interpreter

    ::: notes
    *   Nutzt den AST und Symboltabellen
    *   Beispiel: siehe weiter unten
	:::

*   Stack-basierte Interpreter

    ::: notes
    *   Simuliert eine *Stack Machine*, d.h. hält alle (temporären) Werte auf einem Stack
    *   Arbeitet typischerweise auf bereits stark vereinfachtem Zwischencode (IR),
        etwa Bytecode
	:::

*   Register-basierte Interpreter

    ::: notes
    *   Simuliert eine *Register Machine*, d.h. hält alle (temporären) Werte in virtuellen
        Prozessor-Registern
    *   Arbeitet typischerweise auf bereits stark vereinfachtem Zwischencode (IR),
        etwa Bytecode
	:::

::: notes
Weiterhin kann man Interpreter danach unterscheiden, ob sie interaktiv sind oder nicht.
Python kann beispielsweise direkt komplette Dateien verarbeiten oder interaktiv Eingaben
abarbeiten. Letztlich kommen dabei aber die oben dargestellten Varianten zum Einsatz.
:::


## Syntaxgesteuerte Interpreter: Attributierte Grammatiken

```yacc
expr returns [int v]
    : e1=expr '*' e2=expr  {$v = $e1.v * $e2.v;}
    | e1=expr '+' e2=expr  {$v = $e1.v + $e2.v;}
    | DIGIT                {$v = $DIGIT.int;}
    ;

DIGIT : [0-9] ;
```

::: notes
[Beispiel: calc.g4]{.bsp}

Die einfachste Form des Interpreters wird direkt beim Parsen ausgeführt und kommt ohne AST aus.
Der Nachteil ist, dass der AST dabei nicht vorverarbeitet werden kann, insbesondere entfallen
semantische Prüfungen weitgehend.
:::


## Eingebettete Aktionen in ANTLR I

::: notes
Erinnerung: ANTLR generiert einen LL-Parser, d.h. es wird zu jeder Regel eine entsprechende Methode
generiert.

Mit Hilfe von `returns [int v]` definiert man den Rückgabewert der Regel (Methode) `expr()`, d.h. es wird
eine lokale Variable `int v` angelegt. Auf diese kann in den Aktionen mit `$v` zugegriffen werden.

Analog kann auf die Eigenschaften der Token und Sub-Regeln zugegriffen werden: `$name.eigenschaft`. Dabei gibt
es bei Token Eigenschaften wie `text` (gematchter Text bei Token), `type` (Typ eines Tokens), `int` (Integerwert
eines Tokens, entspricht `Integer.valueOf($Token.text)`). Parser-Regeln haben u.a. ein `text`-Attribut und ein
spezielles Kontext-Objekt (`ctx`).

Die allgemeine Form lautet:
:::

```
rulename[args] returns [retvals] locals [localvars] : ... ;
```

::: notes
Dabei werden die in `[...]` genannten Parameter mit Komma getrennt (Achtung: Abhängig von Zielsprache!).

Beispiel:
:::

\bigskip
\bigskip

```yacc
// Return the argument plus the integer value of the INT token
add[int x] returns [int result] : '+=' INT {$result = $x + $INT.int;} ;
```


## Eingebettete Aktionen in ANTLR II

```yacc
@members {
    int count = 0;
}

list
    @after {System.out.println(count);}
    : INT {count++;} (',' INT {count++;} )*
    ;

INT : [0-9]+ ;
WS  : [ \r\t\n]+ -> skip ;
```

::: notes
[Beispiel: count.g4]{.bsp}

Mit `@members { ... }` können im generierten Parser weitere Attribute angelegt werden, die in den Regeln normal
genutzt werden können.

Die mit `@after` markierte Aktion wird am Ende der Regel `list` ausgeführt. Analog existiert `@init`.
:::


::: notes
## ANTLR: Traversierung des AST und Auslesen von Kontext-Objekten

Mit dem obigen Beispiel, welches dem Einsatz einer L-attributierten SDD in ANTLR
entspricht, können einfache Aufgaben bereits beim Parsen erledigt werden.

Für den etwas komplexeren Einsatz von attributierten Grammatiken kann man die von
ANTLR erzeugten Kontext-Objekte für die einzelnen AST-Knoten nutzen und über den
AST mit dem Visitor- oder dem Listener-Pattern iterieren.

Die Techniken sollen im Folgenden kurz vorgestellt werden.
:::


::: notes
### ANTLR: Kontext-Objekte für Parser-Regeln
:::

::: slides
## ANTLR: Kontext-Objekte für Parser-Regeln
:::

```{.yacc size="footnotesize"}
s     : field {List<EContext> x = $field.ctx.e();} ;
field : e '.' e ;
```

\bigskip

```{.java size="footnotesize"}
public final SContext s() throws RecognitionException { ... }
public final FieldContext field() throws RecognitionException { ... }

public static class SContext extends ParserRuleContext {
    public FieldContext field;
    public FieldContext field() { ... }
    ...
}
public static class FieldContext extends ParserRuleContext {
    public EContext e(int i) { ... }  // get ith e context
    public List<EContext> e() { ... } // return ALL e contexts
    ...
}
```
![](images/ParserRuleContext.png)

::: notes
Jede Regel liefert ein passend zu dieser Regel generiertes Kontext-Objekt zurück. Darüber kann man das/die
Kontextobjekt(e) der Sub-Regeln abfragen.

In der Aktion fragt man das Kontextobjekt über `ctx` ab.

Für einfache Regel-Aufrufe liefert die parameterlose Methode nur ein Kontextobjekt (statt einer Liste)
zurück:

```yacc
inc   : e '++' ;
```

```java
public final IncContext inc() throws RecognitionException { ... }

public static class IncContext extends ParserRuleContext {
    public EContext e() { ... } // return context object associated with e
    ...
}
```

![](images/ParserRuleContext2.png)

**Anmerkung**: ANTLR generiert nur dann Felder für die Regel-Elemente im Kontextobjekt,
wenn diese in irgendeiner Form referenziert werden. Dies kann beispielsweise durch
Benennung (Definition eines Labels, siehe nächste Folie) oder durch Nutzung in einer
Aktion geschehen.
:::


::: notes
### ANTLR: Benannte Regel-Elemente oder Alternativen

```yacc
stat  : 'return' value=e ';'  # Return
      | 'break' ';'           # Break
      ;
```

\bigskip

```java
public static class StatContext extends ParserRuleContext { ... }
public static class ReturnContext extends StatContext {
    public EContext value;
    public EContext e() { ... }
}
public static class BreakContext extends StatContext { ... }
```

![](images/ParserRuleContext3.png)

Mit `value=e` wird der Aufruf der Regel `e` mit dem Label `value` belegt, d.h. man kann
mit `$e.text` oder `$value.text` auf das `text`-Attribut von `e` zugreifen. Falls es in
einer Produktion mehrere Aufrufe einer anderen Regel gibt, *muss* man für den Zugriff
auf die Attribute eindeutige Label vergeben.

Analog wird für die beiden Alternativen je ein eigener Kontext erzeugt.
:::


::: notes
### ANTLR: Arbeiten mit dem Listener-Pattern
:::

::: slides
## ANTLR: Arbeiten mit dem Listener-Pattern
:::

::: notes
ANTLR (generiert auf Wunsch) zur Grammatik passende Listener (Interface und leere Basisimplementierung).
Beim Traversieren mit dem Default-`ParseTreeWalker` wird der Parse-Tree mit Tiefensuche abgelaufen und
jeweils beim Eintritt in bzw. beim Austritt aus einen/m Knoten der passende Listener mit dem passenden
Kontext-Objekt aufgerufen.

Damit kann man die Grammatik "für sich" halten, d.h. unabhängig von einer konkreten Zielsprache
und die Aktionen über die Listener (oder Visitors, s.u.) ausführen.
:::

```{.yacc size="footnotesize"}
expr : e1=expr '*' e2=expr  # MULT
     | e1=expr '+' e2=expr  # ADD
     | DIGIT                # ZAHL
     ;
```

\smallskip

::: notes
ANTLR kann zu dieser Grammatik einen passenden Listener (Interface) generieren:

```java
public interface calc2Listener extends ParseTreeListener {
    void enterADD(calc2Parser.ADDContext ctx);
    void exitADD(calc2Parser.ADDContext ctx);
    void enterZAHL(calc2Parser.ZAHLContext ctx);
    void exitZAHL(calc2Parser.ZAHLContext ctx);
    void enterMULT(calc2Parser.MULTContext ctx);
    void exitMULT(calc2Parser.MULTContext ctx);
}
```

Weiterhin generiert ANTLR eine leere Basisimplementierung:

```java
public class calc2BaseListener implements calc2Listener {
    @Override public void enterR(calc2Parser.RContext ctx) { }
    @Override public void exitR(calc2Parser.RContext ctx) { }
    @Override public void enterADD(calc2Parser.ADDContext ctx) { }
    @Override public void exitADD(calc2Parser.ADDContext ctx) { }
    @Override public void enterZAHL(calc2Parser.ZAHLContext ctx) { }
    @Override public void exitZAHL(calc2Parser.ZAHLContext ctx) { }
    @Override public void enterMULT(calc2Parser.MULTContext ctx) { }
    @Override public void exitMULT(calc2Parser.MULTContext ctx) { }
    @Override public void enterEveryRule(ParserRuleContext ctx) { }
    @Override public void exitEveryRule(ParserRuleContext ctx) { }
    @Override public void visitTerminal(TerminalNode node) { }
    @Override public void visitErrorNode(ErrorNode node) { }
}
```

Von dieser Basisklasse leitet man einen eigenen Listener ab und implementiert
die Methoden, die man benötigt.
:::

```{.java size="footnotesize"}
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
```

![](images/ParseTreeListener.png)

::: notes
Anschließend baut man das alles in eine Traversierung des Parse-Trees ein:

```java
public class TestMyListener {
    public static class MyListener extends calc2BaseListener {
        ...
    }

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
}
```

[Konsole: TestMyListener.java und calc2.g4]{.bsp}
:::


::: notes
### ANTLR: Arbeiten mit dem Visitor-Pattern
:::

::: slides
## ANTLR: Arbeiten mit dem Visitor-Pattern
:::

::: notes
ANTLR (generiert ebenfalls auf Wunsch) zur Grammatik passende Visitoren (Interface und leere Basisimplementierung).
Hier muss man allerdings selbst für eine geeignete Traversierung des Parse-Trees sorgen. Dafür hat man mehr
Freiheiten im Vergleich zum Listener-Pattern, insbesondere im Hinblick auf Rückgabewerte.
:::

```{.yacc size="footnotesize"}
expr : e1=expr '*' e2=expr  # MULT
     | e1=expr '+' e2=expr  # ADD
     | DIGIT                # ZAHL
     ;
```

\bigskip

::: notes
ANTLR kann zu dieser Grammatik einen passenden Visitor (Interface) generieren:

```java
public interface calc2Visitor<T> extends ParseTreeVisitor<T> {
    T visitR(calc2Parser.RContext ctx);
    T visitADD(calc2Parser.ADDContext ctx);
    T visitZAHL(calc2Parser.ZAHLContext ctx);
    T visitMULT(calc2Parser.MULTContext ctx);
}
```

Weiterhin generiert ANTLR eine leere Basisimplementierung:

```java
public class calc2BaseVisitor<T> extends AbstractParseTreeVisitor<T> implements calc2Visitor<T> {
    @Override public T visitR(calc2Parser.RContext ctx) { return visitChildren(ctx); }
    @Override public T visitADD(calc2Parser.ADDContext ctx) { return visitChildren(ctx); }
    @Override public T visitZAHL(calc2Parser.ZAHLContext ctx) { return visitChildren(ctx); }
    @Override public T visitMULT(calc2Parser.MULTContext ctx) { return visitChildren(ctx); }
}
```

Von dieser Basisklasse leitet man einen eigenen Visitor ab und überschreibt
die Methoden, die man benötigt. Wichtig ist, dass man selbst für das "Besuchen"
der Kindknoten sorgen muss (rekursiver Aufruf der geerbten Methode `visit()`).
:::

```{.java size="footnotesize"}
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
```

![](images/ParseTreeVisitor.png)

::: notes
Anschließend baut man das alles in eine manuelle Traversierung des Parse-Trees ein:

```java
public class TestMyVisitor {
    public static class MyVisitor extends calc2BaseVisitor<Integer> {
        ...
    }

    public static void main(String[] args) throws Exception {
        calc2Lexer lexer = new calc2Lexer(CharStreams.fromStream(System.in));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        calc2Parser parser = new calc2Parser(tokens);

        ParseTree tree = parser.r();    // Start-Regel
        System.out.println(tree.toStringTree(parser));

        MyVisitor eval = new MyVisitor();
        System.out.println(eval.visit(tree));
    }
}
```

[Konsole: TestMyVisitor.java und calc2.g4]{.bsp}
:::


## Wrap-Up

*   Interpreter simulieren die Programmausführung

\smallskip

*   Syntaxgesteuerter Interpreter (attributierte Grammatiken)
*   Beispiel ANTLR4: Eingebettete Aktionen, Kontextobjekte, Visitors/Listeners [(AST-Traversierung)]{.notes}







<!-- DO NOT REMOVE - THIS IS A LAST SLIDE TO INDICATE THE LICENSE AND POSSIBLE EXCEPTIONS (IMAGES, ...). -->
::: slides
## LICENSE
![](https://licensebuttons.net/l/by-sa/4.0/88x31.png)

Unless otherwise noted, this work is licensed under CC BY-SA 4.0.
:::
