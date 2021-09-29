---
type: assignment
title: "Blatt 02: Semantische Analyse"
author: "BC George, Carsten Gips (FH Bielefeld)"
points: 10
hidden: true
weight: 2
---


## A2.1: Erweiterung der Grammatik: Funktionen (2P)

Erweitern Sie die Grammatik von Blatt 01 um die Möglichkeit, Funktionen definieren und aufrufen zu
können. Funktionen sollen (wie in C) nicht innerhalb von Funktionen definiert werden können.
Globale Variablen sollen ebenfalls zwischen den Funktionsdefinitionen/-aufrufen definiert werden
können.

Hinweis: Sogenannte Vorwärtsdeklarationen sind nicht nötig.


## A2.2: Grammatik und Scanner-/Parser-Generatoren (2P)

Erzeugen Sie mithilfe der Grammatik und ANTLR einen Scanner und Parser, den Sie für die folgenden
Aufgaben nutzen.


## A2.3: Symboltabellen (3P)

Fügen Sie Symboltabellen in Ihren Compiler ein.

Ergänzen Sie Ihre Ausgabefunktion, so dass auch die Symboltabelle als DOT-Code formatiert ausgegeben wird.
Nutzen Sie diese Ausgabe auch zum Debuggen und Erklären ihres Codes.


## A2.4: Type Checking (3P)

Attributieren Sie Ihren AST und führen Sie mit Hilfe der Attribute und der Symboltabellen ein *Type Checking*
durch.

Bestimmen Sie zunächst, wo ein Type Checking möglich und/oder nötig ist. Welche Attribute benötigen Sie,
wo können Sie Ableitungsregeln geeignet einsetzen/anpassen?






{{% challenges %}}
## Bonus: Wuppie (2P)
Erstellen Sie ...
{{% /challenges %}}
