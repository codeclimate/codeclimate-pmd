#!/usr/bin/env sh

GROOVY="${GROOVY_HOME}/embeddable/groovy-all-${GROOVY_VERSION}.jar"
JUNIT=$(ls ${GROOVY_HOME}/lib/junit*.jar)
HAMCREST=$(ls ${GROOVY_HOME}/lib/hamcrest*.jar)

TEST_CLASSES=$(find test -name "*.groovy" | sed -E 's#test/(.*).groovy#\1#' | xargs)

rm -rf build

groovyc test/**.groovy -d build


java -cp build:$JUNIT:$HAMCREST:$GROOVY org.junit.runner.JUnitCore $TEST_CLASSES
