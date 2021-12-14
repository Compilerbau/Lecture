---
type: lecture-cg
title: "Garbage Collection"
menuTitle: "Garbage Collection"
author: "Carsten Gips (FH Bielefeld)"
weight: 5
readings:
  - key: "Aho2008"
    comment: "Kapitel XYZ"
  - key: "Nystrom2021"
    comment: "Kapitel Garbage Collection"
youtube:
  - id: TODO
fhmedia:
  - link: https://www.fh-bielefeld.de/medienportal/m/27b2648014a692bd4aacbea6cb0b3787a045bc43a0ab2f0b028158c84ec7bcb27ea654d3955836d86bad5bd9e0ee3cd48f6c9ca7ce766a6e744971e0224b4494
    name: "Direktlink FH-Medienportal: CB Garbage Collection"
---

## Ist das Code oder kann das weg?

:::::: columns
::: {.column width="45%"}
```python
var a = "first value";
a = "updated";
print a;

a = 'first value'
a = 'updated'
print(a)
```
:::
::: {.column width="45%"}
```python
fun makeClosure() {
    var a = "data";

    fun f() { print a; }
    return f;
}

var closure = makeClosure();
closure();

def makeClosure():
    a = 'data'    
    def f():
        print(a)
    return f

closure = makeClosure()
# GC here
closure()
```
:::
::::::

[Quelle nach: [@Nystrom2021], Kapitel "Garbage Collection"]{.origin}


## Erreichbarkeit

![Erreichbarkeit von Objekten](images/reachable.png)

[Quelle: [@Nystrom2021], [`reachable.png`](https://github.com/munificent/craftinginterpreters/blob/master/site/image/garbage-collection/reachable.png), ([MIT](https://github.com/munificent/craftinginterpreters/blob/master/LICENSE))]{.origin}

::: notes
Erreichbar sind zunächst alle "Wurzeln", d.h. alle Objekte, die direkt über den
Stack oder die Konstanten-Arrays oder die Hashtabelle mit den globalen Variablen
(und Funktionen) erreichbar sind.

Alle Objekte, die von erreichbaren Objekten aus erreichbar sind, sind ebenfalls
erreichbar.

"Objekt" meint dabei im Zuge der Bytecodegenerierung oder während der Bearbeitung
durch die VM erstellte Werte/Objekte, die auf dem Heap alloziert wurden und durch
die VM aktiv freigegeben werden müssen.
:::


## Mark-Sweep Garbage Collection

::: notes
Das führt zu einem zweistufigen Algorithmus:

1.  **Mark**: Starte mit den Wurzeln und traversiere so lange durch die Objektreferenzen,
    bis alle erreichbaren Objekte besucht wurden.
2.  **Sweep**: Lösche alle anderen Objekte.
:::

![Erreichbarkeit von Objekten](images/mark-sweep.png)

[Quelle: [@Nystrom2021], [`mark-sweep.png`](https://github.com/munificent/craftinginterpreters/blob/master/site/image/garbage-collection/mark-sweep.png), ([MIT](https://github.com/munificent/craftinginterpreters/blob/master/LICENSE))]{.origin}


## Phase "Mark": Wurzeln markieren

:::::: columns
::: {.column width="45%"}

```python
typedef struct sObj Obj;
struct sObj {
    ObjType type;
    bool isMarked;
    struct sObj* next;
};

class Obj():								
    def __init__(self, type, next):			# ObjType type, Obj next
        self.type = type
        self.next = next
        self.isMarked = false				# bool isMarked
```
[Quelle nach: [@Nystrom2018], [`object.h`](https://github.com/munificent/craftinginterpreters/blob/master/c/object.h#L91), Kapitel "Garbage Collection"]{.origin}

:::
::: {.column width="45%"}

```python
typedef struct {
    Chunk* chunk;
    ...
    Obj* objects;
    ...
    int grayCount;
    int grayCapacity;
    Obj** grayStack;
} VM;

class VM():
    def __init__(self):
        self.chunk = None
        self.objects = None
        self.grayCount = 0
        self.grayCapacity = 0
        self.grayStack = None
       
```
[Quelle nach: [@Nystrom2018], [`vm.h`](https://github.com/munificent/craftinginterpreters/blob/master/c/vm.h#L41), Kapitel "Garbage Collection"]{.origin}

:::
::::::

\bigskip
\bigskip

```c
object->isMarked = true;
vm.grayStack[vm.grayCount++] = object;

obj.isMarked = true
vm.grayStack[vm.grayCount++] = object
```

[Quelle nach: [@Nystrom2021], Kapitel "Garbage Collection"]{.origin}

::: notes
Die Strukturen für Objekte und die VM werden ergänzt. Objekte erhalten noch
ein Flag für die Markierung sowie einen `next`-Pointer, mit dem alle Objekte
in einer verketteten Liste gehalten werden können. Die VM erhält einen Stack
für die Verwaltung markierter Objekte.

Zum Auffinden der erreichbaren Objekte wird mit einem Färbungsalgorithmus
gearbeitet. Initial sind alle Objekte "weiß" (nicht markiert).

Im ersten Schritt färbt man alle "Wurzeln" "grau" ein. Dabei werden alle
Objektreferenzen im Stack der VM, in der Hashtabelle für globale Variablen
der VM, in der Konstantentabelle des Bytecode-Chunks sowie in den Funktionspointern
betrachtet: Über diese Datenstrukturen wird iteriert und alle auf dem Heap
der Laufzeitumgebung allozierten Strukturen/Objekte werden markiert, indem
ihr Flag gesetzt wird. Zusätzlich werden die Pointer auf diese Objekte in
den `grayStack` hinzugefügt. Damit sind alle Wurzeln "grau" markiert".
:::


## Phase "Mark": Trace

```c
void traceReferences() {
    while (vm.grayCount > 0) {
        Obj* object = vm.grayStack[--vm.grayCount];
        blackenObject(object);
    }
}

def traceReferences():
    while vm.grayCount > 0:
        object = vm.grayStack[--vm.grayCount]
        blackenObject(object)
```

[Quelle nach: [@Nystrom2018], [`memory.c`](https://github.com/munificent/craftinginterpreters/blob/master/c/memory.c#L264), Kapitel "Garbage Collection"]{.origin}

::: notes
Nachdem alle Wurzeln "grau" markiert wurden und auf den `grayStack` der VM
gelegt wurden, müssen nun mögliche Verweise in den Wurzeln verfolgt werden.
Dazu entfernt man schrittweise die Objekte vom Stack und betrachtet sie damit
als "schwarz". (Das Markierungs-Flag bleibt gesetzt, "schwarz" sind die Objekte,
weil sie nicht mehr auf dem `grayStack` der VM liegen.) Sofern das aktuell
betrachtete Objekt seinerseits wieder Referenzen hat (beispielsweise haben
Funktionen wieder einen Bytecode-Chunk mit einem Konstanten-Array), werden
diese Referenz iteriert und alle dabei aufgefundenen Objekte auf den `grayStack`
der VM gelegt.

Dieser Prozess wird so lange durchgeführt, bis der Stack leer ist. Dann sind
alle erreichbaren Objekte markiert.
:::


## Phase "Sweep"

```c
void sweep() {
    Obj* previous = NULL;  Obj* object = vm.objects;
    while (object != NULL) {
        if (object->isMarked) {
            object->isMarked = false;
            previous = object;  object = object->next;
        } else {
            Obj* unreached = object;
            object = object->next;
            if (previous != NULL) { previous->next = object; }
            else { vm.objects = object; }
            freeObject(unreached);
        }
    }
}

def sweep():
    previous = NULL
    object = vm.objects
    while object != NULL:
        if object.isMarked:
            object.isMarked = false
            previous = object
            object = object.next
        else:
            unreached = object
            object = object.next
            if previous != NULL:
                previous.next = object
            else:
                vm.objects = object
            freeObject(unreached)
```

[Quelle nach: [@Nystrom2018], [`memory.c`](https://github.com/munificent/craftinginterpreters/blob/master/c/memory.c#L272), Kapitel "Garbage Collection"]{.origin}

::: notes
Wann immer für ein Objekt Speicher auf dem Laufzeit-Heap angefordert wird,
wird dieses Objekt in eine verkettete Liste aller Objekte der VM eingehängt
(Feld `vm.objects`). Über diese Liste wird nun iteriert und alle "weissen"
(nicht markierten) Objekte werden ausgehängt und freigegeben.

Zusätzlich müssen alle verbleibenden Objekte für den nächsten GC-Lauf
wieder entfärbt werden, d.h. die Markierung muss wieder zurückgesetzt
werden.
:::


## Metriken: Latenz und Durchsatz

::: notes
*   **Latenz**: Längste Zeitdauer, während der das eigentliche Programm (des Users)
    pausiert, beispielsweise weil gerade eine Garbage Collection läuft

*   **Durchsatz**: Verhältnis aus Zeit für den User-Code zu Zeit für Garbage Collection

    Beispiel: Ein Durchsatz von 90% bedeutet, dass 90% der Rechenzeit für den User
    zur Verfügung steht und 10% für GC verwendet werden
:::

::: center
![Erreichbarkeit von Objekten](images/latency-throughput.png)

[Quelle: [@Nystrom2021], [`latency-throughput.png`](https://github.com/munificent/craftinginterpreters/blob/master/site/image/garbage-collection/latency-throughput.png), [MIT](https://github.com/munificent/craftinginterpreters/blob/master/LICENSE)]{.origin}
:::


## Self-adjusting Heap

*   GC selten: Hohe Latenz
*   GC oft: Geringer Durchsatz

\bigskip

**Heuristik**

*   Beobachte den allozierten Speicher der VM
*   Wenn [vorher festgelegte (willkürliche)]{.notes} Grenze überschritten: GC
*   Größe des verbliebenen Speichers mal Faktor \blueArrow neue Grenze

::: notes
Hier spielt die *Nursery*-Theorie mit hinein: Die meisten Objekte haben eher eine
kurze Lebensdauer. Wenn sie aber ein gewisses "Alter" erreicht haben, werden sie
oft noch weiterhin benötigt.

D.h. die Objekte bzw. der Speicherverbrauch, der nach einem GC-Lauf übrig bleibt,
ist ein Indikator für den aktuell nötigen. Deshalb setzt man die neue Schwelle, ab
der der nächste GC-Lauf gestartet wird, etwas auf diesen Speicherverbrauch mal
einem gewissen Faktor (beispielsweise den Wert 2), um nicht sofort wieder einen
GC zu starten ...
:::

[[Hinweis: *Nursery*-Theorie]{.bsp}]{.slides}

::: notes
**Anmerkung**: Man unterscheidet zusätzlich noch zwischen *konservativem*
und *präzisem* GC:

*   *Konservatives* GC geht eher vorsichtig vor: Wenn ein Speicherbereich
    möglicherweise noch benötigt werden *könnte*, wird er nicht angefasst;
    alles, was auch nur so aussieht wie ein Pointer wird entsprechend behandelt.
*   *Präzises* GC "weiss" dagegen genau, welche Werte Pointer sind und welche
    nicht und handelt entsprechend.

Das obige Beispiel aus [@Nystrom2021] ist ein Beispiel für präzises GC.
:::

## Konservative Garbage Collection

* Von Boehm und Weiser
* Kann Pointer im Speicher finden ohne die innere Struktur eines Objektes zu kennen
  * Interpretiert alle Daten in dem Speicherbereich, in dem Pointer gesucht werden, als Pointer
  * Die Daten werden unsichere Pointer genannt, da der Collector nicht weiß, ob sie Pointer sind.
  * Prozessorregister, den Stack und alle statischen Daten werden so untersucht, um den Root zu finden.

    

## Allocation

* 4 KB große Blöcke
  * Aufgeteilt in Objektbereich und Verwaltungsbereich.
  * Objektbereich
    * Objekt
  * Verwaltungsbereich 
    * die Größe des gespeicherten Objektes
    * Anzahl an gespeicherten Objekten
    * Bit-Feld für die Mark-Phase
* Der Mutator schaut, beim anfordern von Speicherplatz, in der Blockliste und im Verwaltungsbereich des entsprechendem Blockes, ob es ein freies Objekt mit der Größe gibt
  * Wenn ja, wird die Objektanzahl des Blockes inkrementiert und ein Pointer auf dieses Objekt zurückgegeben
  * Wenn nein, wird ein neuer Block initialisiert und ein Pointer auf das erste Objekt zurückgegeben



## Collection

* Inspiziert die Prozessorregister, den Stack und die statischen Objekte
* Alle Daten werden als Adressen interpretiert und müssen getestet werden, ob sie gültige Pointer sind
* Es sind keine gültigen Pointer,
  * Wenn die Adresse höher oder niedriger als die höchste oder niedrigste Adresse im Heap ist
  * Wenn die Adresse *nicht* innerhalb eines Blockes liegt also die Adresse nicht in der Blockliste vorkommt
  * Wenn die Adresse in die Mitte und nicht am Anfangen eines Objektes zeigt
  * Wenn die Adresse auf ein Objekt in der Free-List zeigt
* Die Objekte auf den ein gültiger Pointer zeigt kann dadurch als erreichbar markiert werden
* Diese Objekte werden als Root-Objekte inspiziert, also alle Daten werden als Pointer interpretiert, getestet und ggf. die Objekte auf die Pointer zeigen, markiert
* Die Collection endet, wenn die Objekte inpiziert wurden und alle nicht markierten Objekte in die Free-Liste aufgenommen wurden

 

## Blacklisting

* Soll Fehlinterpretationen von unsicherer Pointer verringern
* Mark-Phase
  * In die Black-List wird jeder unsichere Pointer eingetragen, der sich nach den Test als ungültig erweist
* Allocation
  * Es kann kein Objekt an eine Adresse alloziert werden, wenn die Adresse in der Black-List steht

Das ganze wird umgesetzt, indem eine Seite des Heaps nicht verwendet wird, wenn ein Pointer in der Black-List in diese Seite zeigt. Durch die virtuelle Speicherverwaltung wird der Speicher erst zur Verfügung gestellt, wenn auf die Seite zugegriffen wird. Dadurch wird auch kein Speicher verschenkt. Der Aufwand für das Blacklisting liegt bei unter 1% der für Allocation und Collection verbrauchten Laufzeit.



## Vor- und Nachteile der Konservative Garbage Collection 

Vorteile:

* Keine explizite Kooperation des Mutators nötig
* Der Mutator muss nur eine Bedingung erfüllen
  * Jedes benutze Objekt hat ein Pointer auf den Anfang (innerhalb des Zugriffsbereichs des Collectors)
* Kann mit anderen Speicherverwaltungen koexistieren
* Explizite Deallocation ist möglich
* Kann jederzeit abgebrochen werden
  * Praktisch in Verbindung mit opportunistischer Garbage Collection in interaktiven Applikationen



Nachteile:

* Mark-Phase dauert durch die zusätzlichen Tests länger
* Die Möglichkeit einer Fragmentierung des Speichers ist hoch.
  * In manchen Situationen kann etwa die hälfte des Heaps nicht genutzt werden, da die freien Objekte nicht die vom Mutator benötigte Größe hatten
* Fehlinterpretationen können dafür sorgen, dass unsichere Pointer nicht freigegeben werden
* Bei hoch optimierten Compilern ist der Collector nicht zuverlässig, da die Adressen nicht mehr auf die benutzen Objekte zeigt




## Alternativen

*   Reference Counting
*   Inkrementelles GC
*   Concurrent GC
*   Generational GC: Markieren der "Generationen" der Lebensdauer, Umsortieren
    "erwachsener" Objekte in Speicherbereich mit weniger häufigem GC

## Wrap-Up

*   Pflege verkette Liste alle Objekte in der VM

\smallskip

*   Mark-Sweep-GC:
    1.  Markiere alle Wurzeln ("grau", aus Stack und Hashtabelle)
    2.  Traversiere ausgehend von den Wurzeln alle Objekte und markiere sie
    3.  Gehe die verkettete Liste aller Objekte durch und entferne alle nicht markierten

\smallskip

*   Problem: Latenz und Durchsatz, Idee des "self-adjusting" Heaps


<!-- DO NOT REMOVE - THIS IS A LAST SLIDE TO INDICATE THE LICENSE AND POSSIBLE EXCEPTIONS (IMAGES, ...). -->
::: slides
## LICENSE
![](https://licensebuttons.net/l/by-sa/4.0/88x31.png)

Unless otherwise noted, this work is licensed under CC BY-SA 4.0.

### Exceptions
*   TODO (what, where, license)
:::
