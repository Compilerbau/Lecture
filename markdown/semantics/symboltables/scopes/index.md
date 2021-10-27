---
type: lecture-cg
title: "Nested Scopes"
author: "Carsten Gips (FH Bielefeld)"
weight: 2
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

![Nested Scopes](images/nestedscopes.png)

![Nested Scopes](images/nestedscopesuml.png)

```python
class Scope:
    Scope enclosingScope # None if global (outermost) scope
    Symbol<String, Symbol> symbols
    
    def resolve(name):
        # do we know "name" here?
        s = symbols[name]
        if (s != None) return s
        # if not here, check any enclosing scope
        if (enclosingScope != None) return
        enclosingScope.resolve(name)
        return None # not found
    
	def bind(symbol):
   	    symbols[symbol.name] = symbol
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
