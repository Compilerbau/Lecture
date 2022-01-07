---
type: lecture-cg
title: "Generierung von Maschinencode"
menuTitle: "Maschinencode"
author: "Carsten Gips (FH Bielefeld)"
weight: 7
readings:
  - key: "Mogensen2017"
    comment: "Kapitel 7 Machine-Code Generation"
youtube:
  - id: TODO
fhmedia:
  - link: https://www.fh-bielefeld.de/medienportal/m/47d7c8fdb987cefc82f49fce22697f3928c70de91d914605eb1377b662aa1b24da40d9169ddc80bef9e01ef495d6be85061589b334b4e8c6634fe1c21d9f42ae
    name: "Direktlink FH-Medienportal: CB Maschinencode"
---


## Einordnung

![](https://raw.githubusercontent.com/munificent/craftinginterpreters/master/site/image/a-map-of-the-territory/mountain.png)

[Quelle: ["A Map of the Territory (mountain.png)"](https://github.com/munificent/craftinginterpreters/blob/master/site/image/a-map-of-the-territory/mountain.png) by [Bob Nystrom](https://github.com/munificent), licensed under [MIT](https://github.com/munificent/craftinginterpreters/blob/master/LICENSE)]{.origin}

::: notes
Die Erzeugung von Maschinencode ist in gewisser Weise ein "Parallelweg" zum Erzeugen von
Bytecode. Die Schwierigkeit liegt darin, die **technischen Besonderheiten** der
**Zielplattform** (Register, Maschinenbefehle) gut zu kennen und sinnvoll zu nutzen.

Häufig nutzt man als Ausgangsbasis den Drei-Adressen-Code als IR, der strukturell dem zu
erzeugenden Maschinencode bereits recht ähnlich ist. Oder man macht sich die Sache einfach
und generiert LLVM IR und lässt die LLVM-Toolchain übernehmen ;-)

Hier der Vollständigkeit halber ein Ausblick ...
:::



<!-- TODO
Abbildungen:

Prozessorarchitektur:
https://en.wikipedia.org/wiki/Microarchitecture#/media/File:Intel_i80286_arch.svg
https://commons.wikimedia.org/w/index.php?curid=5197867

Virtueller Speicher:
https://commons.wikimedia.org/wiki/File:Virtual_address_space_and_physical_address_space_relationship.svg
https://commons.wikimedia.org/wiki/File:Virtual_memory.svg

Abarbeitung OP-Codes:
https://commons.wikimedia.org/wiki/File:AbarbeitMaschinenBefehl8085.svg

Stack-Frames, Funktionsaufrufe:
https://de.wikipedia.org/wiki/Aufrufstapel
-->


## Prozessorarchitektur

![](https://upload.wikimedia.org/wikipedia/commons/thumb/2/27/Intel_i80286_arch.svg/1024px-Intel_i80286_arch.svg.png)

[Quelle: ["Intel i80286 arch"](https://commons.wikimedia.org/wiki/File:Intel_i80286_arch.svg) by [Appaloosa](https://commons.wikimedia.org/wiki/User:Appaloosa), licensed under [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0)]{.origin}

::: notes
Am Beispiel der noch übersichtlichen Struktur des Intel i80286 lassen sich verschiedene
Grundbausteine eines Prozessors identifizieren.

Zunächst hat man eine Ausführungseinheit (*Execution Unit*), die sich vor allem aus
verschiedenen Registern und der Recheneinheit (*ALU*) zusammen setzen. Hier kann man
Adressen berechnen oder eben auch Dinge wie Addition ...

Über die Register wird auch die Adressierung des Speichers vorgenommen. Aus Sicht eines
Prozesses greift dieser auf einen zusammenhängenden, linearen Speicher zu ("Virtueller
Speicher", siehe nächste Folie). Dieser setzt sich in der Realität aus verschiedenen
Segmenten zusammen, die auch auf unterschiedliche Speichertypen (RAM, Cache, SSD, ...)
verteilt sein können. In der *Address Unit* werden aus *logischen* Adressen die konkreten
*physikalischen* Adressen berechnet.

Über die *Bus Unit* erfolgt der physikalische Zugriff auf die konkrete Hardware.

In der *Instruction Unit* wird der nächste Befehl geholt und dekodiert und zur Ausführung
gebracht.
:::



## Virtueller Speicher

\bigskip

:::::: columns
::: {.column width="35%"}

![Virtueller Speicher](figs/pointer/VirtuellerSpeicher){height="70%"}\

:::
::: {.column width="60%"}

\vspace{10mm}

*   Kernel weist jedem Prozess seinen eigenen virtuellen Speicher zu
    *   Linearer Adressbereich, beginnend mit Adresse 0 bis zu einer
        maximalen Adresse

    \bigskip

*   Verwaltung durch MMU (*Memory Management Unit*)
    *   MMU bildet logische Adressen aus virtuellem Speicher auf den
        physikalischen Speicher ab
    *   Transparent für den Prozess

:::
::::::


## Segmente des virtuellen Speichers: Text

:::::: columns
::: {.column width="60%"}

\vspace{10mm}

*   **Text Segment** (read-only)
    *   Programm Code
    *   Konstanten, String Literale

\smallskip

*   Bereich initialisierter Daten
    *   globale und static Variablen (explizit initialisiert)

\smallskip

*   Bereich uninitialisierter Daten
    *   globale und static Variablen (uninitialisiert) \blueArrow Wert 0

:::::: notes
**ACHTUNG**: Bereich (un-) initialisierter Daten nicht in Abbildung dargestellt!
::::::

:::
::: {.column width="35%"}

![Virtueller Speicher](figs/pointer/VirtuellerSpeicher){height="70%"}\

:::
::::::


## Segmente des virtuellen Speichers: Stack

:::::: columns
::: {.column width="60%"}

\vspace{10mm}

*   Stackframe je Funktionsaufruf:
    *   Lokale Variablen ("automatische" Variablen)
    *   Argumente und Return-Werte

*   Dynamisch wachsend und schrumpfend

*   [Automatische]{.alert} Pflege

    ::: notes
    *   Nach Funktionsrückkehr wird der Stackpointer ("Top of Stack") weiter gesetzt
    *   Dadurch "Bereinigung": Speicher der lokalen Variablen wird freigegeben
    :::

:::
::: {.column width="35%"}

:::::: slides
![Virtueller Speicher](figs/pointer/VirtuellerSpeicher){height="70%"}\
::::::

:::
::::::


## Segmente des virtuellen Speichers: Data (Heap)

:::::: columns
::: {.column width="60%"}

\vspace{10mm}

*   Bereich für dynamischen Speicher (Allokation während der Laufzeit)

*   Dynamisch wachsend und schrumpfend

*   Zugriff und Verwaltung aus [laufendem]{.alert} Programm \newline
    \blueArrow\ **Pointer**

    ::: notes
    *   `malloc()`/`calloc()`/`free()` (C)
    *   `new`/`delete` (C++)
    *   typischerweise [**Pointer**]{.alert}
    :::

:::
::: {.column width="35%"}

:::::: slides
![Virtueller Speicher](figs/pointer/VirtuellerSpeicher){height="70%"}\
::::::

:::
::::::


## Befehlszyklus (von-Neumann-Architektur)

::: center
![Befehlszyklus](figs/vl10/instructioncycle){width="80%"}
[Quelle: [MichaelFrey (CC BY-SA 3.0)](https://de.wikipedia.org/wiki/Datei:AbarbeitMaschinenBefehl8085.svg)]{.origin}
:::

::: notes
Typischerweise hat man neben dem Stack und dem Heap noch diverse Register auf dem Prozessor,
die man wie (schnelle Hardware-) Variablen nutzen kann. Es gibt normalerweise einige spezielle
Register:

*   Program Counter (*PC*): Zeigt auf die Stelle im Textsegment, die gerade ausgeführt wird
*   Stack Pointer (*SP*): Zeigt auf den nächsten freien Stackeintrag
*   Frame Pointer (*LR*): Zeigt auf die Rücksprungadresse auf dem Stack (s.u.)
*   Akkumulator: Speichern von Rechenergebnissen

Der Prozessor holt sich die Maschinenbefehle, auf die der PC aktuell zeigt und dekodiert sie,
d.h. holt sich die Operanden, und führt die Anweisung aus. Danach wird der PC entsprechend
erhöht und der *Fetch-Decode-Execute*-Zyklus startet erneut. (OK, diese Darstellung ist stark
vereinfacht und lässt beispielsweise *Pipelining* außen vor.)

Ein Sprung bzw. Funktionsaufruf kann erreicht werden, in dem der *PC* auf die Startadresse der
Funktion gesetzt wird.

Je nach Architektur sind die Register, Adressen und Instruktionen 4 Bytes (32 Bit) oder 8 Bytes
(64 Bit) "breit".
:::


## Aufgaben bei der Erzeugung von Maschinen-Code

::: notes
Relativ ähnlich wie bei der Erzeugung von Bytecode, nur muss diesmal die Zielhardware
(Register, Maschinenbefehle, ...) beachtet werden:
:::

*   Übersetzen des Zwischencodes in Maschinenbefehle [für die jeweilige Zielhardware]{.notes}
*   Sammeln von Konstanten und Literalen am Ende vom Text-Segment

\smallskip

*   Auflösen von Adressen:
    *   Sprünge: relativ [(um wie viele Bytes soll gesprungen werden)]{.notes} oder
        absolut [(Adresse, zu der gesprungen werden soll)]{.notes}
    *   Strukturen (Arrays, Structs)[ haben ein Speicherlayout]{.notes}: Zugriff
        auf Elemente/Felder über Adresse [(muss berechnet werden)]{.notes}
    *   Zugriffe auf Konstanten oder Literalen: [Muss ersetzt werden durch]{.notes}
        Zugriff auf Text-Segment

\smallskip

*   Zuordnung der Variablen und Daten zu Registern oder Adressen
*   Aufruf von Funktionen: Anlegen der *Stack-Frames* [(auch *Activation Record* genannt)]{.notes}

\smallskip

*   Aufbau des Binärformats und Linking auf der Zielmaschine (auch Betriebssystem) beachten


## Übersetzen von Zwischencode in Maschinencode

::: notes
Für diese Aufgabe muss man den genauen Befehlssatz für den Zielprozessor kennen.
Im einfachsten Fall kann man jede Zeile im Zwischencode mit Hilfe von Tabellen
und Pattern Matching direkt in den passenden Maschinencode übertragen. Beispiel
vgl. Tabelle 7.1 in [@Mogensen2017, S.162].

Je nach Architektur sind die Register, Adressen und Instruktionen 4 Bytes (32 Bit)
oder 8 Bytes (64 Bit) "breit".

Da in einer Instruktion wie `ldr r0, x` die Adresse von `x` mit codiert werden
muss, hat man hier nur einen eingeschränkten Wertebereich. Üblicherweise ist dies
relativ zum *PC* zu betrachten, d.h. beispielsweise `ldr r0, #4[pc]` (4 Byte plus *PC*).
Dadurch kann man mit *PC-relativer Adressierung*  dennoch größere Adressbereiche
erreichen. Alternativ muss man mit indirekter Adressierung arbeiten und im Textsegment
die Adresse der Variablen im Datensegment ablegen: `ldr r0, ax`, wobei `ax` eine
mit *PC-relativer Adressierung* erreichbare Adresse im Textsegment ist, wo die
Adresse der Variablen `x` im Datensegment hinterlegt ist. Anschließend kann man
dann `x` laden: `ldr ro, [ro]`.

Ähnliches gilt für Konstanten: Wenn diese direkt geladen werden sollen, steht
quasi nur der "Rest" der Bytes vom Opcode zur Verfügung. Deshalb sammelt man die
Konstanten am Ende vom Text-Segment und ruft sie von dort ab.
:::

```
L:  ...
    ...
    if t3 < v goto L
```

\pause
\bigskip

```
1000: ...                 ;; L
      ...
1080: LD      R1, t3      ;; R1 = t3
1088: LD      R2, v       ;; R2 = v
1096: SUB     R1, R1, R2  ;; R1 = R1-R2
1104: BLTZ    R1, 1000    ;; if R1<0 jump to 1000 (L)
```

*Anmerkung*: `1000` ist die Adresse, die dem Label `L` entspricht.

[Quelle: nach [@Aho2008]]{.origin}


## Aufruf von Funktionen

![](images/ViewCaller.png){height="86%"}


## Sichern von lokalen Variablen beim Funktionsaufruf

- bei Funktionsaufrufen müssen verwendete Register (lokale Variablen) gesichert werden
- Register werden in den Speicher (Stack) ausgelagert
- Sicherung kann durch aufrufende Funktion (Caller-Saves) oder aufgerufene Funktion (Callee-Saves) erfolgen
- Vorteile:
  - Caller-Safes: nur "lebende" Register müssen gesichert werden
  - Callee-Safes: nur tatsächlich verwendete Register müssen gesichert werden
- Nachteile: unnötiges Sichern von Registern bei beiden Varianten möglich
- in der Praxis daher meist gemischter Ansatz aus Caller-Saves und Callee-Saves Registern


## Stack-Frame

::: center
![](https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/Aufrufstapellayout_nach_Freigabe.svg/512px-Aufrufstapellayout_nach_Freigabe.svg.png){height="86%"}
:::

[Quelle: [H3xc0d3r](https://commons.wikimedia.org/wiki/User:H3xc0d3r), [Aufrufstapellayout nach Freigabe](https://commons.wikimedia.org/wiki/File:Aufrufstapellayout_nach_Freigabe.svg), [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/legalcode)]{.origin}

::: notes
Ein Funktionsaufruf entspricht einem Sprung an die Stelle im
Textsegment, wo der Funktionscode abgelegt ist. Dies erreicht man, in
dem man diese Adresse in den *PC* schreibt. Bei einem `return` muss
man wieder zum ursprünglichen Programmcode zurückspringen, weshalb man
diese Adresse auf dem Stack hinterlegt.

Zusätzlich müssen Parameter für die Funktion auf dem Stack abgelegt
werden, damit die Funktion auf diese zugreifen kann. Im Funktionscode
greift man dann statt auf die Variablen auf die konkreten Adressen im
Stack-Frame zu. Dazu verwendet man den *Framepointer* bzw. *FP*
("Rahmenzeiger" in der Skizze), der auf die Adresse des ersten
Parameters, d.h. auf die Adresse *hinter* der Rücksprungadresse,
zeigt. Die Parameter sind dann je Funktion über konstante Offsets
relative zum *FP* erreichbar. Ähnlich wie die Rücksprungadresse muss
der *FP* des aufrufenden Kontexts im Stack-Frame der aufgerufenen
Funktion gesichert werden (in der Skizze nicht dargestellt) und am
Ende des Aufrufs wieder hergestellt werde.

Lokale Variablen einer Funktion werden ebenfalls auf dem Stack
abgelegt, falls nicht genügend Register zur Verfügung stehen, und
relativ zum *FP* adressiert. Die Größe des dafür verwendeten Speichers
wird oft als *Framesize* oder *Rahmengröße* bezeichnet. Am Ende eines
Funktionsaufrufs wird dieser Speicher freigegeben indem der
*Stackpointer* bzw. *SP* ("Stapelzeiger" in der Skizze) auf den *FP*
zurückgesetzt wird (oder auf *FP*+4 was den Speicher für die
Rücksprungadresse mit einschließt).

Die Adressierung von Parametern und Variablen kann auch relativ zum
*SP* erfolgen, so dass kein *FP* benötigt wird. Der dazu erzeugte
Maschinencode kann aber deutlich komplexer sein, da Stack und *SP*
auch für arithmetische Berechnungen verwendet werden und der *SP*
somit für die Dauer eines Funktionsaufrufs nicht zwingend konstant
ist. (Die nachfolgenden Code-Beispiele verwenden der Einfachheit
halber dennoch eine Adressierung relativ zum *SP*)

Falls eine Funktion Rückgabewerte hat, werden diese ebenfalls auf dem
Stack abgelegt (Überschreiben der ursprünglichen Parameter).

Zusammengefasst gibt es für jeden Funktionsaufruf die in der obigen
Skizze dargestellte Struktur ("Stack Frame" oder auch "Activation
Record" genannt):

*   Funktionsparameter (falls vorhanden)
*   Rücksprungadresse (d.h. aktueller *PC*)
*   Lokale Variablen der Funktion (falls vorhanden)
:::

## Funktionsaufruf: Prolog

:::::: columns
::: {.column width="40%"}
\vspace{2cm}
![](images/ViewCallee_Prolog.png){height="86%"}
:::
::: {.column width="40%"}
![](https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/Aufrufstapel_schema.svg/512px-Aufrufstapel_schema.svg.png){height="86%"}

[Quelle:  [H3xc0d3r](https://commons.wikimedia.org/wiki/User:H3xc0d3r), [Aufrufstapel schema](https://commons.wikimedia.org/wiki/File:Aufrufstapel_schema.svg), [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/legalcode)]{.origin}
:::
::::::

::: notes
Die Parameter einer Funktion werden vom aufrufenden Kontext auf dem Stack abgelegt und nachdem der Sprung in die Funktion erfolgt ist wieder in Variablen/Register geladen. Dieses Vorgehen ist in der Praxis aber ineffizient, da das Speichern und Laden direkt aufeinander folgen. Daher werden werden oft bestimmte (Caller-Saves) Register für die übergabe von Parametern verwendet.
:::

## Funktionsaufruf: Epilog

:::::: columns
::: {.column width="40%"}
\vspace{2cm}
![](images/ViewCallee_Epilog.png){height="86%"}
:::
::: {.column width="40%"}
![](https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Aufrufstapellayout_nach_R%C3%BCcksprung.svg/512px-Aufrufstapellayout_nach_R%C3%BCcksprung.svg.png){height="86%"}

[Quelle:  [H3xc0d3r](https://commons.wikimedia.org/wiki/User:H3xc0d3r), [Aufrufstapellayout nach Rücksprung](https://commons.wikimedia.org/wiki/File:Aufrufstapellayout_nach_R%C3%BCcksprung.svg), [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/legalcode)]{.origin}
:::
::::::

::: notes
Beim Rücksprung aus einer Funktion wird der Rückgabewert an die Stelle des ersten Parameters geschrieben und der restliche Stack freigegeben (lokale Variablen, Rücksprungadresse).
:::

## Freigabe des Rückgabewertes

::: center
![](https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/Aufrufstapellayout_nach_Freigabe.svg/512px-Aufrufstapellayout_nach_Freigabe.svg.png){height="86%"}
:::

[Quelle: [H3xc0d3r](https://commons.wikimedia.org/wiki/User:H3xc0d3r), [Aufrufstapellayout nach Freigabe](https://commons.wikimedia.org/wiki/File:Aufrufstapellayout_nach_Freigabe.svg), [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/legalcode)]{.origin}

::: notes
Nach Verarbeiten des Rückgabewertes wird auch dieser vom Stack entfernt (`pop`).
Damit ist der Stack-Frame des letzten Funktionsaufrufs komplett vom Stack entfernt.
:::

## Beispiel: Modell-gesteuerte Übersetzung

```{.python size="scriptsize"}
class CodeGenerator(LucyVisitor):
    def __init__(self):
        self.types = { 'Void': ir.VoidType(), 'Int':  ir.IntType(32), }
        self.module  = ir.Module()
        self.symbols = SymbolTable()

    def new_block(self):
        block = self.func.append_basic_block(name='.entry')
        self.builder = ir.IRBuilder(block)

    def new_func(self, typ, ide):
        self.func = ir.Function(self.module, typ, name=ide)
        self.symbols.bind(ide, self.func)

    def new_var(self, typ, ide, val=None):
        ptr = self.builder.alloca(typ, name=ide)
        self.symbols.bind(ide, ptr)
        if val is not None: self.builder.store(val, ptr)
    ...

if __name__ == '__main__':
    print CodeGenerator().visit(parser.program()).module
```

::: notes
In diesem Beispiel wird die manuelle Erzeugung von Maschinencode vermieden. Stattdessen nutzt der Autor ein durch das Python-Modul `llvmlite` bereitgestelltes Modell, welches bei der Traversierung des AST mit Informationen befüllt wird und welches anschließend die Ausgabe des Zielcodes übernimmt.
:::

[Quelle: nach Andrea Orru (BSD 2, [github.com/AndreaOrru/Lucy](https://github.com/AndreaOrru/Lucy), [`lucyc.py`](https://github.com/AndreaOrru/Lucy/blob/master/compiler/lucyc.py#L34))]{.origin}

## Wrap-Up

Skizze zur Erzeugung von Assembler-Code

*   Relativ ähnlich wie die Erzeugung von Bytecode
*   Beachtung der Eigenschaften der Zielhardware (Register, Maschinenbefehle, ...)
    *   Übersetzen des Zwischencodes in Maschinenbefehle
    *   Sammeln von Konstanten und Literalen am Ende vom Text-Segment
    *   Auflösen von Adressen
    *   Zuordnung der Variablen und Daten zu Registern oder Adressen
    *   Aufruf von Funktionen: Anlegen der *Stack-Frames*




<!-- DO NOT REMOVE - THIS IS A LAST SLIDE TO INDICATE THE LICENSE AND POSSIBLE EXCEPTIONS (IMAGES, ...). -->
::: slides
## LICENSE
![](https://licensebuttons.net/l/by-sa/4.0/88x31.png)

Unless otherwise noted, this work is licensed under CC BY-SA 4.0.

### Exceptions
*   Figure ["A Map of the Territory (mountain.png)"](https://github.com/munificent/craftinginterpreters/blob/master/site/image/a-map-of-the-territory/mountain.png)
    (https://github.com/munificent/craftinginterpreters/blob/master/site/image/a-map-of-the-territory/mountain.png),
    by [Bob Nystrom](https://github.com/munificent), licensed under [MIT](https://github.com/munificent/craftinginterpreters/blob/master/LICENSE)
*   Figure ["Intel i80286 arch"](https://commons.wikimedia.org/wiki/File:Intel_i80286_arch.svg)
    (https://commons.wikimedia.org/wiki/File:Intel_i80286_arch.svg), by [Appaloosa](https://commons.wikimedia.org/wiki/User:Appaloosa), licensed
    under [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0)
:::
