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
## Was ist kein regulärer Ausdruck?
Sind $a^lb^mc^{l+m}$ und $(a | b)^3$ reguläre Ausdrücke? Kann man eine oder beide Angaben in einen regulären Ausdruck umwandeln?
[Syntax regulärer Ausdrücke]{.thema}


## Ausschluss von Teilwörtern (REGEX)
Entwickeln Sie einen regulären Ausdruck für die Menge aller Wörter über $\{a, b\}$, die nicht das Teilwort *ab* enthalten.
[Formulierung von regulären Ausdrücken]{.thema}


## Vereinigung von DFAs)
Entwickeln Sie je einen DFA für die Wörter *aa*, *aaba*, *aab*, *aabab*. Vereinigen Sie die Automaten zu einem, der genau all diese Wörter akzeptiert.
[Entwicklung eines DFA]{.thema}


## Einschluss bestimmter Wörter (DFA)
Entwicklen Sie einen DFA, der alle Binärzahlen größer Null mit einer geraden Anzahl von Einsen und einer ungeraden Anzahl von Nullen akzeptiert.
[Entwicklung eines DFA]{.thema}


## Einschluss bestimmter Wörter (NFA)
Konstruieren Sie einen NFA, der die Menge aller Wörter *w* über $\{a, b, c\}$ akzeptiert, deren letzter Buchstabe vorher schon in *w* enthalten war.
[Entwicklung eines NFA]{.thema}


## NFA nach DFA
Wandeln Sie den NFA aus der vorigen Aufgabe in einen DFA um.
[Konvertierung eines NFA in einen DFA]{.thema}


## Reguläre Grammatik
Schreiben Sie eine reguläre Grammatik für die Sprache aus den letzten beiden Aufgaben.
[Entwicklung einer regulären Grammatik]{.thema}
{{% /challenges %}}
