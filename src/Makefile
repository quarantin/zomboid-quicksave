ZOMBOID  := $(HOME)/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid/
JAVAMODS := ../libs/javamods.jar
JAR      := QuickSave.jar

build:
	javac -cp "$(ZOMBOID):$(JAVAMODS)" zombie/quicksave/*.java && \
	jar -cf "$(JAR)" javamods.txt zombie/quicksave/*.class

clean:
	rm -f *.jar zombie/quicksave/*.class
