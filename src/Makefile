ZOMBOID   := $(HOME)/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid/
QUICKSAVE := QuickSave.jar
SCRIPT    := QuickSave.sh

build:
	javac -cp "$(ZOMBOID)" zombie/*.java zombie/Lua/*.java && \
	jar -cf "$(QUICKSAVE)" zombie/

clean:
	rm -f *.jar zombie/*.class zombie/Lua/*.class

install: build
	cp "$(QUICKSAVE)" "$(SCRIPT)" "$(ZOMBOID)"

uninstall:
	rm -f "$(ZOMBOID)/$(QUICKSAVE)" "$(ZOMBOID)/$(SCRIPT)"
