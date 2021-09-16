---
type: lecture-cg
title: "LL-Parser: Fortgeschrittene Techniken"
menuTitle: "LL: Fortgeschrittene Techniken"
author: "Carsten Gips (FH Bielefeld)"
weight: 3
readings:
  - key: "Parr2010"
    comment: "Kapitel 3"
  - key: "Parr2014"
    comment: ""
  - key: "Mogensen2017"
    comment: "Kapitel 2 (insbesondere Abschnitte 2.3 bis (einschließlich) 2.19)"
  - key: "Aho2008"
    comment: "Abschnitte 2.4 und 4.4"
  - key: "Grune2012"
    comment: "Abschnitte 3.1 bis (einschließlich) 3.4"
quizzes:
  - link: XYZ
    name: "Testquizz (URL from `'`{=markdown}Invite more Players`'`{=markdown})"
assignments:
  - topic: blatt01
youtube:
  - id: XYZ (ID)
  - link: https://youtu.be/XYZ
    name: "Use This As Link Text (Link from `'share'`{=markdown}-Button)"
fhmedia:
  - link: https://www.fh-bielefeld.de/medienportal/m/XYZ
    name: "Use This As Link Text (Link from `'share'`{=markdown}-Button)"
sketch: true
---


## Motivation
Lorem Ipsum. Starte mit H2-Level.
...

## LL-Parser mit Backtracking

[Problem: Manchmal kennt man den nötigen Lookahead nicht vorher. Beispiel:]{.notes}

```cpp
void bar();         // Vorwärtsdeklaration
void bar() { ...}   // Definition
```

[Entsprechend sähe die Grammatik aus:]{.notes}

```yacc
func : def | decl ;
def  : head '{' body '}' ;
decl : head ';' ;
head : ... ;
```

::: notes
Hier müsste man erst den gesamten Funktionskopf parsen, bevor man entscheiden kann, ob es sich um
eine Deklaration oder eine Definition handelt. Unglücklicherweise gibt es keine Längenbeschränkung
bei den Funktionsnamen ...

Mit Hilfe von Backtracking kann man zunächst spekulativ matchen und beim Auftreten eines Fehlers
die Spekulation rückgängig machen:
:::

\pause
\bigskip

```python
def func():
    if speculate_def: def()
    elif speculate_decl: decl()
    else: raise Exception()
```

::: notes
Die erste Alternative, die passt, gewinnt. Über die Reihenfolge der Spekulationen kann man
entsprechend Vorrangregeln implementieren.
:::


## Spekulatives Matchen

```python
def speculate_decl():
    success = True

    mark()                  # markiere aktuelle Position
    try:
        decl()              # probiere Regel decl
    catch:
        success = False
    release()               # Rollback

    return success
```

::: notes
Vor dem spekulativen Matchen muss die aktuelle Position im Tokenstrom markiert werden. Falls
der Versuch, die Deklaration zu matchen nicht funktioniert, wird `decl()` eine Exception werfen,
entsprechend wird die Hilfsvariable gesetzt. Anschließend muss noch mit `release()` das aktuelle
Token wieder hergestellt werden.
:::


## Spekulatives Matchen: Hilfsmethoden I/II

```python
class Parser:
    Lexer lexer
    Stack<INT> markers     # Stack für Integer
    List<Token> lookahead  # Puffer (1 Token vorbefüllt via Konstruktor)
    int p = 0  # aktuelle Tokenposition im lookahead-Puffer

    def mark():
        markers.push(p)

    def release():
        p = markers.pop()
```


## Spekulatives Matchen: Hilfsmethoden II/II

```python
def consume():
    p++
    if p == lookahead.count() and markers.isEmpty():
        p = 0; lookahead.clear()
    sync(1)

def LT(i):
    sync(i)
    return lookahead.get(p+i-1)

def sync(i):
    if p+i-1 > lookahead.count()-1:
        n = (p+i-1) - (lookahead.count()-1)
        for (i=0; i<n; i++):
            lookahead.add(lexer.nextToken())
```

::: notes
**Hinweis**: Die Methode `count` liefert die Anzahl der aktuell gespeicherten Elemente
in `lookahead` zurück (nicht die Gesamtzahl der Plätze in der Liste -- diese kann größer
sein). Mit der Methode `add` wird ein Element hinten an die Liste angefügt, dabei wird
das Token auf den nächsten Index-Platz (`count`) geschrieben und ggf. die Liste automatisch
um weitere Speicherplätze ergänzt. Über `clear` werden die Elemente in der Liste gelöscht,
aber der Speicherplatz erhalten (d.h. `count()` liefert den Wert 0, aber ein `add` müsste
nicht erst die Liste mit weiteren Plätzen erweitern, sondern könnte direkt an Index 0 das
Token schreiben).
:::

[Tafel: Beispiel mit dynamisch wachsendem Puffer]{.bsp}

<!-- XXX
*   Fall 1: `p` steht am Ende und kein Spekulieren: `consume()` würde `p=0` setzen
*   Fall 2: `p` steht mitten im Puffer (mit Spekulieren), `LT(2)` würde den Platz hinter `p` mit befüllen
    (dahinter noch leere Plätze)
*   Fall 3: wie eben, aber `LT(8)` würde Puffer um weitere Plätze ergänzen
-->

::: notes
Backtracking führt zu Problemen:

1.  BT kann sehr langsam sein (Ausprobieren vieler Alternativen)
2.  Der spekulative Match muss ggf. rückgängig gemacht werden
3.  Man muss bereits gematchte Strukturen erneut matchen (\blueArrow Vorgriff: Packrat-Parsing)
:::


## Verbesserung Backtracking: Packrat Parser (Memoizing)

```yacc
stat :  list EOF  |  list '=' list  ;
```

::: notes
Bei Eingabe `[a,b]=[c,d]` wird zunächst spekulativ die erste Alternative untersucht und
eine `list` gematcht. Da die Alternative nicht komplett passt, muss die Spekulation
rückgängig gemacht werden und die zweite Alternative untersucht werden. Dabei muss man
den selben Input erneut auf `list` matchen!

Idee: Wenn `list` sich merken würde, ob damit ein bestimmter Teil des Tokenstroms bereits
behandelt wurde (erfolgreich oder nicht), könnte man das Spekulieren effizienter gestalten.
Jede Regel muss also durch eine passende Regel mit Speicherung ergänzt werden.

*Anmerkung*: Mit `EOF` kann man ANTLR-Parser zwingen, den kompletten Eingabestrom zu betrachten.
:::

\pause

::: center
![Beispiel zu Packrat](images/packrat){height="85%"}\
:::


## Details zum Packrat-Parsing

``` {.python size="scriptsize"}
Map<INT, INT> list_memo

def _list():  # Original List-Methode (umbenannt)
    match(LBRACK); elements(); match(RBRACK);

def list():   # Ersatz-Methode mit Packrat-Parsing
    failed = False
    start = p
    if markers.isNotEmpty() and alreadyParsed(list_memo): return
    try: _list()  # nicht am Spekulieren oder noch nicht untersucht
    catch(e): failed = True; raise e
    finally:
        if markers.isNotEmpty(): memoize(list_memo, start, failed)
```

::: notes
*   Wenn am Spekulieren und bereits untersucht: Direkt zurückkehren (und Vorspulen)
*   Sonst Original-Regel ausführen
*   Exception: Regel hatte keinen Erfolg; merken und Exception weiter reichen
*   Falls wir am Spekulieren sind: Ergebnis für diese Startposition und diese Regel merken
:::

``` {.python size="scriptsize"}
def memoize(memo, start, failed):
    stop = failed ? -1 : p
    memo.put(start, stop)

def alreadyParsed(memo):
    idx = memo.get(p)
    if idx == null: return False
    if idx == -1: raise Exception()
    p = idx  # Vorspulen
    return True
```

::: notes
*   `memoize` wird nach dem Test der Regel aufgerufen (im spekulativen Modus)
    *   Falls Regel erfolgreich, dann wird die Start-Position und die aktuelle Position
        (Stopp-Position) notiert
    *   Falls Regel nicht erfolgreich, wird als Stopp-Position der Wert `-1` genutzt

*   `alreadyParsed` wird im spekulativen Fall vor dem (erneuten) Test einer Regel aufgerufen
    *   Falls aktuelle Position noch nicht in der Tabelle, dann wurde die Regel offenbar noch
        nicht getestet (an dieser Position)
    *   Falls für die aktuelle Position eine `-1` in der Tabelle steht, wurde die Regel bereits
        an dieser Position getestet, aber nicht erfolgreich. Dies würde wieder der Fall sein,
        also kann man direkt eine Exception auslösen
    *   Anderenfalls wurde für die aktuelle Startposition die Regel bereits erfolgreich getestet
        (aber die Spekulation passte nicht), also wird einfach "vorgespult", d.h. `p` auf die
        vermerkte Endposition gesetzt.


**Anmerkung**: Die Funktion `consume()` muss passend ergänzt werden: Falls der Parser nicht
(mehr) spekuliert, d.h. der `markers`-Stack leer ist, müssen alle `*_memo` zurückgesetzt werden!

```python
def consume():
    p++
    if p == lookahead.count() and markers.isEmpty():
        p = 0
        lookahead.clear()
        clearAllMemos()  # leere alle "regel"_memo-Maps
    sync(1)
```
:::


## Semantische Prädikate

Problem in Java: `enum` ab Java5 Schlüsselwort [(vorher als Identifier-Name verwendbar)]{.notes}

```yacc
prog : (enumDecl | stat) ;
stat : ... ;

enumDecl : ENUM id '{' id (',' id)* '}' ;
```

::: notes
Wie kann ich eine Grammatik bauen, die sowohl für Java5 und später als auch für die Vorgänger
von Java5 funktioniert?

Angenommen, man hätte eine Hilfsfunktion ("Prädikat"), mit denen man aus dem Kontext heraus
die Unterscheidung treffen kann, dann würde die Umsetzung der Regel ungefähr so aussehen:
:::

\bigskip
\pause

```python
def prog():
    if LT(1) == ENUM and java5: enumDecl()
    else: stat()
```


## Semantische Prädikate in ANTLR

::: notes
### Semantische Prädikate in Parser-Regeln
:::

```yacc
@parser::members {public static boolean java5;}

prog : ({java5}? enumDecl | stat)+ ;
stat : ... ;

enumDecl : ENUM id '{' id (',' id)* '}' ;
```

::: notes
Prädikate aktivieren bzw. deaktivieren alles, was nach der Abfrage des Prädikats gematcht
werden könnte. Entsprechend könnte man die obige Formulierung auch so schreiben:

Wichtig ist hierbei nur, dass das Prädikat ausgewertet wird, bevor ein "`enum`"-Token im
Eingabestrom auftritt.


### Semantische Prädikate in Lexer-Regeln

Alternativ für Lexer-Regeln:
:::

```yacc
ENUM : 'enum' {java5}? ;
ID   : [a-zA-Z]+ ;
```

::: notes
Bei Token kommt das Prädikat erst am rechten Ende einer Lexer-Regel vor, da der Lexer keine
Vorhersage macht, sondern nach dem längsten Match sucht und die Entscheidung erst trifft,
wenn das ganze Token gesehen wurde. Bei Parser-Regeln steht das Prädikat links vor der
entsprechenden Alternative, da der Parser mit Hilfe des Lookaheads Vorhersagen trifft, welche
Regel/Alternative zutrifft.

*Anmerkung*: Hier wurden nur Variablen eingesetzt, es können aber auch Methoden/Funktionen
genutzt werden. In Verbindung mit einer Symboltabelle (["Symboltabellen"](cb_symboltabellen1.html))
und/oder mit Attributen und Aktionen in der Grammatik (["Attribute"](cb_attribute.html) und
["Interpreter: Attribute+Aktionen"](cb_interpreter2.html)) hat man hier ein mächtiges Hilfswerkzeug!
:::

[[Beispiel: EnumP.g4 und EnumL.g4]{.bsp}]{.notes}


## Wrap-Up

*   LL(1) und LL(k): Erweiterungen
    *   Dynamischer Lookahead: BT-Parser mit Packrat-Ergänzung
    *   Semantische Prädikate zum Abschalten von Alternativen





<!-- DO NOT REMOVE - THIS IS A LAST SLIDE TO INDICATE THE LICENSE AND POSSIBLE EXCEPTIONS (IMAGES, ...). -->
::: slides
## LICENSE
![](https://licensebuttons.net/l/by-sa/4.0/88x31.png)

Unless otherwise noted, this work is licensed under CC BY-SA 4.0.

### Exceptions
*   TODO (what, where, license)
:::
