---
type: lecture-cg
title: "Grenze Lexer und Parser"
author: "Carsten Gips (FH Bielefeld)"
weight: 9
readings:
  - key: "Parr2014"
assignments:
  - topic: sheet01
youtube:
  - id: TODO
fhmedia:
  - link: "TODO"
    name: "Direktlink FH-Medienportal: CB Grenze Lexer und Parser"
---


## Grenze Lexer und Parser (Faustregeln)

::: notes
Der Lexer verwendet einfache reguläre Ausdrücke, während der Parser
mit Lookaheads unterschiedlicher Größe, Backtracking und umfangreicher
Error-Recovery arbeitet. Entsprechend sollte man alle Arbeit, die
man bereits im Lexer erledigen kann, auch dort erledigen. Oder
andersherum: Man sollte dem Parser nicht unnötige Arbeit aufbürden.

=> Erreiche in jeder Verarbeitungsstufe die maximal mögliche Abstraktionsstufe!
:::


1.  Matche und verwerfe im Lexer alles, was der Parser nicht braucht.

    ::: notes
    Wenn bestimmte Dinge später nicht gebraucht werden, sollten sie bereits
    im Lexer erkannt und aussortiert werden. Der Lexer arbeitet deutlich
    einfacher und schneller als der Parser ... Und je weniger Token der
    Parser betrachten muss, um so einfacher und schneller kann er werden.
    :::

\bigskip

2.  Matche gebräuchliche Token [(Namen, Schlüsselwörter, Strings, Zahlen)]{.notes} im Lexer.

    ::: notes
    Der Lexer hat deutlich weniger Overhead als der Parser. Es lohnt sich deshalb,
    beispielsweise Ziffern bereits im Lexer zu Zahlen zusammenzusetzen und dem
    Parser als entsprechendes Token zu präsentieren.
    :::

\bigskip

3.  Quetsche alle lexikalischen Strukturen, die der Parser nicht unterscheiden muss, in einen Token-Typ.

    ::: notes
    Wenn der Parser bestimmte Strukturen nicht unterscheiden muss, dann macht es
    wenig Sinn, dennoch unterschiedliche Token an den Parser zu senden.

    Beispiel:
    Wenn eine Anwendung nicht zwischen Integer- und Gleitkommazahlen unterscheidet,
    sollte der Lexer dafür nur einen Token-Typ erzeugen und an den Parser senden
    (etwa `NUMBER`).

    Beispiel:
    Wenn der Parser nicht den Inhalt eines XML-Tags "verstehen" muss, dann kann man
    diesen in ein einzelnes Token packen.
    :::

\bigskip

4.  Wenn der Parser Texteinheiten unterscheiden muss, erzeuge dafür eigene Token-Typen im Lexer.

    ::: notes
    Wenn der Parser etwa Elemente einer IP-Adresse verarbeiten muss, sollte
    der Lexer passende Token für die Teile der IP-Adresse erzeugen und an den
    Parser schicken.
    :::


## Diskussion: Parsen von Logfiles eines Webservers

::: notes
Typischer Aufbau einer Zeile im Logfile eines Webservers:
:::

``` {size="footnotesize"}
192.168.0.10 "GET /wuppie.html HTTP/1.0" 200
```

\pause
\bigskip

*   Zählen der Zeilen

    ::: notes
    Wenn es nur um das Zählen der Zeilen geht, muss der Parser nicht den
    Aufbau der Zeilen oder sogar der IP-Adressen verstehen. Es reichen
    einfache Lexer-Regeln, die quasi die Zeilenumbrüche repräsentieren.
    Der Rest wird per `skip` einfach entfernt ...
    :::

    ``` {.yacc size="scriptsize"}
    file : ROW+;
    ROW  : '\n';
    STUFF: ~'\n' -> skip ;
    ```

\pause

*   Liste aller IP-Adressen

    ::: notes
    Wenn man nun eine Liste aller IP-Adressen erzeugen will, wäre es ausreichend,
    die Struktur einer Zeile (und damit die IP-Adressen) mit Lexer-Regeln
    (und -Fragmenten) zu erkennen.
    :::

    ``` {.yacc size="scriptsize"}
    file: row+;
    row : IP REST;
    IP  : INT '.' INT '.' INT '.' INT ;
    ```

\pause

*   Bearbeiten der IP-Adressen im Parser (Aktionen)

    ::: notes
    Wenn man zusätzlich die IP-Adressen noch im Parser bearbeiten will
    (etwa durch eingebettete Aktionen), dann muss die Regel zum Erkennen
    der Adressen entsprechend eine Parser-Regel sein:
    :::

    ``` {.yacc size="scriptsize"}
    file: row+;
    row : ip REST;
    ip  : INT '.' INT '.' INT '.' INT ;
    ```

[[Quelle: nach [@Parr2014, S. 82]]{.origin}]{.notes}
**TODO Code**


## Wrap-Up

*   Grenze zw. Lexer und Parser ist gleitend
*   Ziel: möglichst hohe Abstraktion auf jeder Ebene erreichen





<!-- DO NOT REMOVE - THIS IS A LAST SLIDE TO INDICATE THE LICENSE AND POSSIBLE EXCEPTIONS (IMAGES, ...). -->
::: slides
## LICENSE
![](https://licensebuttons.net/l/by-sa/4.0/88x31.png)

Unless otherwise noted, this work is licensed under CC BY-SA 4.0.

### Exceptions
*   TODO (what, where, license)
:::
