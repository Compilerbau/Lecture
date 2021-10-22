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

[Quelle: BC George, Vorlesung "Einführung in die Programmierung mit Skriptsprachen"]{.origin}

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
grammar SimpleClassDef;

prog     :  classDef+ ;
classDef :  'class' ID '{' member+ '}' ;
member   :  'int' ID ';' | 'int' ID '=' INT ';' ;

ID  :   [a-zA-Z]+ ;
INT :   [0-9]+ ;
WS  :   [ \t\r\n]+ -> skip ;
```

[ANTLR4: SimpleClassDef.g4, Beispiele: SimpleClassDef.txt]{.bsp}


::::::::: notes
*Anmerkung*: Die nachfolgenden Fehler werden am Beispiel der Grammatik
`SimpleClassDef.g4` und ANTLR4 demonstriert.


### Lexikalische Fehler

```
class X* { int x; }
```

Fehlermeldung: `token recognition error at: '*'`


### Ein extra Token {#std}

```
class Xa Xb { int x; }
```

Fehlermeldung: `extraneous input 'Xb' expecting '{'`


### Mehrere extra Token

```
class Xa Xb Xc { int x; }
```

Fehlermeldung: `mismatched input 'Xb' expecting '{'`


### Fehlendes Token {#sti}

```
class { int x; }
```

Fehlermeldung: `missing ID at '{'`


### Fehlendes Token am Entscheidungspunkt

```
class X { int ; }
```

Hier fehlt ein Token, aber an einer Stelle, wo sich der Parser zwischen zwei Sub-Regeln entscheiden muss.
Fehlermeldung: `no viable alternative at input 'int;'`
:::::::::


## Überblick Recovery bei Parser-Fehlern

::: center
![](images/recovery.png)
:::

::: notes
*   Fehler im Lexer (hier nicht weiter betrachtet):
    *   Aktuelles Zeichen passt zu keinem Token: Entfernen oder Hinzufügen von Zeichen (plus Rückmeldung an den Parser)
    *   Spezielle Token, die typische fehlerhafte Zeichenketten als Token erkennen (mit Weiterverarbeitung im Parser)

*   Fehler im Parser:
    *   Token passt nicht: Token entfernen oder ein Dummy-Token erzeugen
    *   Panic-Mode: Entferne Token bis zu einem Synchronisationspunkt. Problem: Dabei nicht zu weit zu springen!
    *   Spezielle Fehlerproduktionen: Spezielle Regeln in der Grammatik, die typische Fehler matchen.

Anmerkung LR-Parser: Ein Syntaxfehler wird entdeckt, wenn die Action-Tabelle für Top-of-Stack und akt. Token leer
ist => Stack und/oder Token modifizieren, aber deutlich schwieriger als bei LL ...
:::


## Detail: Generierte Parser-Regeln (LL)

```yacc
classDef :  'class' ID '{' member+ '}' ;
```

\bigskip

```java
public void classDef() {
    try {
        match("class"); match(ID); match("{");
        do { member(); } while (LA(1) == "int");
        match("}");
    } catch (RecognitionException re) {
        _errHandler.recover(this, re);               // Panic-Mode
    }
}
public Token match(int ttype) throws RecognitionException {
    Token t = getCurrentToken();
    if ( t.getType() == ttype ) { consume(); }
    else { t = _errHandler.recoverInline(this); }    // Inline-Mode
    return t;
}
```


## Recovery bei Token-Mismatch (LL)

```java
public Token recoverInline(Parser recognizer) {
    // SINGLE TOKEN DELETION
    Token matchedSymbol = singleTokenDeletion(recognizer);
    if ( matchedSymbol != null ) {
        recognizer.consume();
        return matchedSymbol;
    }

    // SINGLE TOKEN INSERTION
    if ( singleTokenInsertion(recognizer) ) {
        return getMissingSymbol(recognizer);
    }

    // even that didn't work; must throw the exception
    throw new InputMismatchException(recognizer);
}
```

::: notes
*Anmerkung*: Siehe Folie ["*ANTLR4: Ändern der Fehlerbehandlungs-Strategie*"](#antlr-change):
Die Klasse `InputMismatchException` drückt aus, dass das aktuelle Token nicht zur
Erwartung des Parsers passt. Deshalb wird diese Exception am Ende von `recoverInline`
geworfen. Die Klasse `RecognitionException`, die in den Parserregeln wie `classDef`
gefangen wird, ist die gemeinsame Oberklasse aller Parser-Exceptions.

*  Beispiel zu Token-Mismatch: Single Token Deletion (LL) siehe ["Ein extra Token"](#std)
*  Beispiel zu Token-Mismatch: Single Token Insertion (LL) siehe ["Fehlendes Token"](#sti)
:::


## Panic Mode: Sync-and-Return (LL)

```java
public void classDef() {
    try {
        ... rule-body ...
    } catch (RecognitionException re) {
        _errHandler.reportError(this, re);
        _errHandler.recover(this, re);
    }
}
```

\bigskip

=> Entferne solange Token, bis aktuelles Token im "*Resynchronization Set*"


## Definition *Resynchronization Set*

::: notes
*   **Following Set**: Menge der Token, die direkt auf eine Regel-Referenz folgen,
    ohne dass die aktuelle Regel/Alternative verlassen wird.
*   **Resynchronization Set**: Vereinigung der *Following Sets* für alle Regeln im
    aktuellen Aufruf-Stack.
:::

\bigskip

```yacc
group
    : '[' expr ']'      // Following Set für "expr": {']'}
    | '(' expr ')'      // Following Set für "expr": {')'}
    ;
expr: atom '^' INT ;    // Following Set für "atom": {'^'}
```

[Quelle: nach [@Parr2014, S. 162]]{.origin}

\bigskip

*   Eingabe: `[]`
*   Aufruf-Stack nach Bearbeitung von `[`: `[group, expr, atom]`
*   **Resynchronization Set**: `{'^', ']'}`

[[Hinweis: *FOLLOW* $\ne$ *Following*]{.bsp}]{.slides}


::: notes
## Hinweis: *FOLLOW* $\ne$ *Following*

**FOLLOW** ist die Menge aller Token, die auf eine Regel folgen können

*   `FOLLOW(atom) = {'^'}`
*   `FOLLOW(expr) = {']', ')'}`

\bigskip

**Following** ist dagegen **abhängig vom aktuellen Kontext**!

*   Stack: `[group, expr, atom]` => *Resynchronization Set*: `{'^', ']'}`
:::


::: notes
## Beispiele Resynchronisation im Panic Mode (LL)

**Hinweis**: Die Regel `atom` ist in obigem Beispiel nicht weiter detailliert. Sie könnte
beispielsweise alternativ einen Namen (`ID`) oder einen Integer (`INT`) verlangen.

*   Eingabe: `[]`
    *   In Regel `atom`: Token `']'` passt nicht
        *   `consume()`, bis aktuelles Token in *Resynchronization Set*: `{'^', ']'}`
            (d.h. hier bleibt `']'` das aktuelle Token)
        *   Rückkehr zu Regel `expr`
    *   In Regel `expr`: Token `']'` passt nicht
        *   `consume()`, bis aktuelles Token in *Resynchronization Set*: `{']'}`  (d.h.
            hier bleibt `']'` das aktuelle Token)
        *   Rückkehr zu Regel `group`
    *   Abschluss des Parsing (mit Fehlermeldung)

\smallskip

*   Eingabe: `[42 ^ )))]`
    *   In Regel `expr`: Token `')'` passt nicht
        *   `consume()`, bis aktuelles Token in *Resynchronization Set*: `{']'}`  (d.h.
            hier werden alle `')'` entfernt)
        *   Rückkehr zu Regel `group`
    *   Abschluss des Parsing (mit Fehlermeldung)

[ANTLR4: Following.g4]{.bsp}
:::


::: notes
## ANTLR4: Details zu Fehlerbehandlung in Sub-Regeln

Bei Sub-Regeln (d.h. eine Regel enthält Alternativen) oder Schleifenkonstrukten
(d.h. eine Regel enthält `(...)*` oder `(...)+`) geht ANTLR etwas anders vor.

```yacc
classDef :  'class' ID '{' member+ '}' ;
member   :  'int' ID ';' | 'int' ID '=' INT ';' ;
```

1.  Start einer Sub-Regel/Alternative: Versuch einer *single token deletion*

    ```java
    // am Anfang einer Alternative oder Schleife
    _errHandler.sync(this);
    ```

\smallskip

2.  Schleifenkonstrukte: `(...)*` oder `(...)+`

    Versuche, in der Schleife zu bleiben! Im Fehlerfall `consume()` bis

    *   Weitere Iteration der Schleife
    *   Token, welches der Schleife folgt
    *   *Resynchronization Set* des aktuellen Aufruf-Stacks

    Anmerkung: Im Prinzip entspricht dies dem *Panic Mode*, der Unterschied liegt darin, bis
    wohin der Parser nach der Recovery in einer Funktion/Methode (Regel) zurückspringt. D.h.
    wenn es verschiedene Möglichkeiten gibt, haben diese die obige Priorisierung.

[IDEA: SimpleClassDef.g4, Beispiele: Subrule.txt]{.bsp}


### *Single Token Deletion* am Start einer Alternative

```java
class X {{ int x; }
class Y { int y; }
```

=> `line 1:9 extraneous input '{' expecting 'int'`

Die zweite `}` nach `X` wird am Start von `member` durch die extra *single token deletion* entfernt.


### *consume()* bis weitere Schleifeniteration

```java
class X {
    int x;
    y;;;
    int z;
}
class Y { int y; }
```

=> `line 3:4 extraneous input 'y' expecting {'}', 'int'}`

Die Token resultierend aus `y;;;` werden schrittweise entfernt, bis das aktuelle Token wieder
einen Schleifenanfang anzeigt.


### *consume()* bis Token, welches der Schleife folgt

```java
class X {
    int x;
    y;;;
}
class Y { int y; }
```

=> `line 3:4 extraneous input 'y' expecting {'}', 'int'}`

Die Token resultierend aus `y;;;` werden schrittweise entfernt, bis das aktuelle Token dem
auf die Schleife folgenden Token entspricht.


### *consume()* bis Resynchronization Set des aktuellen Aufruf-Stacks

```java
class X {
    int x;
    ;
class Y { int y; }
```

=> `line 3:4 extraneous input ';' expecting {'}', 'int'}`

Das Token `;` wird entfernt. Da der Aufrufstack an der Stelle `[prog, classDef, member]`
ist, ist das *Resynchronization Set* entsprechend `['int', '}', 'class']`. Dadurch wird
verhindert, dass der Recovery-Prozess noch `class Y { int y;` entfernt und die erste
Klassendefinition mit der letzten `}` abschließt. Die auf die kaputte Klassendefinition
für `X` folgende Definition für `Y` wird also korrekt erkannt.
:::


::: notes
### Error Recovery Fail-Save (LL)

```java
class X {
    int int x;
}
```

[IDEA: SimpleClassDef.g4]{.bsp}


Der Parser geht nach dem Verarbeiten von `{` in die Schleife `member+`, d.h. in die Regel
`member`. Da dort beide Alternativen mit `int` beginnen, wird keine *single token deletion*
ausgeführt. Das zweite `int` ergibt aber einen Syntaxfehler, auf den der Parser mit einem
*sync-and-return* reagiert. Dummerweise führt das in eine Endlosschleife:

*   Der Aufrufstack ist `[prog, classDef, member]`
*   Das *Resynchronization Set* ist entsprechend ['int', '}', 'class']
*   Der Parser kehrt aus `member` ohne weiteres `consume()` zur `member+`-Schleife zurück, da
    das aktuelle Token `int` im *Resynchronization Set* enthalten ist.
*   Die `member+`-Schleife versucht gemäß der Sonderregeln für Sub-Regeln, in der Schleife zu
    bleiben: Aufruf von `member` mit dem selben Problem ...


Ausweg: Der Parser löst beim zweiten Versuch, die `member`-Regel mit `int int x;` zu bearbeiten,
einen *Fail-Safe* aus, weil er an der selben Parser-Stelle und Input-Position angekommen ist
(bei bereits aktivem Fehler). Der *Fail-Safe* konsumiert ein Token und fährt mit der Recovery
fort. Da das nächste Token wieder ein `int` ist, wird dieses wieder nicht entfernt, sondern nach
`member+` zurückgekehrt und von dort wieder nach `member` gesprungen. Diesmal passt es dann ...

Das sieht man schön im generierten Parse-Tree:

![](images/fail-save.png)
:::


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
### ANTLR
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
## ANTLR4: Ändern der Fehler-Meldungen

::: center
![](images/listener.png){width="80%"}
:::


Für einen eigenen Listener leitet man sinnvollerweise von `BaseErrorListener` ab und überschreibt
die leere Implementierung von `syntaxError()`.

Im obigen Beispiel wird der aktuelle Aufrufstack ausgegeben, gefolgt von der Zeilennummer und Position
und Original-Fehlermeldung. Zum Unterstreichen gibt man die fehlerhafte Zeile des Inputs erneut aus, und
auf der nächsten Zeile Leerzeichen, bis man unter der fehlerhaften Stelle (`offendingToken`) ist, danach
dann `'^'` zum markieren ...

Damit die Fehlermeldungen nicht mehrfach ausgegeben werden, entfernt man zunächst alle Listener und fügt
dann den eigenen hinzu, bevor man den Parser startet.

[Konsole: SimpleListener.java]{.bsp}
:::


::: notes
## ANTLR4: Ändern der Fehlerbehandlungs-Strategie {#antlr-change}

```java
public void classDef() {
    try {
        ... rule-body ...
    } catch (RecognitionException re) {
        _errHandler.reportError(this, re);
        _errHandler.recover(this, re);
    }
}
```

```yacc
r : ...
  ;
  catch[RecognitionException e] { throw e; }
```


Anmerkung: Es lassen sich auch andere bzw. mehrere Exceptions fangen.
Der `catch`-Block ersetzt den Default-`catch`-Block der generierten
Methode. Das bedeutet, dass sich der geänderte Modus nur für die
eine Regel auswirkt.

Der im Parser registrierte ErrorHandler erzeugt in der Methode
`reportError()` eine geeignete Meldung und gibt sie an den Parser
über dessen Methode `notifyErrorListeners()` weiter.

Die eigentliche Fehlerbehandlung findet in der Methode `recover()`
des ErrorHandlers statt.


Liste der wichtigsten Exceptions (nach
[github.com/antlr/antlr4/blob/master/doc/parser-rules.md](https://github.com/antlr/antlr4/blob/master/doc/parser-rules.md)):

| Exception                   | Beschreibung                                                                           |
|:----------------------------|:---------------------------------------------------------------------------------------|
| `RecognitionException`      | Basisklasse für alle Parser-Exceptions                                                 |
| `NoViableAltException`      | Parser konnte sich nicht für (mind.) einen Pfad entscheiden angesichts des Tokenstroms |
| `LexerNoViableAltException` | Lexer-Pendant zu `NoViableAltException`                                                |
| `InputMismatchException`    | Das aktuelle Token ist nicht das, was der Parser erwartet                              |

:::


::: notes
## ANTLR4: Ändern der Fehlerbehandlungs-Strategie (global)

![](images/handler.png)
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
aus der ANTLR-Runtime als ErrorListener.


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
    *   ANTLR: Sonderbehandlung bei Start von Sub-Regeln und in Schleifen
    *   ANTLR4: Fail-Save zur Vermeidung von Endlosschleifen

*   Fehler-Alternativen in Grammatik einbauen






<!-- DO NOT REMOVE - THIS IS A LAST SLIDE TO INDICATE THE LICENSE AND POSSIBLE EXCEPTIONS (IMAGES, ...). -->
::: slides
## LICENSE
![](https://licensebuttons.net/l/by-sa/4.0/88x31.png)

Unless otherwise noted, this work is licensed under CC BY-SA 4.0.

### Exceptions
*   TODO (what, where, license)
:::
