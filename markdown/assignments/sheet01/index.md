---
type: assignment
title: "Blatt 01: Scanner und Parser"
author: "BC George, Carsten Gips (FH Bielefeld)"
hidden: true
weight: 1
---


## A1.1: Grammatik

Modifizieren Sie die Grammatik für [**Small C**](https://medium.com/\@efutch/a-small-c-language-definition-for-teaching-compiler-design-b70198531a2f)
folgendermaßen:

1.  Entfernen Sie folgende Elemente:
    *   die `for`-Schleife
    *   das `break`-Statement
    *   die Zufallszahlengenerierung mit `?n`
    *   die Zuweisungsoperatoren `+=`, `-=`, `*=` und `/=`
    *   die Modulo-Operation `%`
    *   den "Elvis"-Operator `?:` (ternäre Abfrage)
    *   die Auto-Inkrement-/-Dekrement-Operatoren `++` und `--`

<!--  -->

2.  Bestimmen Sie die terminalen Symbole Ihrer Grammatik und deren Aufbau.

<!--  -->

3.  Erklären Sie, wo in der Grammatik das Konzept der Zuweisung auftaucht und welche Auswirkungen
    dies auf die erlaubten Programme hat.


## A1.2: Scanner

Entwickeln Sie einen Scanner (Lexer) für Ihren Compiler. Nutzen Sie keinen Scanner-Generator (manuelle
Implementierung gesucht).


## A1.3: Parser

Entwickeln Sie einen LL-Parser für Ihre Grammatik. Nutzen Sie keinen Parser-Generator (manuelle
Implementierung gesucht).

*   Modifizieren Sie dafür Ihre Grammatik, wenn nötig.
*   Legen Sie fest, wie Ihr AST strukturiert sein soll.
*   Bauen Sie einen AST auf.


## A1.4: Visualisierung des AST

Visualisieren Sie Ihren AST mit DOT:

*    [https://de.wikipedia.org/wiki/DOT_(GraphViz)](https://de.wikipedia.org/wiki/DOT_(GraphViz))
*    [http://www.graphviz.org/doc/info/lang.html](http://www.graphviz.org/doc/info/lang.html)

Analysieren Sie die Grammatik, die dem DOT-System zugrunde liegt und programmieren Sie für Ihren Parser
eine Ausgabefunktion, die den AST als DOT-Code ausgibt.
{{% challenges %}}
## LL-Parser

*   Wie kann man aus einer LL(1)-Grammatik einen LL(1)-Parser mit rekursivem
    Abstieg implementieren? Wie "übersetzt" man dabei Token und Regeln?
*   Wie geht man mit Alternativen um? Wie mit optionalen Subregeln?
*   Warum ist Linksrekursion i.A. bei LL-Parsern nicht erlaubt? Wie kann man
    Linksrekursion beseitigen?
*   Wie kann man Vorrangregeln und unterschiedliche Assoziativität implementieren?
*   Wann braucht man mehr als ein Token Lookahead? Geben Sie ein Beispiel an.


## ANTLR

Betrachten Sie folgenden Code-Schnipsel:

```
let fibonacci = fn(x) {
  if (x == 0) {
    return 0;
  } else {
    if (x == 1) {
      return 1;
    } else {
      fibonacci(x - 1) + fibonacci(x - 2);
    }
  }
};

let wuppie = fibonacci(4);
```

Erstellen Sie für diese fiktive Programmiersprache einen LL-Parser in ANTLR.


## Implementierung von LL-Parsern

<!-- XXX S. 68 Drachenbuch -->

Erstellen Sie für folgende Grammatiken **manuell** je einen passenden LL-Parser:

1.  `s : '+' s s | '-' s s | A ;`
2.  `s : '0' s '1' | '0' '1' ;`

::: showme
Tipp: `a : ax | y ;` (wobei `x` und `y` nicht mit `a` beginnen) wird umgeformt zu

```
a : yr;
r : xr | ;
```
:::

[Manuelle Implementierung von LL-Parsern]{.thema}


## Real-World-Parser mit ANTLR: Programmiersprache Lox

<!-- XXX Fibonacci aus Monkey übersetzt nach Lox -->

Betrachten Sie folgenden Code-Schnipsel in der Sprache
["Lox"](https://www.craftinginterpreters.com/the-lox-language.html):

```
fun fib(x) {
    if (x == 0) {
        return 0;
    } else {
        if (x == 1) {
            return 1;
        } else {
            fib(x - 1) + fib(x - 2);
        }
    }
}

var wuppie = fib(4);
```

Erstellen Sie für diese fiktive Sprache einen Parser mit ANTLR. Die genauere Sprachdefinition finden Sie unter
[craftinginterpreters.com/the-lox-language.html](https://www.craftinginterpreters.com/the-lox-language.html).

[Erstellen eines Parsers für eine Programmiersprache mit ANTLR]{.thema}

{{% /challenges %}}
