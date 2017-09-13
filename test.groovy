#!/usr/bin/env groovy

import groovy.json.JsonSlurper
import groovy.util.FileNameFinder

def execute(command) {
  def proc = command.execute()
  def out = new StringBuffer()
  def err = new StringBuffer()

  proc.waitForProcessOutput(out, err)

  System.err.println err
  System.err.println out

  return [proc, out, err]
}

def sanityCheck() {
  def (proc, out, err) = execute("/usr/src/app/pmd.groovy --codeFolder=/code/test --configFile=/code/test/config.json")

  assert !out.toString().isEmpty()
  assert err.toString().isEmpty()
  assert proc.exitValue() == 0
}

def checkConfigBackwardCompatibility() {
  def (proc, out, _err) = execute("/usr/src/app/pmd.groovy --codeFolder=/code/test --configFile=/code/test/config.new.json")
  def (procOld, outOld, _errOld) = execute("/usr/src/app/pmd.groovy --codeFolder=/code/test --configFile=/code/test/config.old.json")

  assert proc.exitValue() == procOld.exitValue()
  assert out.toString().equals(outOld.toString())
  assert proc.exitValue() == 0
}

def abortOnBadConfig() {
  def (proc, out, err) = execute("/usr/src/app/pmd.groovy --codeFolder=/code/test --configFile=/code/test/config.bad.json")

  assert !err.toString().isEmpty()
  assert proc.exitValue() != 0
}

def handleBadOutput() {
  def (proc, out, err) = execute("/usr/src/app/pmd.groovy --codeFolder=/code/test --configFile=/code/test/config.problematic_rules.json")

  assert !err.toString().isEmpty()
  assert proc.exitValue() != 0
}

def engineCheckList() {
  def engine = new JsonSlurper().parse(new File("engine.json"), "UTF-8")
  assert engine.name
  assert engine.description
  assert engine.description.size() <= 140
  assert engine.maintainer && engine.maintainer.name && engine.maintainer.email
  assert engine.languages.size() > 0
  assert engine.version
}

def dockerfileCheckList() {
  def dockerfile = new File("Dockerfile").text

  assert dockerfile.contains("MAINTAINER")
  assert dockerfile.contains("VOLUME")
  assert dockerfile.contains("WORKDIR")
  assert dockerfile.contains("USER")
  assert dockerfile.contains("CMD")

  assert !dockerfile.toUpperCase().contains("EXPOSE")
  assert !dockerfile.toUpperCase().contains("ONBUILD")
  assert !dockerfile.toUpperCase().contains("ARG")

  assert dockerfile.split("\n")[-1].startsWith("CMD")
}

/** MAIN **/

engineCheckList()
dockerfileCheckList()

sanityCheck()
checkConfigBackwardCompatibility()
abortOnBadConfig()
handleBadOutput()
