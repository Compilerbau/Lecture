DOCKER = docker run -i --rm -u "$(shell id -u):$(shell id -g)" -v "$(shell pwd):/work" -w "/work"
IMAGE  = openjdk/antlr4

JAVA   = $(DOCKER) --entrypoint "java"  $(IMAGE)
JAVAC  = $(DOCKER) --entrypoint "javac" $(IMAGE)

CP    = -cp .:/opt/antlr4/antlr-4.9.2-complete.jar
ANTLR = $(JAVA) $(CP) org.antlr.v4.Tool
GRUN  = $(JAVA) $(CP) org.antlr.v4.runtime.misc.TestRig


.PHONY: all
all: SimpleClassDef.g4
	$(ANTLR) SimpleClassDef.g4
	$(JAVAC) $(CP) *.java
	$(GRUN) SimpleClassDef prog -tokens


.PHONY: clean
clean:
	rm -rf SimpleClassDef.interp SimpleClassDef.tokens SimpleClassDefBase* SimpleClassDefLexer* SimpleClassDefListener* SimpleClassDefParser* *.class
