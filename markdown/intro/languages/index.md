---
type: lecture-cg
title: "Bandbreite der Programmiersprachen"
menuTitle: "Programmiersprachen"
author: "Carsten Gips (FH Bielefeld)"
weight: 2
readings:
  - key: "Aho2008"
    comment: "Kapitel 1 Introduction"
  - key: "Grune2012"
    comment: "Kapitel 1 Introduction"
youtube:
  - id: TODO
fhmedia:
  - link: TODO
    name: "TODO"
---


## 99 Bottles of Beer

> 99 bottles of beer on the wall, 99 bottles of beer.
> Take one down and pass it around, 98 bottles of beer on the wall.
>
> 98 bottles of beer on the wall, 98 bottles of beer.
> Take one down and pass it around, 97 bottles of beer on the wall.
>
> [...]
>
> 2 bottles of beer on the wall, 2 bottles of beer.
> Take one down and pass it around, 1 bottle of beer on the wall.
>
> 1 bottle of beer on the wall, 1 bottle of beer.
> Take one down and pass it around, no more bottles of beer on the wall.
>
> No more bottles of beer on the wall, no more bottles of beer.
> Go to the store and buy some more, 99 bottles of beer on the wall.

[Quelle: [99-bottles-of-beer.net/lyrics.html](https://www.99-bottles-of-beer.net/lyrics.html)]{.origin}


## Imperativ, Hardwarenah: C

``` {.c size="footnotesize"}
 #define MAXBEER (99)
 void chug(int beers);
 main() {
    register beers;
    for(beers = MAXBEER; beers; chug(beers--))  puts("");
    puts("\nTime to buy more beer!\n");
 }
 void chug(register beers) {
    char howmany[8], *s;
    s = beers != 1 ? "s" : "";
    printf("%d bottle%s of beer on the wall,\n", beers, s);
    printf("%d bottle%s of beeeeer . . . ,\n", beers, s);
    printf("Take one down, pass it around,\n");
    if(--beers) sprintf(howmany, "%d", beers); else strcpy(howmany, "No more");
    s = beers != 1 ? "s" : "";
    printf("%s bottle%s of beer on the wall.\n", howmany, s);
 }
```

[Quelle: [99-bottles-of-beer.net/language-c-116.html](https://www.99-bottles-of-beer.net/language-c-116.html), by Bill Wein]{.origin}

::: notes
*   Imperativ
*   Procedural

*   Statisches Typsystem
*   Resourcenschonend, aber "unsicher": Programmierer muss wissen, was er tut
*   Relativ hardwarenah

*   Einsatz: Betriebssysteme, Systemprogrammierung
:::


## Imperativ, Objektorientiert: Java

```java
class bottles {
    public static void main(String args[]) {
        String s = "s";
        for (int beers=99; beers>-1;) {
            System.out.print(beers + " bottle" + s + " of beer on the wall, ");
            System.out.println(beers + " bottle" + s + " of beer, ");
            if (beers==0) {
                System.out.print("Go to the store, buy some more, ");
                System.out.println("99 bottles of beer on the wall.\n");
                System.exit(0);
            } else
                System.out.print("Take one down, pass it around, ");
            s = (--beers == 1)?"":"s";
            System.out.println(beers + " bottle" + s + " of beer on the wall.\n");
        }
    }
}
```

[Quelle: [99-bottles-of-beer.net/language-java-4.html](https://www.99-bottles-of-beer.net/language-java-4.html), by Sean Russell]{.origin}

::: notes
*   Imperativ
*   Objektorientiert
*   Multi-Threading

*   Basiert auf C/C++
*   Statisches Typsystem
*   Automatische Garbage Collection
*   "Sichere" Architektur: Laufzeitumgebung fängt viele Probleme ab

*   Architekturneutral: Nutzt Bytecode und eine JVM

*   Einsatz: High-Level All-Purpose Language
:::


## Logisch: Prolog

```prolog
bottles :-
    bottles(99).

bottles(1) :-
    write('1 bottle of beer on the wall, 1 bottle of beer,'), nl,
    write('Take one down, and pass it around,'), nl,
    write('Now they are all gone.'), nl,!.
bottles(X) :-
    write(X), write(' bottles of beer on the wall,'), nl,
    write(X), write(' bottles of beer,'), nl,
    write('Take one down and pass it around,'), nl,
    NX is X - 1,
    write(NX), write(' bottles of beer on the wall.'), nl, nl,
    bottles(NX).
```

[Quelle: [99-bottles-of-beer.net/language-prolog-965.html](https://www.99-bottles-of-beer.net/language-prolog-965.html), by M\@]{.origin}

::: notes
*   Deklarativ
*   Logisch: Definition von Fakten und Regeln; eingebautes Beweissystem

*   Einsatz: Theorem-Beweisen, Natural Language Programming (NLP), Expertensysteme, ...
:::


## Funktional: Haskell

```haskell
bottles 0 = "no more bottles"
bottles 1 = "1 bottle"
bottles n = show n ++ " bottles"

verse 0   = "No more bottles of beer on the wall, no more bottles of beer.\n"
         ++ "Go to the store and buy some more, 99 bottles of beer on the wall."

verse n   = bottles n ++ " of beer on the wall, " ++ bottles n ++ " of beer.\n"
         ++ "Take one down and pass it around, " ++ bottles (n-1)
                                                 ++ " of beer on the wall.\n"

main      = mapM (putStrLn . verse) [99,98..0]
```

[Quelle: [99-bottles-of-beer.net/language-haskell-1070.html](https://www.99-bottles-of-beer.net/language-haskell-1070.html), by Iavor]{.origin}

::: notes
*   Deklarativ
*   Funktional
*   Lazy, pure

*   Statisches Typsystem
*   Typinferenz

*   Algebraische Datentypen, Patternmatching

*   Einsatz: Compiler, DSL, Forschung
:::


## Brainfuck

::: center
![](images/screenshot_brainfuck_99bottles.png){width="15%"}

[Quelle: [99-bottles-of-beer.net/language-brainfuck-2542.html](https://99-bottles-of-beer.net/language-brainfuck-2542.html), by Michal Wojciech Tarnowski]{.origin}
:::

::: notes
*   Imperativ
*   Feldbasiert (analog zum Band der Turingmaschine)

*   8 Befehle: Zeiger und Zellen inkrementieren/dekrementieren, Aus- und Eingabe, Sprungbefehle
:::



## Programmiersprache Lox

```
fun fib(x) {
    if (x == 0) {
        return 0;
    } else {
        if (x == 1) {
            return 1;
        } else {
            fib(x - 1) + fib(x - 2);
        }
    }
}

var wuppie = fib;
wuppie(4);
```

::: notes
*   Die Sprache "Lox" finden Sie hier: [craftinginterpreters.com/the-lox-language.html](https://www.craftinginterpreters.com/the-lox-language.html)

*   C-ähnliche Syntax
*   Imperativ, objektorientiert, Funktionen als *First Class Citizens*, Closures

*   Dynamisch typisiert
*   Garbage Collector
*   Statements und Expressions

*   (Kleine) Standardbibliothek eingebaut

Die Sprache ähnelt stark anderen modernen Sprachen und ist gut geeignet, um an ihrem
Beispiel Themen wie Scanner/Parser/AST, Interpreter, Object Code und VM zu studieren :)
::::


## Wrap-Up

*   Compiler übersetzen formalen Text in ein anderes Format

\bigskip

*   Berücksichtigung von unterschiedlichen
    -   Sprachkonzepten (Programmierparadigmen)
    -   Typ-Systemen
    -   Speicherverwaltungsstrategien
    -   Abarbeitungsstrategien







<!-- DO NOT REMOVE - THIS IS A LAST SLIDE TO INDICATE THE LICENSE AND POSSIBLE EXCEPTIONS (IMAGES, ...). -->
::: slides
## LICENSE
![](https://licensebuttons.net/l/by-sa/4.0/88x31.png)

Unless otherwise noted, this work is licensed under CC BY-SA 4.0.

### Exceptions

Citations of "99 Bottles of Beer" lyrics and programming language examples:
*   [99-bottles-of-beer.net/lyrics.html](https://www.99-bottles-of-beer.net/lyrics.html) (Author unknown, Website hosted by Oliver Schade, Gregor Scheithauer, and Stefan Scheler)
*   [99-bottles-of-beer.net/language-c-116.html](https://www.99-bottles-of-beer.net/language-c-116.html), by Bill Wein
*   [99-bottles-of-beer.net/language-java-4.html](https://www.99-bottles-of-beer.net/language-java-4.html), by Sean Russell
*   [99-bottles-of-beer.net/language-prolog-965.html](https://www.99-bottles-of-beer.net/language-prolog-965.html), by M\@
*   [99-bottles-of-beer.net/language-haskell-1070.html](https://www.99-bottles-of-beer.net/language-haskell-1070.html), by Iavor
*   [99-bottles-of-beer.net/language-brainfuck-2542.html](https://99-bottles-of-beer.net/language-brainfuck-2542.html), by Michal Wojciech Tarnowski

:::
