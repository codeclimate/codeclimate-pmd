#!/usr/bin/env groovy

import groovy.json.JsonSlurper
import groovy.util.FileNameFinder

def sanityCheck() {
  def command = "/usr/src/app/pmd.groovy --codeFolder=/code/test --configFile=/code/test/config.json"

  def proc = command.execute()
  def out = new StringBuffer()
  def err = new StringBuffer()

  proc.waitForProcessOutput(out, err)

  assert proc.exitValue() == 0
  assert !out.toString().isEmpty()
  assert err.toString().isEmpty()
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

sanityCheck()
engineCheckList()
dockerfileCheckList()
