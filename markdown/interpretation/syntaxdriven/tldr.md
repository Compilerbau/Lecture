---
title: "TL;DR"
disableToc: true
hidden: true
---

Zur Einordnung noch einmal die bisher betrachteten Phasen und die jeweiligen Ergebnisse:

![Compiler-Pipeline](images/architektur_cb)\

|      | Phase                          | Ergebnis                                                     |
| :--- | :----------------------------- | :----------------------------------------------------------- |
| 0    | Lexer/Parser                   | AST                                                          |
| 1    | Semantische Analyse, Def-Phase | Symboltabelle (Definitionen), Verknüpfung Scopes mit AST-Knoten |
| 2    | Semantische Analyse, Ref-Phase | Prüfung auf nicht definierte Referenzen                      |
| 3    | Interpreter                    | Abarbeitung, Nutzung von AST und Symboltabelle               |

Das Erzeugen der Symboltabelle wird häufig in zwei Phasen aufgeteilt: Zunächst
werden die Definitionen abgearbeitet und in der zweiten Phase wird noch einmal
über den AST iteriert und die Referenzen werden geprüft. Dies hat den Vorteil,
dass man mit Vorwärtsreferenzen arbeiten kann ...

Für die semantische Analyse kann man gut mit Listenern arbeiten, für den Interpreter
werden oft Visitors eingesetzt.

Die einfachste Form von Interpretern sind die "syntaxgesteuerten Interpreter". Durch
den Einsatz von attributierten Grammatiken und eingebetteten Aktionen kann in einfachen
Fällen der Programmcode bereits beim Parsen interpretiert werden, d.h. nach dem Parsen
steht das Ergebnis fest.

Normalerweise traversiert man in Interpretern aber den AST, etwa mit dem Listener-
oder Visitor-Pattern. Die in dieser Sitzung gezeigten einfachen Beispiele der
syntaxgesteuerten Interpreter werden erweitert auf die jeweilige Traversierung mit
dem Listener- bzw. Visitor-Pattern. Für nicht so einfache Fälle braucht man aber
zusätzlich noch Speicherstrukturen, die wir in ["AST-Traversierung I"](cb_interpreter2.html)
und ["AST-Traversierung II"](cb_interpreter3.html) betrachten.