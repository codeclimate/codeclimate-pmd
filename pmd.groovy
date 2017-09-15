#!/usr/bin/env groovy

import groovy.json.JsonSlurper
import groovy.util.FileNameFinder

class Config {
  def args
  def appContext
  def parsedConfig
  def filesToAnalyze
  def filesList

  Config(args) {
    this.args = args
    this.appContext = setupContext()
    this.parsedConfig = new JsonSlurper().parse(new File(appContext.configFile), "UTF-8")
    this.filesToAnalyze = filesToAnalyze()
    this.filesList = createTempFile()
    filesList << filesToAnalyze
  }

  def noFiles() {
    filesToAnalyze.isEmpty()
  }

  def ruleSet() {
    if(parsedConfig.config) {
      def configFile = parsedConfig.config instanceof String ? parsedConfig.config : parsedConfig.config.file

      if(fileExists(configFile)) {
        return configFile
      } else {
        System.err.println "Config file ${configFile} not found"
        System.exit(1)
      }
    }

    "/usr/src/app/ruleset.xml"
  }

  def filesListPath() {
    filesList.absolutePath
  }

  private def fileExists(file) {
    new File(file).exists()
  }

  private def filesToAnalyze() {
    def includePaths = parsedConfig.include_paths?.join(" ")
    def codeFolder = new File(appContext.codeFolder)

    def files = new FileNameFinder().getFileNames(appContext.codeFolder, includePaths)

    def i = files.iterator()
    while(i.hasNext()) {
      def name = i.next()
      if(!name.endsWith(".java")) {
        i.remove()
      }
    }

    def fileNames = files.toString()
    fileNames.substring(1, fileNames.length()-1).replaceAll("\\s+","")
  }

  private def createTempFile() {
    File tmp = File.createTempFile("files", ".txt")
    tmp.deleteOnExit()
    tmp
  }

  private def setupContext() {
    def cli = new CliBuilder(usage:"${this.class.name}")
    cli._(longOpt: "configFile", required: true, args: 1, "Path to config.json file")
    cli._(longOpt: "codeFolder", required: true, args: 1, "Path to code folder")
    cli.parse(args)
  }
}

def execute(command) {
  ProcessBuilder builder = new ProcessBuilder(command.split(' '))
  Process process = builder.start()
  process.consumeProcessOutput(System.out, System.err)
  process.waitFor()
  System.exit(process.exitValue())
}

/* ********** MAIN ********** */

def config = new Config(args)
if (config.noFiles()) {
  System.exit(0)
}

execute("/usr/src/app/lib/pmd/bin/run.sh pmd -filelist ${config.filesListPath()} -f codeclimate -R ${config.ruleSet()} -failOnViolation false")
