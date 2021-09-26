---
type: assignment
title: "Blatt 03: Optimierung"
author: "BC George, Carsten Gips (FH Bielefeld)"
hidden: true
weight: 3
---


## A3.1: Konstruktion DAG

Konstruieren Sie aus Ihrem AST einen DAG. Ergänzen Sie Ihre Ausgabefunktion, so dass auch
der DAG als DOT-Code formatiert ausgegeben wird.

Hinweis: Geben Sie sowohl den ursprünglichen DAG als auch den DAG nach der Optimierung (siehe
nächste Aufgabe) aus.


## A3.2: Optimierung

<<<<<<< HEAD
Welche der in der Vorlesung besprochenen Optimierungen sind in der betrachteten Sprache sinnvoll?
Begründen Sie Ihre Analyse und führen Sie diese Optimierungen auf dem DAG durch.
=======


{{% challenges %}}
## Syntaxgerichtete Übersetzungsverfahren

*   Erstellen Sie ein SDT, das arithmetische Ausdrücke von der Infix- in die Präfix-Notation übersetzt.
*   Erstellen Sie ein SDT, das ganze Zahlen in römische Zahlen übersetzt und umgekehrt.
*   Betrachten Sie die Produktionsregel `a : b c d ;`. Jedes der Nicht-Terminalsymbole `a`, `b`, `c` und
    `d` hat zwei Attribute `s` und `i`, wobei `s` ein berechnetes und `i` ein geerbtes Attribut sei.
    Welche der folgenden semantischen Regeln sind mit einer S-attributierten SDD, mit einer L-attributierten
    SDD oder mit jeder beliebigen Auswertungsreihenfolge verträglich?
    1.  `a.s = b.i + c.s`
    2.  `a.s = b.i + c.s` und `d.i = a.i + b.s`
    3.  `a.s = b.s + d.s`
    4.  `a.s = d.i`, `b.i = a.s + c.s`, `c.i = b.s` und `d.i = b.i + c.i`
{{% /challenges %}}
>>>>>>> challenges
