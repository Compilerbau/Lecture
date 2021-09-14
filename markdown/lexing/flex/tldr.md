---
title: "TL;DR"
disableToc: true
hidden: true
---

![Compiler-Pipeline](images/architektur_cb_lexer)\

Flex ("*Fast Lexical Analyzer Generator*") ist ein Lexer-Generator für C/C++.

Die Konfigurationsdatei für Flex enthält drei Abschnitte: Definition von Namen sowie Code, der
im generierten C-Code am Anfang eingebettet wird (typischerweise `#include`), dann die Regeln
für den Lexer (reguläre Ausdrücke plus C-Code, der beim Matchen aufgerufen wird), und schließlich
C-Code, der beispielsweise die `main()`-Funktion enthält.

Bei einem Match gewinnt der längste Match. Wenn es mehrere gleich längste Matches gibt, gewinnt
die zuerst definierte Regel.

Die Funktion zum Aufrufen des Lexers heisst `yylex()`. In der vordefinierten (externen) Variable
`yytext` kann der gematchte Text abgerufen werden. `yytokentype` ist ein `enum`, welche die Token
definiert (üblicherweise von Bison generiert) und in `yylval` kann das Lexem (Wert eines Tokens)
gespeichert werden.

Regeln können mit einem Zustand annotiert werden und gelten dann nur in diesem Zustand. Auf diesem
Weg kann man Inselgrammatiken abbilden.