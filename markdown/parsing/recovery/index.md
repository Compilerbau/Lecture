---
type: lecture-cg
title: "Error-Recovery"
author: "Carsten Gips (FH Bielefeld)"
weight: 8
readings:
  - key: "Parr2010"
    comment: "Kapitel 2 und 3"
  - key: "Parr2014"
  - key: "Levine2009"
    comment: "Kapitel 7 und 8"
assignments:
  - topic: sheet01
youtube:
  - id: TODO
fhmedia:
  - link: "TODO"
    name: "Direktlink FH-Medienportal: CB Error-Recovery"
---


## Fehler beim Parsen

![](images/bc_xml-parsing-error.png)

[Quelle: BC George, Vorlesung "Einführung in die Programmierung mit Skriptsprachen", [CC BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)]{.origin}

::: notes
*   Compiler ist ein schnelles Mittel zum Finden von (syntaktischen) Fehlern
*   Wichtige Eigenschaften:
    *   Reproduzierbare Ergebnisse
    *   Aussagekräftige Fehlermeldungen
    *   Nach Erkennen eines Fehlers: (vorläufige) Korrektur und Parsen des restlichen Codes
        => weitere Fehler anzeigen.
        Problem: Bis wohin "gobbeln", d.h. was als Synchronisationspunkt nehmen? Semikolon?
    *   Syntaktisch fehlerhafte Programme dürfen nicht in die Zielsprache übersetzt werden!
:::


## Typische Fehler beim Parsing

```yacc
grammar VarDef;

alt   : stmt | stmt2 ;
stmt  : 'int' ID ';' ;
stmt2 : 'int' ID '=' ID ';'  ;
```

[ANTLR4: VarDef.g4, Beispiele: VarDef.txt]{.bsp}


::::::::: notes
*Anmerkung*: Die nachfolgenden Fehler werden am Beispiel der Grammatik
`[VarDef.g4](src/VarDef.g4)`{=markdown} und ANTLR4 demonstriert.

### Lexikalische Fehler

Eingabe: `int x1;`

Fehlermeldung: `token recognition error at: '1'` (Startregel `stmt`)

Die ist ein Fehler aus dem Lexer, wenn beim Erkennen eines Tokens ein komplett
unbekanntes Zeichen auftritt.

### Ein extra Token

Eingabe: `int x y;`

Fehlermeldung: `extraneous input 'y' expecting ';'` (Startregel `stmt`)

Wenn nur ein Token zu viel ist, dann kann der von ANTLR generierte Parser eine
passende Fehlermeldung ausgeben.

### Mehrere extra Token

Eingabe: `int x y z;`

Fehlermeldung: `mismatched input 'y' expecting ';'` (Startregel `stmt`)

Wenn dagegen mehr als ein Token zu viel ist, dann gibt der von ANTLR generierte
Parser eine generische Fehlermeldung aus.

### Fehlendes Token

Eingabe: `int ;`

Fehlermeldung: `missing ID at ';'` (Startregel `stmt`)

Ein anderer typischer Fehler sind fehlende Token, die kann der Parser analog zu
überzähligen Token erkennen und ausgeben.

### Fehlendes Token am Entscheidungspunkt

Eingabe: `int ;`

Fehlermeldung: `no viable alternative at input 'int;'` (Startregel `alt`)

Hier fehlt ein Token, aber an einer Stelle, wo sich der Parser zwischen zwei
Alternativen (Sub-Regeln) entscheiden muss.
:::::::::


## Überblick Recovery bei Parser-Fehlern

::: center
![](images/recovery.png){width="80%"}
:::

::: notes
*   Fehler im Lexer (hier nicht weiter betrachtet):
    *   Aktuelles Zeichen passt zu keinem Token: Entfernen oder Hinzufügen
        von Zeichen (plus Rückmeldung an den Parser)
    *   Spezielle Token, die typische fehlerhafte Zeichenketten als Token
        erkennen (mit Weiterverarbeitung im Parser)

*   Fehler im Parser:
    *   Token passt nicht: Token entfernen oder ein Dummy-Token erzeugen
    *   Panic-Mode: Entferne Token bis zu einem Synchronisationspunkt.
        Problem: Dabei nicht zu weit zu springen!
    *   Spezielle Fehlerproduktionen: Spezielle Regeln in der Grammatik,
        die typische Fehler matchen.

Anmerkung LR-Parser: Ein Syntaxfehler wird entdeckt, wenn die Action-Tabelle
für Top-of-Stack und akt. Token leer ist => Stack und/oder Token modifizieren,
aber deutlich schwieriger als bei LL ...
:::


## Skizze: Generierte Parser-Regeln (ANTLR)

```yacc
stmt  : 'int' ID ';' ;
```

\bigskip

```python
def stmt():
    try: match("int"); match(ID); match(";")
    catch (RecognitionException re):
        _errHandler.reportError(self)               # let's report it
        _errHandler.recover(self)                   # Panic-Mode

def match(ttype):
    token = getCurrentToken()
    if token.type() == ttype: consume()
    else: token = _errHandler.recoverInline(this)   # Inline-Mode
    return token
}
```

::: notes
Der im Parser registrierte ErrorHandler erzeugt in der Methode
`reportError()` eine geeignete Meldung und gibt sie an den Parser
über dessen Methode `notifyErrorListeners()` weiter.

Die eigentliche Fehlerbehandlung findet in der Methode `recover()`
bzw. `recoverInline()` des ErrorHandlers statt.
:::


## Inline-Recovery bei Token-Mismatch (Skizze)

```python
def recoverInline(parser):
    # SINGLE TOKEN DELETION
    if singleTokenDeletion(parser):
        return getMatchedSymbol(parser)

    # SINGLE TOKEN INSERTION
    if singleTokenInsertion(parser):
        return getMissingSymbol(parser)

    # that didn't work, throw a new exception
    throw new InputMismatchException(parser)
}
```

::: notes
Die Klasse `InputMismatchException` drückt aus, dass das aktuelle Token nicht
zur Erwartung des Parsers passt. Deshalb wird diese Exception am Ende von
`recoverInline()` geworfen. Die Klasse `RecognitionException`, die in den
Parserregeln wie `stmt` gefangen wird, ist die gemeinsame Oberklasse aller
Parser-Exceptions.


Liste der wichtigsten Exceptions (nach
[github.com/antlr/antlr4/blob/master/doc/parser-rules.md](https://github.com/antlr/antlr4/blob/master/doc/parser-rules.md)):

| Exception                   | Beschreibung                                                                           |
|:----------------------------|:---------------------------------------------------------------------------------------|
| `RecognitionException`      | Basisklasse für alle Parser-Exceptions                                                 |
| `NoViableAltException`      | Parser konnte sich nicht für (mind.) einen Pfad entscheiden angesichts des Tokenstroms |
| `LexerNoViableAltException` | Lexer-Pendant zu `NoViableAltException`                                                |
| `InputMismatchException`    | Das aktuelle Token ist nicht das, was der Parser erwartet                              |

:::


## Panic Mode: Sync-and-Return (Skizze)

```python
def rule():
    try: ... rule-body ...
    catch (RecognitionException re):
        _errHandler.reportError(self)       # let's report it
        _errHandler.recover(self)           # Panic-Mode
}
```

\bigskip

=> Entferne solange Token, bis aktuelles Token im "*Resynchronization Set*"


## ANTLR: Einsatz des "*Resynchronization Set*"

::: note
*   **Following Set**: Menge der Token, die direkt auf eine Regel-Referenz folgen,
    ohne dass die aktuelle Regel/Alternative verlassen wird
*   **Resynchronization Set**: Vereinigung der *Following Sets* für alle Regeln im
    aktuellen Aufruf-Stack

[Quelle: nach [@Parr2014, S. 161 ff.]]{.origin}
:::

\bigskip

```yacc
stmt : 'if' expr ':' stmt           // Following Set für "expr": {':'}
     | 'while' '(' expr ')' stmt ;  // Following Set für "expr": {')'}
expr : term '+' INT ;               // Following Set für "term": {'+'}
```

\bigskip

*   Eingabe: `if :`
*   Aufruf-Stack nach Bearbeitung von `if`: `[stmt, expr, term]`
*   **Resynchronization Set**: `{'+', ':'}`

[[Hinweis: *FOLLOW* $\ne$ *Following*]{.bsp}]{.slides}


::: notes
### Hinweis: *FOLLOW* $\ne$ *Following*

**FOLLOW** ist die Menge aller Token, die auf eine Regel folgen können

*   `FOLLOW(term) = {'+'}`
*   `FOLLOW(expr) = {':', ')'}`


**Following** ist dagegen **abhängig vom aktuellen Kontext**!

*   Stack: `[stmt, expr, term]` => *Resynchronization Set*: `{'+', ':'}`
:::


::: notes
### Beispiele Resynchronisation im Panic Mode (ANTLR)

**Hinweis**: Die Regel `term` ist in obigem Beispiel nicht weiter detailliert. Hier wird
angenommen, dass das aktuelle Token `':'` nicht passt.

*   Eingabe: `if :`
    *   In Regel `term`: Token `':'` passt nicht
        *   `consume()`, bis aktuelles Token in *Resynchronization Set*: `{'+', ':'}`
            (d.h. hier bleibt `':'` das aktuelle Token)
        *   Rückkehr zu Regel `expr`
    *   In Regel `expr`: Token `':'` passt jetzt
        *   Abschluss des Parsing (mit Fehlermeldung)

*   Eingabe: `if x + 42 ))):`
    *   In Regel `expr`: Token `')'` passt nicht
        *   `consume()`, bis aktuelles Token in *Resynchronization Set*: `{':'}`  (d.h.
            hier werden alle `')'` entfernt)
    *   In Regel `expr`: Token `':'` passt jetzt
        *   Abschluss des Parsing (mit Fehlermeldung)
:::


::::::::: notes
## ANTLR4: Ändern der Fehlerbehandlungs-Strategie

### Ändern der Fehlerbehandlungs-Strategie (global)

![](images/handler.png)

Sie überschreiben die Klasse `DefaultErrorStrategy` und müssen die oben gezeigten Methoden
`recover()` und `recoverInline()`aufrufen. Die eigene Fehlerbehandlung setzen Sie über die
Methode `setErrorHandler` des Parsers.

### Ändern der Fehlerbehandlungs-Strategie (lokal)

```yacc
r : ...
  ;
  catch[RecognitionException e] { throw e; }
```

Es lassen sich auch andere bzw. mehrere Exceptions fangen. Der `catch`-Block ersetzt den
Default-`catch`-Block der generierten Methode. Das bedeutet, dass sich der geänderte Modus
nur für die eine Regel auswirkt.

### Ändern der Fehler-Meldungen

::: center
![](images/listener.png)
:::

Für einen eigenen Listener leitet man sinnvollerweise von `BaseErrorListener` ab und
überschreibt die leere Implementierung von `syntaxError()`.

Damit die Fehlermeldungen nicht mehrfach ausgegeben werden, entfernt man zunächst alle
Listener und fügt dann den eigenen hinzu, bevor man den Parser startet.

### ANTLR4: Anmerkungen Fehlerbehandlung in Sub-Regeln

Bei Sub-Regeln (d.h. eine Regel enthält Alternativen) oder Schleifenkonstrukten
(d.h. eine Regel enthält `(...)*` oder `(...)+`) geht ANTLR4 etwas anders vor.

1.  Start einer Sub-Regel/Alternative: Versuch einer *single token deletion*

    ```python
    # am Anfang einer Alternative oder Schleife
    _errHandler.sync(self)
    ```

2.  Schleifenkonstrukte: `(...)*` oder `(...)+`

    Versuche, in der Schleife zu bleiben! Im Fehlerfall `consume()` bis

    *   Weitere Iteration der Schleife erkannt
    *   Token, welches der Schleife folgt, erkannt
    *   Token im *Resynchronization Set* des aktuellen Aufruf-Stacks

    Anmerkung: Im Prinzip entspricht dies dem *Panic Mode*, der Unterschied liegt
    darin, bis wohin der Parser nach der Recovery in einer Funktion/Methode (Regel)
    zurückspringt. D.h. wenn es verschiedene Möglichkeiten gibt, haben diese die
    obige Priorisierung.

3.  Fail-Save

    Um Endlos-Schleifen durch die Schritte (1) bzw. (2) zu vermeiden, löst der Parser
    beim zweiten Versuch, die selbe Parser-Stelle und Input-Position zu bearbeiten
    (also bei bereits aktivem Fehler), einen "*Fail-Safe*" aus. Der Parser konsumiert
    dann ein Token und fährt dann mit der Recovery fort.

Zu Details zur Fehlerbehandlung durch ANTLR vergleiche [@Parr2014, S. 170 ff.].
:::::::::


## Panic Mode in Bison (Error Recovery)

```yacc
line    : /* nothing */
        | line expr '\n'    { printf("%d\n", $2); }
        | error '\n'        { yyerror(); yyerrok; }
        ;
```

::: notes
Bison kennt ein spezielles Fehler-Token `error`. Dieses Token wird genutzt, um einen
Synchronisationspunkt in der Grammatik zu finden, von dem aus man *höchstwahrscheinlich*
weiter parsen kann.

Der Parser wird mit diesen Produktionen generiert wie mit normalen Token auch. Im
Fehlerfall werden so lange Symbole vom Stack entfernt, bis eine Regel der Form
$A \to \operatorname{error} \alpha$ anwendbar ist. Dann wird das Token `error` auf den
Stack geschoben und so lange Eingabe-Token gelesen und verworfen, bis eines gefunden
wird, welches auf das `error`-Token folgen kann. Dies nennt Bison "Resynchronisation".
Anschließend wird im Recovery-Modus normal fortgefahren, bis drei weitere Token auf den
Stack geschoben wurden und damit der Recovery-Modus verlassen wird. Falls bereits vorher
weitere Fehler auftreten, werden diese nicht separat gemeldet.

Im obigen Beispiel ist die Regel `line : error '\n'` enthalten. Im Fehlerfall werden die
Symbole vom Stack entfernt, bis ein Zustand erreicht ist, der eine Shift-Aktion auf das
Token `error` hat. Das Error-Token wird auf den Stack geschoben und alle Eingabetoken bis
zum nächsten `'\n'` gelesen und entfernt. Mit dem Erreichen des Umbruchs wird die zugeordnete
Aktion ausgeführt. Diese gibt den Fehler auf der Konsole aus und führt mit dem Makro `yyerrok`
einen Reset des Parsers aus (d.h. er verlässt den Recovery-Modus **vor** dem Shiften von drei
Token). Anschließend ist der Bison-Parser wieder im normalen Modus. Die fehlerhaften Symbole/Token
wurden aus dem Eingabestrom entfernt.


Die "schwarze Kunst" ist, die Error-Token an geeigneten Stellen unterzubringen, d.h.
vorherzusehen, wo der Parser am sinnvollsten wieder aufsetzen kann. Häufig sind dies
beispielsweise das ein Statement beendende Semikolon oder die einen Block beendende
schließende geschweifte Klammer. Beispielsweise könnte man für die Sprache C bei der
Definition von Statements mehrere Synchronisationspunkte einbauen:
:::

\pause
\bigskip
\bigskip

```yacc
stmt : ...
     | RETURN expr ';'
     | '{' stmt_list '}'
     | error ';'  /* Synchronisation für RETURN */
     | error '}'  /* Synchronisation nach Block */
     ;
```

[Quelle: [@Levine2009, S.207]]{.origin}

**TODO Grammatik**

::: notes
Wenn Bison im Recovery-Modus ist, werden Symbole und ihre Werte vom Stack entfernt.
Falls diese Werte (vgl. `%union`) Pointer mit dynamisch alloziertem Speicher sind,
muss Bison diesen Speicher freigeben. Dazu kann man sich über die Direktive
`%destructor { code } symbols`  oder `%destructor { code } <types>`
Code definieren, der dann für die jeweiligen Symbole oder Typen ausgeführt wird.
Die Typangabe `<*>` dient dabei als Catch-All für Symbole, für die ein Typ definiert
wurde, aber kein Destruktor.

Beispiel:
```yacc
%destructor { printf("freeing %s at %d\n", $$, @$.first_line); free($$); } <strval>
```
:::


## Fehlerproduktionen

::: notes
### ANTLR4
:::

```yacc
funcall
  : ID '(' expr ')'
  | ID '(' expr ')' ')' {notifyErrorListeners("Too many ')'");}
  | ID '(' expr         {notifyErrorListeners("Missing ')'");}
  ;
```

[Quelle: nach [@Parr2014, S. 172]]{.origin}

**TODO Grammatik**

::: notes
<!-- XXX Konsole statt IDEA, weil zusätzliche Fehlermeldungen nur in Konsole auftauchen ... -->
[Konsole: Call.g4]{.bsp}

Häufig vorkommende Fehler kann man bereits in der Grammatik berücksichtigen.
Dadurch kommt es nicht zu einem Parser-Error mit Recovery-Mechanismus, sondern
der Fehler wird über eine entsprechende Alternative in der Grammatik korrigiert.
Es bietet sich an, in diesem Fall eine entsprechende Ausgabe zu tätigen. Dies
wird in der obigen Grammatik über eingebettete Aktionen erledigt.

Der aus der Grammatik generierte Parser leitet von der Basisklasse `Parser`
ab. Dort wird eine Methode `notifyErrorListeners()` implementiert, die man
mit Hilfe von in die Grammatik eingebetteten Aktionen aufrufen kann (Vorgriff
auf ["Interpreter: Attribute+Aktionen"](cb_interpreter2.html)). Letztlich steht
im generierten Parser in der generierten Methode `funcall()` an der passenden
Stelle ein Aufruf `this.notifyErrorListeners("Too many parentheses");` ...
:::

\bigskip
\bigskip


:::notes
### Flex und Bison

Erkennung von Strings (Flex):
:::

```
\"[^\"\n]+\"    { yylval.string = yytext; return STRING; }
\"[^\"\n]+$     { warning("unterminated string");
                  yylval.string = yytext; return STRING; }
```

[Quelle: [@Levine2009, S. 198]]{.origin}

**TODO Grammatik**

::: notes
Erkennen von IDs:

```
id : NAME   { $$ = $1; }
   | STRING { yyerror("id %s cannot be a string", $1);
              $$ = $1; }
   ;
%%
void yyerror(char *s, ...) {
    va_list ap; va_start(ap, s);
    fprintf(stderr, "%d: error: ", yylineno);
    vfprintf(stderr, s, ap); fprintf(stderr, "\n");
}
```

**TODO Code**

[Quelle: [@Levine2009, S. 198]]{.origin}

Analog zu ANTLR4 ist es auch in Flex/Bison üblich, für typische Szenarien
"nicht ganz korrekte" Eingaben zu akzeptieren. Dazu definiert man zusätzliche
Lexer- oder Parser-Regeln, die diese Eingaben als das, was gemeint war akzeptieren
und eine zusätzliche Warnung ausgeben.

Dabei definiert man sich typischerweise die Funktion `yyerror()`. Über
`yytext` hat man Zugriff auf den Eingabetext des aktuellen Tokens, und
mit `yylineno` hat man Zugriff auf die aktuelle Eingabezeile (`yylineno`
wird automatisch bei jedem `\n` inkrementiert). Wenn man weitere Informationen
benötigt, muss man mit dem Bison-Feature "Locations" arbeiten. Dies ist ein
spezieller Datentyp `YYLTYPE`.
:::




::: notes
## Anmerkung: Nicht eindeutige Grammatiken

```yacc
grammar Ambig;

stat: expr ';'        // expression statement
    | ID '(' ')' ';'  // function call statement
    ;

expr: ID '(' ')'
    | INT
    ;
```

[Quelle: [@Parr2014, S. 159]]{.origin}

**TODO Grammatik**


=> Eingabe: `f()`

[Konsole: Ambig.g4 ohne/mit "-diagnostics"]{.bsp}


### ANTLR4

Nicht eindeutige Grammatiken führen **nicht** zu einer Fehlermeldung, da nicht
der Nutzer mit seiner Eingabe Schuld ist, sondern das Problem in der Grammatik
selbst steckt.

Während des Debuggings von Grammatiken lohnt es sich aber, diese Warnungen
zu aktivieren. Dies kann entweder mit der Option "`-diagnostics`" beim Aufruf
des `grun`-Tools geschehen oder über das Setzen des `DiagnosticErrorListener`
aus der ANTLR4-Runtime als ErrorListener.


### Bison

Bison meldet nicht eindeutige Grammatiken beim Erzeugen des Parsers
(vgl. Shift/Reduce- und Reduce/Reduce-Konflikte) und entscheidet sich
jeweils für eine Operation (wobei Shift bevorzugt wird). Dies kann
man im über die Option `-v` erzeugten `<name>.output`-File überprüfen.
:::


## Wrap-Up

*   Fehler bei `match()`: *single token deletion* oder *single token insertion*

*   Panic Mode: *sync-and-return* bis Token in *Resynchronization Set* (ANTLR4)
    oder `error`-Token shiftbar (Bison)
    *   ANTLR4: Sonderbehandlung bei Start von Sub-Regeln und in Schleifen
    *   ANTLR4: Fail-Save zur Vermeidung von Endlosschleifen

*   Fehler-Alternativen in Grammatik einbauen






<!-- DO NOT REMOVE - THIS IS A LAST SLIDE TO INDICATE THE LICENSE AND POSSIBLE EXCEPTIONS (IMAGES, ...). -->
::: slides
## LICENSE
![](https://licensebuttons.net/l/by-sa/4.0/88x31.png)

Unless otherwise noted, this work is licensed under CC BY-SA 4.0.
:::
