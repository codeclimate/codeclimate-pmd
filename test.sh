#!/usr/bin/env sh
set -ex

BUILD_DIR=build

libs() {
  find $GROOVY_HOME/lib -name "*.jar" | tr "\n" ":"
}

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
  java -cp build:$(libs) org.junit.runner.JUnitCore $(test_classes)
}

clean
build
run
