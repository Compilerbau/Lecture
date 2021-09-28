---
type: assignment
title: "Blatt 02: Semantische Analyse"
author: "BC George, Carsten Gips (FH Bielefeld)"
hidden: true
weight: 2
---


## A2.1: Erweiterung der Grammatik: Funktionen

Erweitern Sie die Grammatik von Blatt 01 um die Möglichkeit, Funktionen definieren und aufrufen zu
können. Funktionen sollen (wie in C) nicht innerhalb von Funktionen definiert werden können.
Globale Variablen sollen ebenfalls zwischen den Funktionsdefinitionen/-aufrufen definiert werden
können.

Hinweis: Sogenannte Vorwärtsdeklarationen sind nicht nötig.


## A2.2: Grammatik und Scanner-/Parser-Generatoren

Erzeugen Sie mithilfe der Grammatik und ANTLR einen Scanner und Parser, den Sie für die folgenden
Aufgaben nutzen.


<<<<<<< HEAD
## A2.3: Symboltabellen

Fügen Sie Symboltabellen in Ihren Compiler ein.

Ergänzen Sie Ihre Ausgabefunktion, so dass auch die Symboltabelle als DOT-Code formatiert ausgegeben wird.
Nutzen Sie diese Ausgabe auch zum Debuggen und zum Erklären Ihres Codes.


## A2.4: Type Checking

Attributieren Sie Ihren AST und führen Sie mit Hilfe der Attribute und der Symboltabellen ein *Type Checking*
durch.

Bestimmen Sie zunächst, wo ein Type Checking möglich und/oder nötig ist. Welche Attribute benötigen Sie,
wo können Sie Ableitungsregeln geeignet einsetzen/anpassen?
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
>>>>>>> move challenges
