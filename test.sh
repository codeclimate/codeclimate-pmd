#!/usr/bin/env sh
set -ex

BUILD_DIR=build
GROOVY="${GROOVY_HOME}/embeddable/groovy-all-${GROOVY_VERSION}.jar"
JUNIT=$(ls ${GROOVY_HOME}/lib/junit*.jar)
HAMCREST=$(ls ${GROOVY_HOME}/lib/hamcrest*.jar)

test_classes() {
  find test -name "*.groovy" | sed -E 's#test/(.*).groovy#\1#' | xargs
}

clean() {
  rm -rf $BUILD_DIR
  mkdir $BUILD_DIR
}

build() {
  groovyc src/**.groovy test/**.groovy -d $BUILD_DIR
}

run() {
  java -cp build:$JUNIT:$HAMCREST:$GROOVY org.junit.runner.JUnitCore $(test_classes)
}

clean
build
run
