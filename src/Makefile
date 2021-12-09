ZOMBOID   := $(HOME)/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid/
QUICKSAVE := QuickSave.jar
JAVAMODS  := ../libs/javamods.jar

build:
	javac -cp "$(ZOMBOID):$(JAVAMODS)" zombie/quicksave/*.java && \
	jar -cf "$(QUICKSAVE)" javamods.txt zombie/quicksave/*.class

clean:
	rm -f *.jar zombie/quicksave/*.class
