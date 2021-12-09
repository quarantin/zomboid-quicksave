#!/bin/bash

INSTDIR="${HOME}/.steam/steam/steamapps/common/ProjectZomboid/projectzomboid"

if "${INSTDIR}/jre64/bin/java" -version > /dev/null 2>&1; then
	ARCH="64"
	JAVAROOT="${INSTDIR}/jre64"

elif "${INSTDIR}/jre/bin/java" -client -version > /dev/null 2>&1; then
	ARCH="32"
	JAVAROOT="${INSTDIR}/jre"
else
	echo "couldn't determine 32/64 bit of java"
	exit 0
fi

echo "${ARCH}-bit java detected"
export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:../natives/linux${ARCH}:../natives:./linux${ARCH}"
XMODIFIERS= LD_PRELOAD="${LD_PRELOAD}:${JAVAROOT}/lib/libjsig.so" \
	"${INSTDIR}/ProjectZomboid${ARCH}" \
	-pzexejavacmd "${JAVAROOT}/bin/java -classpath .:./* zombie.MainQuickSave" \
	-pzexeconfig "test-pzexe-linux-${ARCH}.json" \
	"$@"
