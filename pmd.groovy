#!/usr/bin/env groovy

import groovy.json.JsonSlurper
import groovy.util.FileNameFinder


class Config {
  static final FILE_LIST = "/tmp/files"
  def args
  def appContext
  def parsedConfig
  def filesToAnalyze

  Config(args) {
    this.args = args
    this.appContext = setupContext()
    this.parsedConfig = new JsonSlurper().parse(new File(appContext.configFile), "UTF-8")
    this.filesToAnalyze = filesToAnalyze()
    saveFilesToAnalyze()
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

  def fileExists(file) {
    new File(file).exists()
  }

  def noFiles() {
    filesToAnalyze.isEmpty()
  }

  def saveFilesToAnalyze() {
    File analysisFilesTmp = createTempFile()
    analysisFilesTmp << filesToAnalyze
  }

  def filesToAnalyze() {
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

  def createTempFile() {
    File tmp = new File(FILE_LIST)
    tmp.createNewFile()
    tmp.deleteOnExit()
    tmp
  }

  def setupContext() {
    def cli = new CliBuilder(usage:"${this.class.name}")
    cli._(longOpt: "configFile", required: true, args: 1, "Path to config.json file")
    cli._(longOpt: "codeFolder", required: true, args: 1, "Path to code folder")
    cli.parse(args)
  }
}

/* ********** MAIN ********** */

def config = new Config(args)
if (config.noFiles()) {
  System.exit(0)
}

def command = "/usr/src/app/lib/pmd/bin/run.sh pmd -filelist ${Config.FILE_LIST} -f codeclimate -R ${config.ruleSet()} -failOnViolation false"

ProcessBuilder builder = new ProcessBuilder(command.split(' '))
Process process = builder.start()

InputStream stdout = process.getInputStream()
BufferedReader reader = new BufferedReader(new InputStreamReader(stdout))
while ((line = reader.readLine()) != null) {
  System.out.println(line)
}

process.waitForProcessOutput()

if (process.exitValue() != 0) {
  System.exit(-1)
}

System.exit(0)
