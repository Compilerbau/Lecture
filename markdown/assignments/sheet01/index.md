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
## Lexer

*   Was ist der Unterschied zwischen einer tabellenbasierten und einer direkt codierten Implementierung eines Lexers?
*   Welche Strategien kennen Sie, um effizient mit dem Input-Zeichenstrom zu arbeiten? Wie funktionieren diese?
*   Welche Arten von Fehlern gibt es bei der lexikalischen Analyse, wie geht man damit um?


## Token und Lexeme I

Betrachten Sie folgenden Code-Schnipsel:

```python
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

Erstellen Sie für diese fiktive Programmiersprache passende Token. Begründen Sie Ihre Wahl!


## Token und Lexeme II

<!-- XXX
Dragon-Book, S. 114
-->

Teilen Sie folgenden C-Code in passende Token auf.
Vergleichen Sie mit der [Literatur](https://github.com/antlr/grammars-v4).

```c
float limitedSquare(float x) {
    /* returns x*x, but never more than 100.0 */
    return (fabs(x)<=10.0)?x*x:100.0;
}
```

[Lexeme und Token für C-ähnliche Sprachen]{.thema}


## Token und Lexeme III

<!-- XXX
Dragon-Book, S. 115
-->

Teilen Sie folgenden HTML-Schnipsel in passende Token auf.
Vergleichen Sie mit der [Literatur](https://github.com/antlr/grammars-v4).

```html
Here is a photo of <b>my house</b>:
<img src="house.png"><br>

<p>See <a href = "moreHousePix.html">more pictures</a> if
you liked that one.</p>
```

[Lexeme und Token für XML-ähnliche Sprachen]{.thema}


## Tabellenbasierte Implementierung eines Lexer

<!-- XXX
EAC, S. 65
-->

Geben Sie für die folgenden REs jeweils den DFA sowie die passenden
Tabellen an:

a)  $r(\; [0..2]([0..9]|\epsilon) \;|\; [4..9]\;|\;(3(0|1|\epsilon)) \;)$
b)  $a(b|c)^*$

Erweitern Sie das Beispiel aus der Vorlesung (Zeichenketten für Strickmuster: "10L")
um "verschränkt", also "3RV" oder "7LV". Zeichnen Sie den DFA und geben Sie die
Tabellen an.

[Formulierung von DFA und den passenden Tabellen]{.thema}



{{% /challenges %}}
