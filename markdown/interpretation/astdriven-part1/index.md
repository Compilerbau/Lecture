---
type: lecture-cg
title: "AST-basierte Interpreter: Basics"
menuTitle: "AST-basierte Interpreter 1"
author: "Carsten Gips (FH Bielefeld)"
weight: 2
readings:
  - key: "Levine2009: Bison"
    comment: "Kapitel 6"
  - key: "Parr2014"
    comment: "Kapitel 6.4 und 8.4"
  - key: "Nystrom2018"
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


## Aufgaben im Interpreter

::: notes
Im Allgemeinen reichen einfache syntaxgesteuerte Interpreter nicht aus. Normalerweise simuliert
ein Interpreter die Ausführung eines Programms durch den Computer. D.h. der Interpreter muss
über die entsprechenden Eigenschaften verfügen: Prozessor, Code-Speicher, Datenspeicher, Stack ...
:::

:::::: columns
::: {.column width="25%"}

```c
int x = 42;
int f(int x) {
    int y = 9;
    return y+x;
}

x = f(x);
```

:::
::: {.column width="70%"}

\vspace{3mm}

*   Aufbauen des AST  [... => Lexer+Parser]{.notes}
*   Auflösen von Symbolen/Namen  [... => Symboltabellen, Resolving]{.notes}
*   Type-Checking und -Inference  [... => Semantische Analyse (auf Symboltabellen)]{.notes}

\smallskip

*   Speichern von Daten: Name+Wert vs. Adresse+Wert  [Erinnerung: Data-Segment und Stack im virtuellen Speicher]{.notes}
*   Ausführen von Anweisungen  [Text-Segment im virtuellen Speicher; hier über den AST]{.notes}
*   Aufruf von Funktionen und Methoden  [Kontextwechsel nötig: Was ist von wo aus sichtbar?]{.notes}

:::
::::::


## AST-basierte Interpreter: Visitor-Dispatcher

```java
public Object eval(AST t) {
    switch ( t.getType() ) {
        case Parser.BLOCK : block(t); break;
        case Parser.ASSIGN : assign(t); break;
        case Parser.RETURN : ret(t); break;
        case Parser.IF : ifstat(t); break;
        case Parser.CALL : return call(t);
        case Parser.ADD : return add(t);
        case Parser.MUL : return op(t);
        case Parser.INT : return Integer.parseInt(t.getText());
        case Parser.ID : return load(t);
        default : // catch unhandled node types
            ...
    }
    return null;
}
```

[[Hinweis "Read-Eval-Print-Loop" (REPL)]{.bsp}]{.slides}

::: notes
Nach dem Aufbau des AST durch Scanner und Parser und der semantischen Analyse
anhand der Symboltabellen müssen die Ausdrücke (*expressions*) und Anweisungen
(*statements*) durch den Interpreter ausgewertet werden. Eine Möglichkeit dazu
ist das Traversieren des AST mit dem Visitor-Pattern. Basierend auf dem Typ
des aktuell betrachteten AST-Knotens wird entschieden, wie damit umgegangen
werden soll. Dies erinnert an den Aufbau der Symboltabellen ...

Die `eval()`-Methode bildet das Kernstück des (AST-traversierenden) Interpreters.
Hier wird passend zum aktuellen AST-Knoten die passende Methode des Interpreters
aufgerufen.

**Hinweis**: Im obigen Beispiel wird nicht zwischen der Auswertung von
Ausdrücken und Anweisungen unterschieden, es wird die selbe Methode `eval`
genutzt. Allerdings liefern Ausdrücke einen Wert zurück (erkennbar am `return`
im jeweiligen `switch/case`-Zweig), während Anweisungen keinen Wert liefern.


In den Beispielen auf den Folien wird davon ausgegangen, dass ein komplettes
Programm eingelesen, geparst, vorverarbeitet und dann interpretiert wird.

Für einen interaktiven Interpreter würde man in einer Schleife die Eingaben
lesen, parsen und vorverarbeiten und dann interpretieren. Dabei würde jeweils
der AST und die Symboltabelle *ergänzt*, damit die neuen Eingaben auf frühere
verarbeitete Eingaben zurückgreifen können. Durch die Form der Schleife
"Einlesen -- Verarbeiten -- Auswerten" hat sich auch der Name "*Read-Eval-Loop*"
 bzw. "*Read-Eval-Print-Loop*" (**REPL**) eingebürgert.
:::


## Auswertung von Literalen und Ausdrücken

*   Typen mappen: Zielsprache => Implementierungssprache

    ::: notes
    Die in der Zielsprache verwendeten (primitiven) Typen müssen
    auf passende Typen der Sprache, in der der Interpreter selbst
    implementiert ist, abgebildet werden.

    Beispielsweise könnte man den Typ `nil` der Zielsprache auf den
    Typ `null` des in Java implementierten Interpreters abbilden, oder
    den Typ `number` der Zielsprache auf den Typ `Double` in Java
    mappen.
    :::

\smallskip

*   Literale auswerten:

    ```yacc
    INT: [0-9]+ ;
    ```

    ```java
    case Parser.INT : return Integer.parseInt(t.getText());
    ```

    ::: notes
    Das ist der einfachste Teil ... Die primitiven Typen der
    Zielsprache, für die es meist ein eigenes Token gibt, müssen
    als Datentyp der Interpreter-Programmiersprache ausgewertet
    werden.
    :::

\smallskip

*   Ausdrücke auswerten:

    ```yacc
    add: e1=expr "+" e2=expr ;
    ```

    ```java
    Object add(AST t) {
        Object lhs = eval(t.e1());
        Object rhs = eval(t.e2());
        return (double)lhs + (double)rhs;
    }
    ```

    ::: notes
    Die meisten möglichen Fehlerzustände sind bereits durch den Parser
    und bei der semantischen Analyse abgefangen worden. Falls zur Laufzeit
    die Auswertung der beiden Summanden keine Zahl ergibt, würde eine
    Java-Exception geworfen, die man an geeigneter Stelle fangen und
    behandeln muss. Der Interpreter soll sich ja nicht mit einem Stack-Trace
    verabschieden, sondern soll eine Fehlermeldung präsentieren und danach
    normal weiter machen ...
    :::


## Kontrollstrukturen

```yacc
ifstat: 'if' expr 'then' s1=stat ('else' s2=stat)? ;
```

\bigskip

```java
Void ifstat(AST t) {
    if (eval(t.expr())) {
        eval(t.s1());
    } else {
        if (t.s2() != null) {
            eval(t.s2());
        }
    }
}
```

::: notes
Analog können die anderen bekannten Kontrollstrukturen umgesetzt werden,
etwa `switch/case`, `while` oder `for`.

Dabei können erste Optimierungen vorgenommen werden: Beispielsweise könnten
`for`-Schleifen im Interpreter in `while`-Schleifen transformiert werden,
wodurch im Interpreter nur ein Schleifenkonstrukt implementiert werden
müsste.
:::


## Zustände: Auswerten von Anweisungen

:::::: columns
::: {.column width="25%"}

\vspace{4mm}

```c
int x = 42;
float y;
{
    int x;
    x = 1;
    y = 2;
    { int y = x; }
}
```

:::
::: {.column width="75%"}

\pause

![](images/nested_envs.png)
<!-- TODO (Quelle?) -->

:::
::::::

::: notes
Das erinnert nicht nur zufällig an den Aufbau der Symboltabellen :-)

Und so lange es nur um Variablen ginge, könnte man die Symboltabellen für das
Speichern der Werte nutzen. Allerdings müssen wir noch Funktionen und Strukturen
bzw. Klassen realisieren, und spätestens dann kann man die Symboltabelle nicht
mehr zum Speichern von Werten einsetzen. Also lohnt es sich, direkt neue
Strukturen für das Halten von Variablen und Werten aufzubauen.
:::


## Detail: Felder im Interpreter

::: notes
Eine mögliche Implementierung für einen Interpreter basierend auf einem
ANTLR-Visitor ist nachfolgend gezeigt.
:::

```java
public class Interpreter extends BaseVisitor<Object> {
    private AST root;

    final Environment globals = new Environment();
    private Environment env = globals;

    public Interpreter(AST t) {
        super();
        root = t;
    }
}
```

[[Quelle: nach [@Nystrom2018], Kapitel "Functions"]{.origin}]{.notes}


## Ausführen einer Variablendeklaration

```yacc
varDecl: "var" ID ("=" expr)? ";" ;
```

\bigskip

```java
Void varDecl(AST t) {
    // jede deklarierte Variable in aktuelles Environment packen
    String name = t.ID().getText();

    Object value = null; // TODO: Typ der Variablen beachten (Defaultwert)
    if (t.expr() != null) value = eval(t.expr());

    env.define(name, value);

    return null;
}
```

::: notes
Wenn wir bei der Traversierung des AST mit `eval()` bei einer Variablendeklaration
vorbeikommen, also etwa `int x;` oder `int x = wuppie + fluppie;`, dann wird im
**aktuellen** Environment der String "x" sowie der Wert (im zweiten Fall) eingetragen.
:::


## Ausführen einer Zuweisung

```yacc
assign: ID "=" expr;
```

\bigskip

```java
void assign(AST t) {
    String lhs = t.ID().getText();
    Object value = eval(t.expr());

    env.assign(lhs, value);
}

class Environment {
    void assign(String n, Object v) {
        if (values.containsKey(n)) { values.put(n, v); return; }
        if (enclosing != null) { enclosing.assign(n, v); return; }
        throw new RuntimeError("Undefined variable '" + n + "'.");
    }
}
```

::: notes
[Quelle: nach [@Parr2010, S.235], nach [@Nystrom2018], Kapitel "Statements and State"]{.origin}

Wenn wir bei der Traversierung des AST mit `eval()` bei einer Zuweisung
vorbeikommen, also etwa `x = 7;` oder `x = wuppie + fluppie;`, dann wird
zunächst im aktuellen Environment die rechte Seite der Zuweisung ausgewertet
(Aufruf von `eval()`). Anschließend wird der Wert für die Variable im
Environment eingetragen: Entweder sie wurde im aktuellen Environment früher
bereits definiert, dann wird der neue Wert hier eingetragen. Ansonsten wird
entlang der Verschachtelungshierarchie gesucht und entsprechend eingetragen.
Falls die Variable nicht gefunden werden kann, wird eine Exception ausgelöst.

An dieser Stelle kann man über die Methode `assign` in der Klasse `Environment`
dafür sorgen, dass nur bereits deklarierte Variablen zugewiesen werden dürfen.
Wenn man stattdessen wie etwa in Python das implizite Erzeugen neuer
Variablen erlaubten möchte, würde man statt `Environment#assign` einfach
`Environment#define` nutzen ...

*Anmerkung*: Der gezeigte Code funktioniert nur für normale Variablen, nicht
für Zugriffe auf Attribute einer Struct oder Klasse!
:::


## Blöcke: Umgang mit verschachtelten Environments

```yacc
block:  '{' stat* '}' ;
```

\bigskip

```java
Void block(AST t) {
    Environment prev = env;
    try {
        env = new Environment(env);
        for (AST s : t.stat()) {
            eval(s);
        }
    } finally { env = prev; }
    return null;
}
```

::: notes
[Quelle: nach [@Nystrom2018], Kapitel "Statements and State"]{.origin}

Beim Interpretieren von Blöcken muss man einfach nur eine weitere
Verschachtelungsebene für die Environments anlegen und darin dann
die Anweisungen eines Blockes auswerten ...

**Wichtig**: Egal, was beim Auswerten der Anweisungen in einem Block
passiert: Es muss am Ende die ursprüngliche Umgebung wieder hergestellt
werden (`finally`-Block).
:::


## Wrap-Up

*   Interpreter simulieren die Programmausführung
    *   Namen und Symbole auflösen
    *   Speicherbereiche simulieren
    *   Code ausführen: Read-Eval-Loop

\smallskip

*   Traversierung des AST: `eval(AST t)` als Visitor-Dispatcher
*   Scopes mit `Environment` (analog zu Symboltabellen)
*   Interpretation von Blöcken und Variablen (Deklaration, Zuweisung)







<!-- DO NOT REMOVE - THIS IS A LAST SLIDE TO INDICATE THE LICENSE AND POSSIBLE EXCEPTIONS (IMAGES, ...). -->
::: slides
## LICENSE
![](https://licensebuttons.net/l/by-sa/4.0/88x31.png)

Unless otherwise noted, this work is licensed under CC BY-SA 4.0.

### Exceptions
*   TODO (what, where, license)
:::
