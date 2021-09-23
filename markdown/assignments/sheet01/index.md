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

<<<<<<< HEAD

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
=======
{{% challenges %}}
## $\operatorname{First}_2$ - und $\operatorname{Follow}_2$
Berechnen Sie die $\operatorname{First}_2$ - Mengen von allen rechten Seiten der Produktionen und die $\operatorname{Follow}_2$ - Mengen von allen Nichtterminalen der folgenden Grammatik:

$S\ \ \rightarrow \ \boldsymbol{if}\ \ E \ \ S\  \ \boldsymbol{else}\  \ S\ \ \ \vert \ \ \boldsymbol{\{} \ \ S \ \ R \ \ \vert \ \ \boldsymbol{id\ =} \ \ E$\phantom{m}

$R\ \ \rightarrow\ \ \boldsymbol{\}}\ \ \mid\ \ \boldsymbol{;}\ \ S\ \ R$

$E\ \ \rightarrow\ \ \boldsymbol{num}\ \ \mid\ \ \boldsymbol{id}$

[Berechnen von First- und Follow-Mengen]{.thema}

## $\operatorname{LL(1)}$-Sprachen
Ist die Sprache $L = {a^mcb^{m}}$ LL(1)?
Beweisen Sie Ihre Behauptung.

[Anwenden der Definitionen von First- und Follow-Mengen]{.thema}

## Reduzierte kontextfreie Grammatiken
Entwickeln Sie einen Algorithmus zur Reduzierung einer kontextfreien Grammatik.

[Verständnis reduzierter CFG]{.thema}

## $LL(1)$ - Grammatiken
Formen Sie die folgende Grammatik in eine äquivalente LL(1)-Grammatik um:

$S\ \rightarrow\ A\ T \mid T$

$A\ \rightarrow \ S\ \ \boldsymbol{+}\ \ \mid \ \boldsymbol{a}\ \boldsymbol{+}$

$B\ \rightarrow\ \boldsymbol{a}\ B\ \mid\ T$

$T\ \rightarrow\ T \ \ \boldsymbol{*}\ \ F\ \mid\ F$

$F\ \rightarrow\ L\ E\ \boldsymbol{)}\ \mid \ \boldsymbol{a}\ \mid\ L\ \boldsymbol{a}\ \boldsymbol{)}$

$L\ \rightarrow\ \boldsymbol{(}$

[Äquivalenzumformungen von Grammatiken]{.thema}

## Top-Down-Parsing
Erzeugen Sie die Parsertabelle für die Grammatik aus Aufgabe 4 und parsen Sie (auf dem Papier) das Wort $a + a + a$ mit einem PDA.

[Anwendung von Top-Down-Parsing-Verfahren]{.thema}

## Nicht-$LL(1)$ - Grammatiken
Entwickeln Sie eine Grammatik, die nicht $LL(1)$ ist. Ist sie $LL(k)$ für ein $k > 1$?

[Verständnis der $LL(k)$-Eigenschaft von Grammatiken]{.thema}
{{% /challenges %}}
>>>>>>> challenges
