---
type: assignment
title: "Blatt 05: Interpreter"
author: "BC George, Carsten Gips (FH Bielefeld)"
hidden: true
weight: 5
---


## A5.1: Interpreter

Bauen Sie einen Tree-Walking-Interpreter in Ihr Projekt ein:

*   Lesen Sie zunächst den zu interpretierenden Small-C-Code aus einer Datei ein.
*   Realisieren Sie die Funktionen `readint` und `writeint` als *native* Funktionen im Interpreter.


## A5.2: Interaktivität

Erweitern Sie Ihren Interpreter um Interaktivität:

*   Der Interpreter soll einen Prompt in der Konsole anbieten
*   Der Interpreter soll Code zeilenweise von der Standard-Eingabe lesen und verarbeiten
*   Zur Eingabe mehrzeiliger Konstrukte sehen Sie entweder das Einlesen von
    Codeblöcken aus Dateien vor oder implementieren Sie entsprechend eine "logische
    Einrückung" für den Prompt als visuelles Feedback für den User

<<<<<<< HEAD
Sie können sich hier am [Interpreter für Lox](https://craftinginterpreters.com/a-tree-walk-interpreter.html) orientieren.
=======
{{% challenges %}}
## Interpreter

*   Worin unterscheiden sich interaktive und nicht-interaktive Interpreter?
*   Wie arbeitet ein Interpreter?
*   Wozu benötigt man die Environments (bzw. bei @Parr2010 die Memory-Space-Strukturen und einen Stack)?
*   Erweitern Sie das vorgestellte Interpreterkonzept um interaktive Verarbeitung.
*   Im vorgestellten Konzept wird bei der Auswertung von Symbolen für ein Symbol u.U. immer wieder
    die Environment-Hierarchie durchsucht. Verbessern Sie das, indem Sie nach der semantischen
    Analyse und vor der eigentlichen Interpretation einen weiteren Resolve-Schritt einbauen. Bei
    der Interpretation soll dadurch gezielt auf den richtigen Kontext zugegriffen werden können.
*   Die vorgestellten Strukturen für die Kontexte ("Environment") ähneln stark den bereits
    vorgestellten Symboltabellen. Wie könnte man diese Strukturen "unifizieren", damit man die im
    Frontend erstellten Symboltabellen direkt im Interpreter als "Environment" weiter verwenden
    kann?


{{% /challenges %}}
>>>>>>> challenges
