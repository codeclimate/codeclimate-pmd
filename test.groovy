#!/usr/bin/env groovy

def command = "/usr/src/app/pmd.groovy  --codeFolder=/code/test --configFile=/code/test/config.json"

def proc = command.execute()
def out = new StringBuffer()
def err = new StringBuffer()

proc.waitForProcessOutput(out, err)

assert proc.exitValue() == 0
assert !out.toString().isEmpty()
assert err.toString().isEmpty()
