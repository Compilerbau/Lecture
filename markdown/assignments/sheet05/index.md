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

*   Was müssten Sie ändern, wenn Attribute bereits in der Klassendefinition angelegt werden sollen
    (wie etwa in Java oder C++ üblich)?
*   Erweitern Sie das vorgestellte Interpreterkonzept um Vererbung inkl. Polymorphie.
    Wie würde sich statische und dynamische Polymorphie auf den Interpreter und
    dessen Strukturen und Arbeitsweise auswirken?


{{% /challenges %}}
>>>>>>> challenges
