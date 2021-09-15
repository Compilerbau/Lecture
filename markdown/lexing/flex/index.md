---
type: lecture-cg
title: "Flex: Lexer generieren"
author: "Carsten Gips (FH Bielefeld)"
weight: 3
readings:
  - key: "Levine2009"
    comment: "Kapitel 1, 2 und 5"
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

## Hello World

```lex
%{
#include <stdio.h>
%}
%option noyywrap

%%

"hello "[a-z]+      printf("\t%s\n", yytext);
.|\n                /* ignore */

%%

int main(void) {
    yylex();
}
```

[Konsole: hello.l (flex, lex.yy.c, gcc)]{.bsp}


::::::::: notes
### Flex: Fast Lexical Analyzer Generator

Flex ("*Fast Lexical Analyzer Generator*") ist ein Lexer-Generator für C/C++.
Der ursprünglich von Google-CEO Eric Schmidt entwickelte Vorgänger *Lex*
wurde von Vern Paxson komplett überarbeitet und verbessert und ist auf
[github.com/westes/flex](https://github.com/westes/flex) im Source-Code verfügbar.

Flex wird typischerweise zusammen mit dem Parser-Generator [Bison](https://www.gnu.org/software/bison/)
eingesetzt (Nachfolger von [Yacc](https://en.wikipedia.org/wiki/Yacc) ("*Yet Another Compiler-Compiler*")).

Flex transformiert die regulären Ausdrücke in DFA, die zum Matchen des Eingabezeichenstroms
benutzt werden.


### Aufbau der Eingabedatei

```lex
declarations
%%
patterns
%%
user code
```

Die Eingabedatei gliedert sich in drei Abschnitte, getrennt durch die Zeichenfolge `%%`.

1.  Im ersten Abschnitt können Namen definiert werden sowie Startbedingungen angegeben werden.

    Der Code zwischen `%{` und `%}` wird nah am Anfang im generierten C-Code kopiert.

    Zusätzlich lassen sich hier diverse Optionen aktivieren, beispielsweise wie oben die Option
    `noyywrap`, die die mitgelieferte Mini-Bibliothek `libfl.so` "deaktiviert" (enthält ein minimales
    `main()`-Programm sowie eine Funktion `yywrap()`, die am Dateiende aufgerufen wird).

2.  Im zweiten Abschnitt werden die Regeln angegeben. Dies sind reguläre Ausdrücke ("Pattern"),
    gefolgt von möglichem C-Code. Dieser wird dann aufgerufen, wenn die Regel matcht.

    Jedes Pattern muss am Zeilenanfang beginnen! Der Aufbau der Pattern entspricht weitestgehend
    den üblichen Regeln.

    Wenn der Code nur ein einzelnes Statement enthält, können die geschweiften Klammern entfallen.

3.  Im dritten Abschnitt folgt der Code, der beispielsweise die `main()`-Funktion enthält.


Die Funktion `yylex()` ruft den generierten Scanner auf, welcher per Default auf `stdin` liest.

Mit `yytext` kann man auf den Text des gerade matchenden Patterns zugreifen (vordefinierte
Variable `extern char yytext[]`).

Vereinfachung: Mit `ECHO` wird `yytext` ausgegeben (damit würde man sich im obigen Beispiel das `printf()` sparen).


### Aufruf von Flex

Mit Hilfe von `flex <datei>` erzeugt man eine neue Datei `lex.yy.c` mit dem generierten Scanner.
Diese Datei kompiliert man mit einem C-Compiler, etwa `gcc lex.yy.c` und erhält das entsprechende
ausführbare Programm.
:::::::::


## Word Count

```lex
%{
#include <stdio.h>
int chars = 0;  int words = 0;  int lines = 0;
%}
word    [a-z]+
eol     \n
char    .
%%
{word}  { words++; chars += strlen(yytext); }
{eol}   { lines++; }
{char}  { chars++; }
%%
int main(void) {
    yylex();
    printf("%8d%8d%8d\n", lines, words, chars);
}
```

[Konsole: wc.l (flex, lex.yy.c, gcc)]{.bsp}


::: notes
## Anmerkungen

1.  Wenn mehrere Pattern matchen, bevorzugt Flex den längeren Match.

    Beispiel:

    ```lex
    "+"     { return ADD; }
    "="     { return ASSIGN; }
    "+="    { return ASSIGNADD; }
    ```

    Die Eingabe "+=" würde als *ein* Token (`ASSIGNADD`) gematcht (statt der Folge `ADD` und `ASSIGN`),
    da hier der Match länger ist.

2.  Wenn es mehrere gleichlange "längste" Matches gibt, gewinnt die zuerst definierte Regel.

    Beispiel:

    ```lex
    "if"        { return KEYIF; }
    "else"      { return KEYELSE; }
    [a-zA-Z]+   { return IDENTIFIER; }
    ```

    Die Eingabe "if" würde als das Token `KEYIF` gematcht (statt `IDENTIFIER`), da die Regel für
    `KEYIF` vor der Regel für `IDENTIFIER` definiert wurde.

3.  Definierte *Namen*, wie oben im Beispiel `word`, `eol` und `char`, stellen Abkürzungen für
    die entsprechenden regulären Ausdrücke dar. Bei der Verwendung muss der Name in geschweiften
    Klammern stehen.

4.  Mit `REJECT` als Aktion würde man den Scanner zur nächstbesten matchenden Regel verweisen.
    Da man vorher eigene Funktionen o.ä. aufrufen kann, ist dies eine Möglichkeit, auf bestimmte
    Zeichenfolgen besonders zu reagieren.

    Beispiel:

    ```lex
    "wuppie"    { fluppie(); REJECT; }
    [^ \t\n]+   ++word_count;
    ```

    Bei der Eingabe "wuppie" würde zunächst die erste Regel matchen und die Funktion `fluppie()`
    aufgerufen werden. Danach würde mit `REJECT` der Match zurückgewiesen und die nächstbeste
    Alternative gesucht und entsprechend die Variable `word_count` hochgezählt (wie bei allen
    anderen Wörtern auch).
:::


::: notes
## Dateien scannen

```lex
%%

int main(int argc, char **argv) {
    for (int i = 1; i< argc; i++) {
        FILE *f = fopen(argv[i], "r");

        yyrestart(f);
        yylex();

        fclose(f);
    }
}
```

Mit `yyrestart(f)` arbeitet der Scanner mit der Datei `f` weiter.

Falls nur eine einzige Datei zu verarbeiten ist, könnte man auch einfach `yyin = fopen(argv[i], "r");`
statt `yyrestart(f);` nutzen (`yyin` ist eine intern definierte Variable). Wenn `yyin` nicht gesetzt
wird, nutzt `yylex()` automatisch `stdin`.


Für feinere Eingriffe gibt es folgende drei Möglichkeiten:

1.  Man lässt die Variable `yyin` auf das gewünschte File zeigen (`extern FILE *yyin;`, Default: `stdin`)
2.  Man legt einen Input-Buffer `YY_BUFFER_STATE` (beispielsweise mit `YY_BUFFER_STATE b = yy_create_buffer(FILE*, YY_BUFF_SIZE);`
    oder `YY_BUFFER_STATE b = yy_scan_string("...");` oder ...) an und nutzt diesen mit Hilfe von `yy_switch_to_buffer(b)`
3.  Man definiert das Makro `YY_INPUT` neu (Default: `#define YY_INPUT(buf,result,max_size) ...`)

Falls nach dem Abarbeiten einer Datei auf eine andere Datei gewechselt werden soll, muss `yyrestart(fp)` für diese
aufgerufen werden. Alternativ kann man `yyin` auf die neue Datei zeigen lassen und das Makro `YY_NEW_FILE` aufrufen.

[Konsole: wc++.l (flex, lex.yy.c, gcc)]{.bsp}
:::


## Token

```lex
%{
enum yytokentype { NUM = 258,  ADD = 259,  SUB = 260 };
int yylval;
%}
%%
"+"         { return ADD; }
"-"         { return SUB; }
[0-9]+      { yylval = atoi(yytext); return NUM; }
[ \t\n]     /* ignore whitespace */
%%
int main(void) {
    int tok;
    while (tok = yylex()) {
        if (tok == NUM) printf("\t%d = %d\n", tok, yylval);
        else printf("\t%d\n", tok);
    }
}
```

::: notes
Die Token werden in Flex/Bison über Integer in der Aufzählung `enum yytokentype` repräsentiert. Diese
Aufzählung wird normalerweise von Bison erzeugt und in einer `.h`-Datei gespeichert. Die dabei generierten
Werte starten bei 258. Der Wert 0 entspricht immer dem Dateiende (*EOF*).

Jeder Aufruf von `yylex()` liefert das nächste Token zurück. Diese Funktion wird von Bison genutzt,
um das nächste Token zu bekommen und die syntaktische Analyse mit Hilfe einer CFG durchzuführen.

Im Beispiel wurde `int yylval` zum Speichern der Tokenwerte des Tokens `NUM` definiert. Normalerweise
ist dies eine Union, um für verschiedene Token die Lexeme speichern zu können.
:::

[Konsole: token.l (flex, lex.yy.c, gcc)]{.bsp}


## Startregeln (*start states*)

```lex
%{
int line_num = 1;
%}
%x comment

%%

"/*"                    BEGIN(comment);

<comment>[^\n]*         /* eat anything that's not a '\n' */
<comment>\n             ++line_num;
<comment>"*/"           BEGIN(INITIAL);

\n                      /* ignore */

%%
```

[Beispiel nach [GNU Flex Manual](ftp://ftp.gnu.org/old-gnu/Manuals/flex-2.5.4/html_mono/flex.html#SEC11)]{.origin}

::: notes
Regeln, deren Pattern mit "`<sc>`" beginnt, sind nur aktiv, wenn der Scanner im Zustand "`sc`" ist.
Initial ist der Scanner immer in "`INITIAL`".

Regeln können mehreren Zuständen zugeordnet werden, beispielsweise `<A,B,C>[a-z]+ { ... }` würde
die Regel den Zuständen `A`, `B` und `C` zuordnen.

Mit `BEGIN(sc)` wechselt der Scanner in den Zustand `sc`.

Startzustände definiert man im ersten Abschnitt mit `%x zustand` oder `%s zustand`. Die erste
Form ist ein *exklusiver* Zustand, d.h. wenn der Scanner in diesem Zustand ist, werden nur die
Regeln aktiv, die explizit mit diesem Zustand markiert sind. Die zweite Form ist ein *inklusiver*
Zustand, wo zusätzlich alle unmarkierten Regeln aktiv sind. Der Zustand "`INITIAL`" existiert immer.
:::


## Wrap-Up

*   Lexer mit Flex generieren
    *   Aufbau der Flex-Datei
    *   Längster Match gewinnt, Gleichstand: die zuerst definierte Regel
    *   `yylex()`, `yytext`, `yylval`, `yytokentype`
    *   Startregeln






<!-- DO NOT REMOVE - THIS IS A LAST SLIDE TO INDICATE THE LICENSE AND POSSIBLE EXCEPTIONS (IMAGES, ...). -->
::: slides

## LICENSE
![](https://licensebuttons.net/l/by-sa/4.0/88x31.png)

Unless otherwise noted, this work is licensed under CC BY-SA 4.0.

### Exceptions
*   TODO (what, where, license)
:::
