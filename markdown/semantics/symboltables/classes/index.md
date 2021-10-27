---
type: lecture-cg
title: "Strukturen und Klassen"
author: "Carsten Gips (FH Bielefeld)"
weight: 4
readings:
  - key: "Mogensen2017"
    comment: "Kapitel 3"
  - key: "Parr2014"
    comment: "Kapitel 6.4 und 8.4"
  - key: "Parr2010"
    comment: "Kapitel 6, 7 und 8"
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

## Inhalt

![Struct-Scopes](images/structscopes.png)

![Struct-Scopes (Klassendiagramm)](images/structscopesuml.png)

```python
def bind(symbol):
    symbols[symbol.name] = symbol
    symbol.scope = self # track the scope in each symbol
```

```python
class Struct(Scope, Symbol, Type): 
    def resolveMember(name):
        return symbols[name]
```

![Klassen-Scopes](images/classscopes.png)

![Klassen-Scopes (Klassendiagramm)](images/classscopesuml.png)

``` python
class Clazz(Struct):
  	Clazz parent # None if base class

    def resolve(name):
        # do we know "name" here?
        s = symbols[name]
        if (s != None) return s
        # NEW: if not here, check any parent class ...
        if (parent != None) return parent.resolve(name)
        # ... or enclosing scope if base class
        if (enclosingScope != None) return enclosingScope.resolve(name)
        return None # not found
    
    def resolveMember(name):
        s = symbols[name]
        if (s != None) return s
        # NEW: check parent class
        if (parent != None) return parent.resolveMember(name)
        return None
```

## Motivation

Lorem Ipsum. Starte mit H2-Level.
...

**TODO** 



<!-- DO NOT REMOVE - THIS IS A LAST SLIDE TO INDICATE THE LICENSE AND POSSIBLE EXCEPTIONS (IMAGES, ...). -->
::: slides
## LICENSE
![](https://licensebuttons.net/l/by-sa/4.0/88x31.png)

Unless otherwise noted, this work is licensed under CC BY-SA 4.0.

### Exceptions
*   TODO (what, where, license)
:::
