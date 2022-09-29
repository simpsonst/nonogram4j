all::

FIND ?= find
SED ?= sed
XARGS ?= xargs

PREFIX=/usr/local
JAVASHAREDIR=$(PREFIX)/share/java

INSTALL=install
FIND=find
PRINTF=printf

VWORDS:=$(shell src/getversion.sh --prefix=v MAJOR MINOR PATCH)
VERSION:=$(word 1,$(VWORDS))
BUILD:=$(word 2,$(VWORDS))

## Provide a version of $(abspath) that can cope with spaces in the
## current directory.
myblank:=
myspace:=$(myblank) $(myblank)
MYCURDIR:=$(subst $(myspace),\$(myspace),$(CURDIR)/)
MYABSPATH=$(foreach f,$1,$(if $(patsubst /%,,$f),$(MYCURDIR)$f,$f))

-include $(call MYABSPATH,config.mk)
-include nonogram4j-env.mk

jars += $(SELECTED_JARS)



SELECTED_JARS += nonogram4j_clue
trees_nonogram4j_clue += clue

SELECTED_JARS += nonogram4j_util
trees_nonogram4j_util += util

SELECTED_JARS += nonogram4j_layout
trees_nonogram4j_layout += layout

SELECTED_JARS += nonogram4j_line
trees_nonogram4j_line += line

SELECTED_JARS += nonogram4j_geom
trees_nonogram4j_geom += geom

SELECTED_JARS += nonogram4j_heuristic
trees_nonogram4j_heuristic += heuristic

SELECTED_JARS += nonogram4j_fast
trees_nonogram4j_fast += fast

SELECTED_JARS += nonogram4j_rect
trees_nonogram4j_rect += rect

jars += tests

SELECTED_JARS += nonogram4j_fcomp
trees_nonogram4j_fcomp += fcomp

SELECTED_JARS += nonogram4j_lib
trees_nonogram4j_lib += lib

SELECTED_JARS += nonogram4j_aspect
trees_nonogram4j_aspect += aspect

SELECTED_JARS += nonogram4j_solver
trees_nonogram4j_solver += solver


test_suite += uk.ac.lancs.nonogram.TestUtils
test_suite += uk.ac.lancs.nonogram.line.fast.TestLine

roots_clue=$(found_clue)

roots_layout=$(found_layout)
deps_layout += clue

roots_lib=$(found_lib)

roots_line=$(found_line)
deps_line += clue
deps_line += lib

roots_aspect=$(found_aspect)
statics_aspect += uk/ac/lancs/nonogram/aspect/CharacterEntities.properties
deps_aspect += clue
deps_aspect += layout

roots_fast=$(found_fast)
deps_fast += line
deps_fast += util
deps_fast += clue
deps_fast += lib
deps_fast += heuristic

roots_fcomp=$(found_fcomp)
deps_fcomp += line
deps_fcomp += util
deps_fcomp += lib
deps_fcomp += heuristic

roots_geom=$(found_geom)
deps_geom += layout
deps_geom += lib
deps_geom += aspect

roots_heuristic=$(found_heuristic)
deps_heuristic += clue
deps_heuristic += lib

roots_rect=$(found_rect)
deps_rect += clue
deps_rect += layout
deps_rect += lib
deps_rect += aspect
deps_rect += geom

roots_solver=$(found_solver)
deps_solver += clue
deps_solver += layout
deps_solver += lib
deps_solver += aspect
deps_solver += line
deps_solver += heuristic
deps_solver += geom

roots_tests=$(found_tests)
deps_tests += lib
deps_tests += line
deps_tests += util
ppdeps_tests += fast

roots_util=$(found_util)




version_nonogram4j_util=$(VERSION)
version_nonogram4j_line=$(VERSION)
version_nonogram4j_geom=$(VERSION)
version_nonogram4j_heuristic=$(VERSION)
version_nonogram4j_fast=$(VERSION)
version_nonogram4j_rect=$(VERSION)


JARDEPS_SRCDIR=src/tree
JARDEPS_DEPDIR=src
JARDEPS_MERGEDIR=src/merge

include jardeps.mk
-include jardeps-install.mk

DOC_PKGS += uk.ac.lancs.nonogram.solver
DOC_PKGS += uk.ac.lancs.nonogram.solver.swing
DOC_PKGS += uk.ac.lancs.nonogram.line
DOC_PKGS += uk.ac.lancs.nonogram.util
DOC_PKGS += uk.ac.lancs.nonogram.plugin
DOC_PKGS += uk.ac.lancs.nonogram
DOC_PKGS += uk.ac.lancs.nonogram.aspect
DOC_PKGS += uk.ac.lancs.nonogram.line.heuristic
DOC_PKGS += uk.ac.lancs.nonogram.line.fast
DOC_PKGS += uk.ac.lancs.nonogram.line.comprehensive
DOC_PKGS += uk.ac.lancs.nonogram.geom
DOC_PKGS += uk.ac.lancs.nonogram.geom.rect

DOC_OVERVIEW=src/overview.html
DOC_CLASSPATH += $(jars:%=$(JARDEPS_OUTDIR)/%.jar)
DOC_SRC=$(call jardeps_srcdirs4jars,$(SELECTED_JARS))
DOC_CORE=nonogram4j$(DOC_CORE_SFX)


jtests: $(jars:%=$(JARDEPS_OUTDIR)/%.jar)
	@for class in $(test_suite) ; do \
	  $(PRINTF) 'Testing %s\n' "$$class"; \
	  $(JAVA) -ea -cp $(subst $(jardeps_space),:,$(jars:%=$(JARDEPS_OUTDIR)/%.jar):$(CLASSPATH)) \
	  junit.textui.TestRunner $${class} ; \
	done


testwidget: all
	$(JAVA) -cp $(subst $(jardeps_space),:,$(jars:%=out/%.jar)) \
	  uk.ac.lancs.nonogram.geom.rect.RectangularDisplay

all:: VERSION BUILD installed-jars
installed-jars:: $(SELECTED_JARS:%=out/%.jar)
installed-jars:: $(SELECTED_JARS:%=out/%-src.zip)


install-jar-%::
	@$(call JARDEPS_INSTALL,$(PREFIX)/share/java,$*,$(version_$*))

install-jars:: $(SELECTED_JARS:%=install-jar-%)

install:: install-jars

tidy::
	@$(PRINTF) 'Deleting trash\n'
	@$(FIND) . -name "*~" -delete

clean:: tidy

distclean:: blank
	$(RM) VERSION BUILD

MYCMPCP=$(CMP) -s '$1' '$2' || $(CP) '$1' '$2'
.PHONY: prepare-version
mktmp:
	@$(MKDIR) tmp/
prepare-version: mktmp
	$(file >tmp/BUILD,$(BUILD))
	$(file >tmp/VERSION,$(VERSION))
BUILD: prepare-version
	@$(call MYCMPCP,tmp/BUILD,$@)
VERSION: prepare-version
	@$(call MYCMPCP,tmp/VERSION,$@)



# Set this to the comma-separated list of years that should appear in
# the licence.  Do not use characters other than [-0-9,] - no spaces.
YEARS=2011,2022

update-licence:
	$(FIND) . -name ".svn" -prune -or -type f -print0 | $(XARGS) -0 \
	$(SED) -i 's/Copyright (c)\s[-0-9,]\+\sLancaster University/Copyright (c) $(YEARS), Lancaster University/g'
