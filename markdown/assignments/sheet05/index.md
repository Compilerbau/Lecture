---
title: "Blatt 05: Interpreter"
author: "Carsten Gips (FH Bielefeld)"
hidden: true
weight: 5
---

**TODO: Abnahmedatum eintragen**

## A6: Interpreter (Aufgaben zur Abnahme in KW??) {#a6}

*   Bauen Sie einen Tree-Walking-Interpreter in Ihr Projekt ein:
    *   Lesen Sie zunächst den zu interpretierenden Small-C-Code aus einer Datei ein.
    *   Realisieren Sie die Funktionen aus [A1](#a1) (`scanf`und `printf`) als *native* Funktionen im Interpreter.

\smallskip

*   Erweitern Sie Ihren Interpreter um Interaktivität:
    *   Der Interpreter soll einen Prompt in der Konsole anbieten
    *   Der Interpreter soll Code zeilenweise von der Standard-Eingabe lesen und verarbeiten
    *   Zur Eingabe mehrzeiliger Konstrukte sehen Sie entweder das Einlesen von
        Codeblöcken aus Dateien vor oder implementieren Sie entsprechend eine "logische
        Einrückung" für den Prompt als visuelles Feedback für den User


Sie können sich hier am [Interpreter für Lox](https://craftinginterpreters.com/a-tree-walk-interpreter.html) orientieren.



{{% challenges %}}
## Bonus: Wuppie (2P)
Erstellen Sie ...
{{% /challenges %}}