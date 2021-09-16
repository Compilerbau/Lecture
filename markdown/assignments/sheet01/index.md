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
## Fahrstuhlgrammatik

<!-- XXX S. 157, EAC -->

<!-- XXX `s : s '(' s ')' s | ;` -->

Stellen Sie sich einen Fahrstuhl vor, der mit Hilfe von zwei Kommandos gesteuert wird:
$\uparrow$ bewegt den Fahrstuhl ein Stockwerk nach oben, $\downarrow$ bewegt den Fahrstuhl um ein
Stockwerk nach unten.

Gehen Sie weiter davon aus, dass das Gebäude beliebig hoch ist und der Fahrstuhl in Etage
$x$ startet.

Geben Sie eine LL(1)-Grammatik an, die beliebige Steuersequenzen erzeugt, die folgende
Nebenbedingungen einhalten:

1.  Der Fahrstuhl fährt nie tiefer als Etage $x$.
2.  Am Ende der Sequenz wird der Fahrstuhl wieder zur Etage $x$ gebracht.

Die leere Sequenz sei erlaubt.

\smallskip

**Beispiele**:

*   $\uparrow\uparrow\downarrow\downarrow$ und $\uparrow\downarrow\uparrow\downarrow$ sind gültige Sequenzen
*   $\uparrow\downarrow\downarrow\uparrow$ und $\uparrow\downarrow\downarrow$ sind keine gültigen Sequenzen

[Formulierung von LL-Grammatiken]{.thema}

{{% /challenges %}}
