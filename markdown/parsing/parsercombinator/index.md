---
type: lecture-bc
title: "PEG-Parser, Pratt-Parser und Parser Combinators"
menuTitle: "PEG-Parser, Pratt-Parser und Parser Combinators"
author: "BC George (FH Bielefeld)"
weight: 7
readings:
  - key: "aho2013compilers"
    comment: "Kapitel 2 und 3"
  - key: "Mogensen2017"
attachments:
  - link: "https://www.fh-bielefeld.de"
---


# PEG Parser

## Generative Systeme vs Recognition basierte Systeme

- Generative Systeme:
    - formale Definition von Sprachen durch Regeln, die rekursive
      angewendet Sätze/Strings der Sprache generieren
	- Beispiel: Sprachen aus der Chomsky Hierarchie definiert durch
      kontextfreie Grammatiken (CFGs) und reguläre Ausdrücke (REs)
- Recognition basierte Systeme:
    - Sprachen definiert in Form von Regeln/Prädikaten, die
      entscheiden ob ein gegebener String Teil der Sprache ist
    - Beispiel: Parsing Expression Grammar (PEG)
- Beispiel:
    - generative: $\lbrace s \in \mathrm{a}^{\ast} | s = (\mathrm{aa})^{n}\rbrace$
    - recognition-based: $\lbrace s \in \mathrm{a}^{\ast} | \left| s \right| \mod 2 = 0 \rbrace$
    - gleiche Sprache
    - Unterschied eher subtil

## Motivation

- Chomsky (CFGs + REs):
    - ursprünglich für natürliche Sprachen
    - adaptiert für maschinenorientierte Sprachen (Eleganz,
      Ausdrucksstärke)
    - Nachteile:
    	- maschinenorientierte Sprachen sollten präzise und eindeutig sein
        - CFGs erlauben mehrdeutige Syntax (gut für natürliche Sprachen)
        - Mehrdeutigkeiten schwer oder gar nicht zu verhindern
          (z.B. C++ Syntax, Lambda abstractions, `let` expressions und
          conditionals in Haskell)
        - Parsing generell nur in super-linearer Zeit

- PEG:
    - Stilistisch ähnlich zu CFGs mit REs (EBNF)
    - **Kernunterschied**: priorisierter Auswahloperator (`/` statt `|`)
        - Alternative Muster werden der Reihe nach getestet
        - erste passende Alternative wird verwendet
        - Mehrdeutigkeiten werden dadurch unmöglich
    - Beispiel:
        - EBNF: $A \rightarrow a\;b\; |\; a$ und $A \rightarrow a\;
          |\; a\; b$ sind äquivalent
        - PEG: $A \leftarrow a\;b\; /\; a$ und $A \leftarrow a\;
          /\; a\; b$ sind verschieden
    - nähe an der Funktionsweise von Parsern (Erkennen von Eingaben)
        - PEGs können als formale Beschreibung eines Top-Down-Parsers
          betrachtet werden

## Definition

**Definition**: Eine Parsing Expression Grammar (PEG) ist ein
 4-Tuple $G = (V_N, V_T, R, e_S)$ mit

- $V_N$ eine endliche Menge von Nicht-Terminalen
- $V_T$ eine endliche Menge von Terminalen
- $R$ eine endliche Menge von Regeln
- $e_S$ eine *Parsing Expression*, die als *Start Expression*
  bezeichnet wird.

Weiterhin gilt $V_N \cap V_T = \emptyset$. Jede Regel $e \in R$
ist ein Paar $(A,e)$ geschrieben als $A \leftarrow e$ mit $A \in
    V_N$ und $e$ eine *Parsing Expression*. Für jedes Nicht-Terminal
$A$ existierte genau ein $e$ mit $A \leftarrow e \in R$.

*Parsing Expressions* werden rekursiv definiert: Seien $e$, $e_1$
und $e_2$ Parsing Expressions, dann gilt dies auch für

1.  den leeren String $\varepsilon$
2.  jedes Terminal $a \in V_T$
3.  jedes Nicht-Terminal $A \in V_N$
4.  die Sequenz $e_1 e_2$
5.  die priorisierte Option $e_1 / e_2$
6.  beliebige Wiederholungen $e^{\ast}$
7.  Nicht-Pradikate $!e$

Operatoren wie `.`, `+`, `?` und `&` sind syntaktischer Zucker und lassen sich auf die obigen Definitionen zurückführen.

## PEG Eigenschaften

- parsebar in linearer Zeit mit unbegrenztem Lookahead (Packrat
  Parsing)
- benötigen mehr Speicher
    - Packrat Parsing lädt gesamtes Programm in den Speicher
    - Speicher ist heute aber keine so große Einschränkung mehr
- PEGs sind wahrscheinlich andere Sprachklasse als CFGs
    - PEGs können manche nicht-CFG Sprachen darstellen (\(a^n b^n
      c^n\))
    - PEGs können alle det. LR(k)-Sprachen darstellen
    - ungelöste Frage: Sind alle CFGs durch PEGs darstellbar?
- bietet neue Möglichkeiten für das Syntax-Design von Sprachen
    - aber auch neue Möglichkeiten für Fehler durch Unachtsamkeit
- Syntaxbeschreibung: keine Unterscheidung zwischen Hierarchie und
  lexikalischen Elementen nötig
    - für gewöhnlich Hierarchie durch CFG und lex. Elem. durch RE
      beschrieben
        - CFGs ungeeignet für lex. Elemente (keine Greedy-Matching,
          keine 'negative' Syntax)
        - REs: keine rekursive Syntax
    - Tokens können hierarchische Eigenschaften haben (verschachtelte
      Kommentare)
    - beliebige Escape-Sequenzen möglich
- Herausfordrung bei PEGs:
    - sind Alternativen vertauschbar ohne die Sprache zu ändern?
    - Analog zur Frage der Mehrdeutihkeit bei CFGs

## Beispiele
### PEG Syntax als PEG

Selbstbeschreibung der PEG ASCII-Syntax. Die Grammatik besteht aus
Regeln der Form $A \leftarrow e$ wobei $A$ ein Nichtterminal und $e$
eine *Parsing Expression* ist.

```
# Hierarchical syntax
Grammar    <- Spacing Definition+ EndOfFile
Definition <- Identifier LEFTARROW Expression

Expression <- Sequence (SLASH Sequence)*
Sequence   <- Prefix*
Prefix     <- (AND / NOT)? Suffix
Suffix     <- Primary (QUESTION / STAR / PLUS)?
Primary    <- Identifier !LEFTARROW
	    / OPEN Expression CLOSE
	    / Literal / Class / DOT

# Lexical syntaxq
Identifier <- IdentStart IdentCont* Spacing
IdentStart <- [a-zA-Z_]
IdentCont  <- IdentStart / [0-9]

Literal    <- ['] (!['] Char)* ['] Spacing
	    / ["] (!["] Char)* ["] Spacing
Class      <- '[' (!']' Range)* ']' Spacing
Range      <- Char '-' Char / Char
Char       <- '\\' [nrt'"\[\]\\]
	    / '\\' [0-2][0-7][0-7]
	    / '\\' [0-7][0-7]?
	    / !'\\' .

LEFTARROW  <- '<-' Spacing
SLASH      <- '/' Spacing
AND        <- '&' Spacing
NOT        <- '!' Spacing
QUESTION   <- '?' Spacing
STAR       <- '*' Spacing
PLUS       <- '+' Spacing
OPEN       <- '(' Spacing
CLOSE      <- ')' Spacing
DOT        <- '.' Spacing
Spacing    <- (Space / Comment)*
Comment    <- '#' (!EndOfLine .)* EndOfLine
Space      <- ' ' / '\t' / EndOfLine
EndOfLine  <- '\r
```

```
| Operator | Type         | Precedence | Description        |
|----------+--------------+------------+--------------------|
| ' '      | primary      |          5 | Literal string     |
| " "      | primary      |          5 | Literal string     |
| [ ]      | primary      |          5 | Character class    |
| .        | primary      |          5 | Any character      |
| (e)      | primary      |          5 | Grouping           |
| e?       | unary suffix |          4 | Optional           |
| e*       | unary suffix |          4 | Zero-or-more       |
| e+       | unary suffix |          4 | One-or-more        |
| &e       | unary prefix |          3 | And-predicate      |
| !e       | unary prefix |          3 | Not-predicate      |
| e1 e2    | binary       |          2 | Sequence           |
| e1 / e2  | binary       |          1 | Prioritized Choice |
```

### Verschachtelte Kommentare

Verschachtelte Kommentare wie in *Pascal* sind möglich, da die lexikalische Syntax von PEGs nicht auf REs beschränkt ist:

```
Comment <- '(*' (Comment / !'*)' .)* '*)'
```

### Beliebige Escape-Sequenzen
Escape-Sequenzen haben meist nur eine stark eingeschränkte Syntax. Eine PEG kann beliebige Ausdrücke in eine Escape-Sequenz erlauben:

```
Expression <- ...
Primary    <- Literal / ...
Literal    <- ["] (!["] Char)* ["]
Char       <- '\\(' Expression ')'
            / !'\\' .
```

Zum Beispiel könnte man dadurch in einer Escape-Sequenz auf Variablen zugreifen: `\(var)`.

### Verschachtelte Template Typen in C++

Bekanntes Problem mit Template-Definitionen in C++: Leerzeichen zwischen Winkelklammern notig um Interpretation als Pipe-Operator (`>>`) zu verhindern:
```
vector<vector<float> > MyMatrix;
```

PEG erlaubt kontextsensitive Interpretation:
```
TemplType <- PrimType (LANGLE TemplType RANGLE)?
ShiftExpr <- PrimExpr (ShiftOper PrimExpr)*
ShiftOper <- LSHIFT / RSHIFT
        
LANGLE    <- '<' Spacing
RANGLE    <- '>' Spacing
LSHIFT    <- '<<' Spacing
RSHIFT    <- '>>' Spacing
```

### Dangling-Else

In CFGs sind verschachtelte if-then(-else) Ausdrücke mehrdeutig
(Shift-Reduce-Konflikt). Dies wird häufig durch informelle Meta-Regeln
oder Erweiterung der Syntax aufgelöst. In PEGs sorgt der prioriserende
Auswahloperator für das korrekte Verhalten.

```
Statement <- IF Cond THEN Statement ELSE Statement
           / IF Cond THEN Statement
           / ...
```

### Nicht-CFG-Sprachen

Ein klassisches Beispiel einer nicht-CFG Sprache ist $a^n b^n
c^n$. Diese Sprache lässt sich mit der folgenden PEG darstellen:

$G = (\lbrace A,B,D \rbrace, \lbrace a,b,c \rbrace, R, D)$

$A \leftarrow a\;A\;b\; /\; \varepsilon$

$B \leftarrow b\;B\;c\; /\; \varepsilon$

$D \leftarrow \& (A\; !b)\; a^{\ast}\; B\; !.$

Regel D lässt sich dabei wie folgt lesen: Matche und Verbrauche eine
beliebig lange Sequenz von a's ($a^{\ast}$) gefolgt von einer Sequenz,
die von Regel B gematcht wird und danach keine weiteren Zeichen hat
($!.$) aber nur wenn die Sequenz auch von $A\; !b$ gematcht
wird. Der erste Teil trifft zu wenn in der Sequenz auf $n$ b's gleich
viele c's folgen während der zweite Teil zutrifft wenn $n$ b's auf $n$
a's folgen.

## Links

- [Wiki](https://en.wikipedia.org/wiki/Parsing_expression_grammar)
- [Urspr. Veröffentlichung](https://bford.info/pub/lang/peg.pdf)
- [PEG Parsers](https://medium.com/@gvanrossum_83706/peg-parsers-7ed72462f97c) (Guido van Rossum)
- [New PEG Parser for CPython](https://www.python.org/dev/peps/pep-0617/) (Python Enhancement Proposal)
- [Parsing Expression Grammars: A Recognition-Based Syntactic Foundation](https://bford.info/pub/lang/peg/)

# Operator Precedence Parsing

## Motivation

-   Probleme mit [CFG und BNF](https://matklad.github.io/2020/04/13/simple-but-powerful-pratt-parsing.html)
-   [Recursive Decent Parser](https://eli.thegreenplace.net/2008/09/26/recursive-descent-ll-and-predictive-parsers/) (RD)
    -   Modelliere Grammatik(-regeln) durch rekursive Funktionen
    -   top-down Ansatz
    -   i.d.R. LL(1) Parser (handgeschrieben)
    -   Generatoren: [ANTLR](http://www.antlr.org/), [Boost.Spirit](http://spirit.sourceforge.net/)
-   [Probleme](https://eli.thegreenplace.net/2009/03/14/some-problems-of-recursive-descent-parsers/) mit RD
    -   Linksrekursion (für handgeschriebene Parser von gringer
        Bedeutung; Ersetzbar durch Schleife, EBNF)
    -   Vorrangregeln (Precedence/Binding Power) und Assoziativität
        von Operatoren
    -   Effizienz (Backtracking)
-   Überleitung zu Operator Precedence
    -   benutzt sowohl Rekursion als auch Schleifen
    -   verbesserung zu RD

## Prinzip und Eigenschaften

Prinzip:

-   [Wikipedia](https://en.wikipedia.org/wiki/Pratt_parser)
-   [Pratt Parsing](https://dev.to/jrop/pratt-parsing)

Eigenschaften:

-   Recognition basiert (insofern das der Parser nicht aus einer
    Grammatik generiert sondern von Hand geschrieben wird)
    
- Verwendet Vorschautoken

-   Interpretiert [Operator-Precedence Grammatik](https://en.wikipedia.org/wiki/Operator-precedence_grammar)
    -   Untermenge der deterministischen kontextfreien Grammatiken
    
- Simple zu Programmieren und kann die Operator-Tabelle während der Programmlaufzeit konsultieren

- Verwendet eine Reihenfolge(precedence) für die Operatoren

  

## Methoden

1.  Klassische Methode (RD)

    -   siehe [Parsing Expressions by Recursive Descent](https://www.engr.mun.ca/~theo/Misc/exp_parsing.htm)
    -   neues Nicht-Terminal für jeden Precedence-Level
    -   Nachteile:
        -   Anzahl der Precedence Level bestimmt Größe und
            Geschwindigkeit des Parsers
        -   Operatoren und Precedence Levels fest in Grammatik eingebaut

2. Dijkstras Shunting Yard Algorithmus (SY)

   - [Shunting-yard algorithm](https://en.wikipedia.org/wiki/Shunting_yard_algorithm) (Wikipedia)

   - Beispiel: [A recursive descent parser with an infix expression evaluator](https://eli.thegreenplace.net/2009/03/20/a-recursive-descent-parser-with-an-infix-expression-evaluator) (Python)

   - Verwendet Stacks für Operatoren und Argumente anstatt Rekursion![Shunting Yard Beispiel](images/shuntingyardStack.png)

   - ```
     while there are tokens to be read:
         read a token
         if the token is a number:
           output.add(token)
         if the token is an operator:
           if operator is a bracket:
           	if left bracket:
           		stack.put(token)
           	else:
           		check if left bracket is in stack
           		while stack.top != left bracket:
           			output.add(stack.pop())	
           		output.add(stack.pop())	 // pop left bracket
           else:
              if token.precedence < stack.top.precedence:
                  output.add(stack.pop())        
              stack.put(token)         
     ```
     
     

3. Top Down Operator Precedence (TDOP) Pratt Parsing

   - [Operator-precedence parser](https://en.wikipedia.org/wiki/Operator-precedence_parser) (Wikipedia)

   - [Pratt Parsing Index and Updates](https://www.oilshell.org/blog/2017/03/31.html) (Sammlung von Artikeln/Posts)

   - [Simple but Powerful Pratt Parsing](https://matklad.github.io/2020/04/13/simple-but-powerful-pratt-parsing.html) (Rust)

   - Generalisierung von Precedence Climbing

   - Verwendet eine Gewichtung (binding Power) statt einer Reihenfolge (precedence)

     - lbp = left Binding Power
     - rbp= Right Binding Power

   - Die Tokenhandler behandeln Notationen unterschiedlich

     - infix notation: a = b - c

       - led (left denotation)

     - prefix from: a = -b

       - nud (Null denotation)

     - ```
       class operator_sub_token(object):
           lbp = 10
           def nud(self):
               return -expression(100)
           def led(self, left):
               return left - expression(10)
               
       class operator_mul_token(object):
           lbp = 20
           def led(self, left):
               return left * expression(20)
       ```

   - hat eine Tokenhandler für jede Operation

     -   operator_add_token, operator_mul_token, operator_sub_token, operator_pow_token
     -   operator_lparen_token, operator_rparen_token

   - Beispiel: [Top-Down operator precedence parsing](https://eli.thegreenplace.net/2010/01/02/top-down-operator-precedence-parsing/) (Python)

   - ```
     def expression(rbp=0):
         global token
         t = token
         token = next()
         left = t.nud()
         while rbp < token.lbp:  # TERMINATION CONDITION
             t = token
             token = next()
             left = t.led(left)  # results in recursive call
         return left     
     ```

   - right Associactive Operators?

     -   Der Parser behandelt die folgende Potenzierungsoperatoren als Unterausdrücke des ersten
     -   indem wir den Ausdruck im Handler der Potenzierung mit einem rbp aufrufen, der niedriger ist als der lbp der Potenzierung

     ```
     class operator_pow_token(object):
         lbp = 30
         def led(self, left):
            return left ** expression(30 - 1)  # RECURSIVE CALL
     ```

   -   [Pratt Parsing and Precedence Climbing Are the Same Algorithm](https://www.oilshell.org/blog/2016/11/01.html)

4. Precedence Climbing (PC)

   - Beispiel: [Parsing expressions by precedence climbing](https://eli.thegreenplace.net/2012/08/02/parsing-expressions-by-precedence-climbing) (Python)

   - [Precedence Climbing is Widely Used](http://www.oilshell.org/blog/2017/03/30.html)

   - Climb **down** the precedence levels (Norvell)

   - Die Operatoren stehen mit Gewicht und Association (left, right) in einer Tabelle

   - Pseudocode

     - ```
       compute_expr(min_prec):
         result = compute_atom()    // Atom is a number or a expression in brackets
         curtoken = next()	
         while curtoken precedence > min_prec:
           prec, assoc = precedence and associativity of current token
           if assoc is left:
             next_min_prec = prec + 1
           else:
             next_min_prec = prec
           rhs = compute_expr(next_min_prec)
           result = compute operator(result, rhs)
       
         return result
       ```
     
     - ```
       6 + 3 * 4 + 2
       ```
     
     -   ```
         * compute_expr(1)
         	* compute_atom() --> 6
         	* compute_expr(2) 					# Loop with '+' left assoc
         		* compute_atom() --> 3
         		* compute_expr(3)				# Loop with '*' left assoc
         			* compute_atom() --> 4
               		* result --> 4	 			# Loop not enterd '+ < *'
                 * result = 3 * 4 --> 12
         		* compute_expr(2)				# Loop with '+' left assoc
         			* compute_atom() --> 2
         			* result --> 2				# Loop not enterd - end of expression
         		* result = 12 + 2 --> 14
             * result = 6 + 14 --> 20 
         ```

5. Vergleich

   -   Shunting Yard, TDOP und Precedence Climbing sind im
       wesentlichen der gleiche Algorithmus:
   -   Im Gegensatz zum klassichen RD ist das Hinzufügen/Ändern von
       Operatoren einfach
       -   RD: Hinzufügen/Ändern von Funktionen im Parser
       -   SY/TDOP/PC: Daten liegen in Tabellenform vor
   -   Mischformen möglich (siehe Shunting Yard in [Parsing Expressions by Recursive Descent](https://www.engr.mun.ca/~theo/Misc/exp_parsing.htm))
   -   Shunting Yard verwendet einen Stack anstatt Rekrusive 
   -   Precedence Climbing wird am häufigsten eingesetzt
   -   Pratt Parsing vs. Precedence Climbing
       -   [Pratt Parsing and Precedence Climbing Are the Same Algorithm](https://www.oilshell.org/blog/2016/11/01.html)
       -   [From Precedence Climbing to Pratt Parsing](https://www.engr.mun.ca/~theo/Misc/pratt_parsing.htm) (Norvell)
       -   Eine While Schleife mit rekursiven Aufruf mit Abbruchbedingung (binding Power/precedence)
       -   Rechts Assoziativität 
           -   In precedence climbing: next_min_prec = prec + 1
           -   In Pratt Parsing: rechte binding power rbp auf lbp-1 gesetzt und der rekursive Aufruf mit ihr durchgeführt
       -   Klammern
           -   In precedence climbing in der rekursiven Parsing Funktion behandelt 
           -   In Pratt Parsing können sie als nud-Funktion für das Token ( behandelt werden

## Anwendung

-   [Haskell](https://en.wikipedia.org/wiki/Haskell_(programming_language))
    -   Benutzerdefinierte Infix-Operatoren mit individueller
        Assoziativität und Vorrang-Regeln
    -   Ein Beispiel für das konsultieren der Operator während der Laufzeit
-   [Raku](https://en.wikipedia.org/wiki/Raku_(programming_language))
    -   im Verbund mit zwei weiteren Parsern (Speed-up beim Parsen von
        arithmetischen Ausdrücken)
-   Taschenrechner
    -   Umwandlung Infix-Notation (menschenlesbar) in umgekehrte
        polnische Notation (optimiert für Auswertung)

## Weitere Links

-   [Introduction to Pratt parsing and its terminology](https://abarker.github.io/typped/pratt_parsing_intro.html) (Python [typped](https://github.com/abarker/typped)
    Dokumentation)
-   [Pratt Parsers: Expression Parsing Made Easy](http://journal.stuffwithstuff.com/2011/03/19/pratt-parsers-expression-parsing-made-easy/) (Java)
-   [Top Down Operator Precedence](http://crockford.com/javascript/tdop/tdop.html) (JavaScript)

# Parser Kombinatoren

## Prinzip 

+ High-order Funktion
  + Funktionen als Parameter
  + Funktion als Rückgabewert
  + Programmiersprachen mit first-class functions
+ Verwendet mehrere Parser als Input und gibt einen Kombinierten Parser output als Rückgabewert zurück
  + parse Tree
  + Indice der Stelle im String die zum stoppen des Parsers geführt hat
+ Die Output der verwendeten Parser:
  + Success: {result, restString}
  + failure: Error Message und Position
+ Beispiel:
  + eine Produktionsregel einer kontextfreien Grammatik kann meherer Alternativen haben, diese Alternativen können aus einer Folge von Nichtterminalen und/oder Terminalen bestehen oder auch nur aus einem einzigen Nichtterminalen und/oder Terminalen oder aus einem leeren String. 
  + Wenn nun ein Simpler Parser für jede Alternative verfügbar ist kann man diese miteinander Kombinieren, um alle Alternativen abzudecken

​	

## Links

-   [Wiki](https://en.wikipedia.org/wiki/Parser_combinator)
-   [Introduction to parser combinators](https://gist.github.com/yelouafi/556e5159e869952335e01f6b473c4ec1)
-   [Parser Combinators: a Walkthrough](https://hasura.io/blog/parser-combinators-walkthrough/)




<!-- DO NOT REMOVE - THIS IS A LAST SLIDE TO INDICATE THE LICENSE AND POSSIBLE EXCEPTIONS (IMAGES, ...). -->
::: slides
## LICENSE
![](https://licensebuttons.net/l/by-sa/4.0/88x31.png)

Unless otherwise noted, this work is licensed under CC BY-SA 4.0.

### Exceptions
*   TODO (what, where, license)
:::
