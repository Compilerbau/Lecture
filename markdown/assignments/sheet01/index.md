---
type: assignment
title: "Blatt 01: Scanner und Parser"
author: "BC George, Carsten Gips (FH Bielefeld)"
points: 10
hidden: true
weight: 1
---


Schreiben Sie sukzessive über das Semester Ihren eigenen Compiler/Interpreter für ein C-Derivat ("*Small C*").

Machen Sie sich mit **Small C**  vertraut:
*   [http://marvin.cs.uidaho.edu/Teaching/CS445/c-Grammar.pdf](http://marvin.cs.uidaho.edu/Teaching/CS445/c-Grammar.pdf)
*   [https://medium.com/\@efutch/a-small-c-language-definition-for-teaching-compiler-design-b70198531a2f](https://medium.com/\@efutch/a-small-c-language-definition-for-teaching-compiler-design-b70198531a2f)
*   [https://www.lysator.liu.se/c/ANSI-C-grammar-y.html](https://www.lysator.liu.se/c/ANSI-C-grammar-y.html)


## A1.1: Grammatik (3P)

Modifizieren Sie die Grammatik für *Small C* folgendermaßen:

1.  Entfernen Sie folgende Elemente:
    *   die `for`-Schleife
    *   das `break`-Statement
    *   die Zufallszahlengenerierung mit `?n`
    *   die Zuweisungsoperatoren `+=`, `-=`, `*=` und `/=`
    *   die Auto-Inkrement-/-Dekrement-Operatoren `++` und `--`

<!--  -->

2.  Fügen Sie folgende Elemente hinzu:
    *   Klassen (OOP)
    *   einfache Vererbung
    *   einen impliziten Konstruktor, der (a) Integer-Variablen mit 0, (b) Boolesche Variablen mit `false`, (c) `char`-Variablen
        mit `'\0'` und (d) String-Variablen mit `"\0"` initialisiert. Sind Attribute selbst Klassen, muss deren Default-Konstruktor
        aufgerufen werden.
    *   die Funktion `printf`, die als Parameter einen String bekommt, `void` zurückgibt und den String in die Datei `output` schreibt
    *   die parameterlose Funktion `scanf`, die aus der Datei `input` ein Zeichen liest, es aus der Datei entfernt und als `char` zurückgibt.

<!--  -->

3.  Falls notwendig, bauen Sie die ursprüngliche Grammatik um.

<!--  -->

4.  Bestimmen Sie die terminalen Symbole Ihrer Grammatik und deren Aufbau.


## A1.2: Scanner (2P)

Entwickeln Sie einen Scanner (Lexer) für Ihren Compiler.


## Parser (3P)

Entwickeln Sie einen LL-Parser für Ihre Grammatik.

*   Modifizieren Sie dafür Ihre Grammatik, wenn nötig.
*   Legen Sie fest, wie Ihr AST strukturiert sein soll.
*   Bauen Sie einen AST auf.


## Visualisierung AST mit DOT (2P)

Visualisieren Sie Ihren AST mit DOT:

*    [https://de.wikipedia.org/wiki/DOT_(GraphViz)](https://de.wikipedia.org/wiki/DOT_(GraphViz))
*    [http://www.graphviz.org/doc/info/lang.html](http://www.graphviz.org/doc/info/lang.html)





{{% challenges %}}
## Bonus: Wuppie (2P)
Erstellen Sie ...
{{% /challenges %}}
