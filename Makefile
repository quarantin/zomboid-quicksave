NAME           := $(shell grep ^id mod.info | cut -f2 -d=)
VERSION        := $(shell git tag -l | tail -n 1)
BUNDLE         := dist
BUNDLEDIR      := $(BUNDLE)/$(NAME)
STEAMBUNDLE    := ~/Zomboid/Workshop/$(NAME)
STEAMBUNDLEDIR := $(STEAMBUNDLE)/Contents/mods
JAR            := src/$(NAME).jar

all: steambundle

build:
	$(MAKE) -C src build

bundle: build
	rm -rf $(BUNDLE) && mkdir -p $(BUNDLEDIR) && \
	cp -r mod.info media $(JAR) $(BUNDLEDIR)  && \
	cd $(BUNDLE) && zip -r $(NAME).zip $(NAME)

steambundle: bundle
	rm -rf $(STEAMBUNDLE) && mkdir -p $(STEAMBUNDLEDIR) && \
	cp workshop.txt preview.png $(STEAMBUNDLE)          && \
	cp -r $(BUNDLEDIR) $(STEAMBUNDLEDIR)                && \
	sed -i                                                 \
		-e "s/^version=.*$$/version=$(VERSION)/"           \
		-e "s/^description=Latest version:.*$$/description=Latest version: $(VERSION)/" \
		$(STEAMBUNDLE)/workshop.txt

clean:
	rm -rf $(BUNDLE) $(STEAMBUNDLE) && \
	$(MAKE) -C src clean

.PHONY: build dist
