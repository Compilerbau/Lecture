---
type: lecture-cg
title: "LL-Parser selbst implementiert"
menuTitle: "LL-Parser implementieren"
author: "Carsten Gips (FH Bielefeld)"
weight: 2
readings:
  - key: "Parr2010"
    comment: "Kapitel"
  - key: "Parr2014"
  - key: "Mogensen2017"
    comment: "Kapitel 2 (insbesondere Abschnitte 2.3 bis (einschließlich) 2.19)"
  - key: "Aho2008"
    comment: "Abschnitte 2.4 und 4.4"
  - key: "Grune2012"
    comment: "Abschnitte 3.1 bis (einschließlich) 3.4"
assignments:
  - topic: blatt01
youtube:
  - id: TODO
fhmedia:
  - link: "TODO"
    name: "TODO"
---


## Motivation
Lorem Ipsum. Starte mit H2-Level.
...

## Erinnerung Lexer: Zeichenstrom $\to$ Tokenstrom

<!-- XXX analog zu lexer.md (Zusammenfassung) -->

``` {.python size="footnotesize"}
def nextToken():
    while (peek != EOF):  # globale Variable
        switch (peek):
            case '<':   if match('='): consume(); return Token(LE, "<=")
                        else: consume(); return Token(LESS, '<')
            case '[':   consume(); return Token(LBRACK, '[')
            ...
            default:    raise Error("invalid character: "+peek)
    return Token(EOF_Type, "<EOF>")

def match(c):   # lookahead one character
    consume()
    if (peek == c): return True
    else: rollBack(); return False

def consume():
    peek = Buffer[Input]
    Input = (Input+1) mod 2n
    if (Input mod n == 0):
        fill Buffer[Input:Input+n-1]
        Fence = (Input+n) mod 2n
```


## Grundidee LL-Parser

[Grammatik:]{.notes}

```yacc
r : X s ;
```

\bigskip
\bigskip

[LL-Implementierung:]{.notes}

```java
void r() {
    match(X);
    s();
}
```

::: notes
*   Für jede Regel in der Grammatik wird eine Methode/Funktion mit dem selben Namen definiert
*   Referenzen auf ein Token `T` werden durch den Aufruf der Methode `match(T)` aufgelöst
    *   `match(T)` "konsumiert" das aktuelle Token, falls dieses mit `T` übereinstimmt
    *   Anderenfalls löst `match()` eine Exception aus
*   Referenzen auf Regeln `s` werden durch Methodenaufrufe `s()` aufgelöst

*Erinnerung*: In ANTLR werden Parser-Regeln mit einem kleinen und Lexer-Regeln mit einem
großen Anfangsbuchstaben geschrieben.
:::


::: notes
## Alternative Subregeln

```
a | b | c
```

[wird zu einem `switch`-Konstrukt aufgelöst:]{.notes}

\bigskip
\bigskip

```java
switch (lookahead) {
    case predicting_a:
        a(); break;
    case predicting_b:
        b(); break;
    ...
    default: throw new Exception();
}
```
:::


::: notes
## Optionale Subregeln: Eins oder keins

```
(T)?
```

[wird zu einem `if` ohne `else`-Teil:]{.notes}

\bigskip

```java
if (lookahead.predicting_T) { match(T); }
```
:::


::: notes
## Optionale Subregeln: Mindestens eins

```
(T)+
```

[wird zu einer `do-while`-Schleife:]{.notes}

\bigskip

```java
do {
    match(T);
} while (lookahead.predicting_T);
```
:::


## LL(1)-Parser

[Parsen von Sequenzen wie `[A,B,C]` oder `[A,[B,C],D]`]{.notes}

```yacc
list     : '[' elements ']' ;
elements : element (',' element)* ;
element  : ID | list ;
ID       : ('a'..'z' | 'A'..'Z')+ ;
```

::: notes
Formal berechnet man die Lookahead-Mengen mit `FIRST` und `FOLLOW`. Praktisch betrachtet
kann man sich fragen, welche(s) Token eine Phrase in der aktuellen Alternative starten
können.

Für LL(1)-Parser betrachtet man immer das **aktuelle** Token (**genau *EIN* Lookahead-Token**),
um eine Entscheidung zu treffen.
:::

\pause
\bigskip

```python
def list():
    match(LBRACK); elements(); match(RBRACK);
def elements():
    element()
    while lookahead == COMMA:  # globale Variable/Attribut
        match(COMMA); element()
def element():
    if lookahead == ID: match(ID)
    elif lookahead == LBRACK: list()
    else: raise Exception()
```


## Detail: *match()* und *consume()*

```python
def match(x):
    if lookahead == x: consume()
    else: raise Exception()


def consume():
    lookahead = lexer.nextToken()
```

::: notes
Dabei setzt man in der Klasse `Parser` zwei Attribute voraus:

```python
class Parser:
    Lexer lexer
    Token lookahead
```

Starten würde man den Parser nach dem Erzeugen einer Instanz (dabei wird ein Lexer mit
durchgereicht) über den Aufruf der Start-Regel, also beispielsweise `parser.list()`.

*Anmerkung*: Mit dem generierten Parse-Tree bzw. *AST* beschäftigen wir uns später
(\blueArrow ["Interpreter: AST-Traversierung"](cb_interpreter1.html)).

[Beispiel: NestedLists.g4 (grun NestedLists list -tree/-gui)]{.bsp}
:::


## Vorrangregeln

```
1+2*3 == 1+(2*3) != (1+2)*3
```

[Die Eingabe `1+2*3` muss als `1+(2*3)` interpretiert werden, da `*` Vorrang vor `+` hat.]{.notes}

[Tafel: Unterschiede im AST]{.bsp}

\pause

[Dies formuliert man üblicherweise in der Grammatik:]{.notes}

```yacc
expr : expr '+' term
     | term
     ;
term : term '*' INT
     | INT
     ;
```

::: notes
ANTLR nutzt die Strategie des ["*precedence climbing*"](https://www.antlr.org/papers/Clarke-expr-parsing-1986.pdf)
und löst nach der *Reihenfolge der Alternativen* in einer Regel auf. Entsprechend könnte man die obige Grammatik
unter Beibehaltung der Vorrangregeln so in ANTLR (v4) formulieren:
:::

\pause
\bigskip

```yacc
expr : expr '*' expr
     | expr '+' expr
     | INT
     ;
```


## Linksrekursion

::: notes
Normalerweise sind linksrekursive Grammatiken nicht mit einem LL-Parser behandelbar. Man muss die Linksrekursion
manuell auflösen und die Grammatik umschreiben.

**Beispiel**:
:::

```yacc
expr : expr '*' expr | expr '+' expr | INT ;
```

\bigskip

[Diese linksrekursive Grammatik könnte man (unter Beachtung der Vorrangregeln) etwa so umformulieren:]{.notes}

```yacc
expr     : addExpr ;
addExpr  : multExpr ('+' multExpr)* ;
multExpr : INT ('*' INT)* ;
```

::: notes
ANTLR4 kann Grammatiken mit *direkter* Linksrekursion auflösen. Für frühere Versionen von ANTLR muss man die
Rekursion manuell beseitigen.

Vergleiche ["ALL(\*)" bzw. "Adaptive LL(\*)"](https://www.antlr.org/papers/allstar-techreport.pdf).
:::

\bigskip
\bigskip
\bigskip
\pause

**Achtung**: Mit *indirekter* Linksrekursion kann ANTLR4 *nicht* umgehen:

```yacc
expr : expM | ... ;
expM : expr '*' expr ;
```

[\blueArrow\ *Nicht* erlaubt!]{.notes}


::: notes
## Assoziativität

Die Eingabe `2^3^4` sollte als `2^(3^4)` geparst werden.  [Analog sollte `a=b=c` in C als `a=(b=c)` verstanden werden.]{.notes}

\bigskip

Per Default werden Operatoren wie `+` in ANTLR *links-assoziativ* behandelt, d.h. die Eingabe `1+2+3` wird
als `(1+2)+3` gelesen. Für *rechts-assoziative* Operatoren muss man ANTLR dies in der Grammatik mitteilen:

```yacc
expr : expr '^'<assoc=right> expr
     | INT
     ;
```

*Anmerkung*: Laut [Doku](https://github.com/antlr/antlr4/blob/master/doc/left-recursion.md) gilt die Angabe
`<assoc=right>` immer für die jeweilige Alternative und muss seit Version 4.2 an den Alternativen-Operator `|`
geschrieben werden. In der Übergangsphase sei die Annotation an Tokenreferenzen noch zulässig, würde aber
ignoriert?!
:::


## LL(k)-Parser

```yacc
expr : ID '++' // x++
     | ID '--' // x--
     ;
```

::: notes
Die obige Regel ist für einen LL(1)-Parser nicht deterministisch behandelbar, da die Alternativen
mit  dem selben Token beginnen (die Lookahead-Mengen überlappen sich). Entweder benötigt man zwei
Lookahead-Tokens, also einen LL(2)-Parser, oder man muss die Regel in eine äquivalente LL(1)-Grammatik
umschreiben:
:::

\bigskip
\bigskip
\pause

```yacc
expr : ID ('++' | '--') ; // x++ oder x--
```

::: notes
Analog

```yacc
list     : '[' elements ']' ;
elements : element (',' element)* ;
element  : NAME '=' NAME | NAME | list ;
NAME     : ('a'..'z' | 'A'..'Z')+ ;
```

bzw.

```yacc
element  : NAME ('=' NAME)? | list ;
```
:::


## LL(k)-Parser: Implementierung mit Ringpuffer

::: notes
Für einen größeren Lookahead benötigt man einen Puffer für die Token. Für einen Lookahead von
$k$ Token (also einen LL(k)-Parser) würde man einen Puffer mit $k$ Plätzen anlegen und diesen
wie einen Ringpuffer benutzen. Dabei ist `p` der Index des aktuellen Lookahead-Tokens.

```python
class Parser:
    Lexer lexer
    int k = 3  # Lookahead: 3 Token
    Token[k] lookahead  # Ringpuffer mit k Plätzen (vorbefüllt via Konstruktor)
    int p = 0  # aktuelle Tokenposition im Ringpuffer
```
:::


```python
def consume():
    lookahead[p] = lexer.nextToken()
    p = (p+1) % k

def LT(i):
    return lookahead[(p+i-1) % k]  # i==1: p

def match(x):
    if LT(1) == x: consume()
    else: raise Exception()
```

[Tafel: Beispiel mit Ringpuffer: k=3 und "[a,b=c]"]{.bsp}

<!-- XXX
Beispiel S. 46 in Parr: "Language Implementation Patterns"

*   Eingabe: "[a,b=c]"
*   Puffer (3 Token):
    *   `[a,` (p=0)
    *   `ba,` (p=1)
    *   `b=,` (p=2)
    *   `b=c` (p=0)
    *   `]=c` (p=1)
    ...
-->


## Wrap-Up

*   LL(1) und LL(k) mit festem Lookahead
*   Implementierung von Vorrang- und Assoziativitätsregeln





<!-- DO NOT REMOVE - THIS IS A LAST SLIDE TO INDICATE THE LICENSE AND POSSIBLE EXCEPTIONS (IMAGES, ...). -->
::: slides
## LICENSE
![](https://licensebuttons.net/l/by-sa/4.0/88x31.png)

Unless otherwise noted, this work is licensed under CC BY-SA 4.0.

### Exceptions
*   TODO (what, where, license)
:::
