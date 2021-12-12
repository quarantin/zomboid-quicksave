NAME      := QuickSave
BUNDLEDIR := dist/$(NAME)
JAR       := src/$(NAME).jar

all: bundle

build:
	$(MAKE) -C src build

bundle: build
	mkdir -p $(BUNDLEDIR) && \
	cp -r mod.info media $(JAR) $(BUNDLEDIR)

bundlesteam: bundle

clean:
	rm -rf dist && \
	$(MAKE) -C src clean

.PHONY: build dist
