#!/usr/bin/env groovy

import groovy.json.JsonSlurper
import groovy.util.FileNameFinder

def appContext = setupContext(args)
def parsedConfig = new JsonSlurper().parse(new File(appContext.configFile), "UTF-8")

def includePaths = parsedConfig.include_paths?.join(" ")
def codeFolder = new File(appContext.codeFolder)

def filesToAnalyse = new FileNameFinder().getFileNames(appContext.codeFolder, includePaths)

File analysisFilesTmp = new File("/tmp/files")
analysisFilesTmp.createNewFile()
analysisFilesTmp.deleteOnExit()

def i = filesToAnalyse.iterator()
while(i.hasNext()) {
    string = i.next()
    if( !string.endsWith(".java") ) {
        i.remove()
    }
}

filesToAnalyse = filesToAnalyse.toString()
filesToAnalyse = filesToAnalyse.substring(1, filesToAnalyse.length()-1).replaceAll("\\s+","")
if (filesToAnalyse.isEmpty()) {
    System.exit(0)
}

analysisFilesTmp << filesToAnalyse

def ruleSetPath
if ( parsedConfig.config && (new File(parsedConfig.config).exists()) ) {
  ruleSetPath = parsedConfig.config
} else {
  ruleSetPath = "/usr/src/app/ruleset.xml"
}

def pmdCommand = "/usr/src/app/lib/pmd/bin/run.sh pmd -filelist /tmp/files -f codeclimate -R ${ruleSetPath} -failOnViolation false"

ProcessBuilder builder = new ProcessBuilder( pmdCommand.split(' ') )

Process process = builder.start()

InputStream stdout = process.getInputStream ()
BufferedReader reader = new BufferedReader (new InputStreamReader(stdout))

while ((line = reader.readLine ()) != null) {
   System.out.println ( line )
}

process.waitForProcessOutput()

if ( process.exitValue() != 0 ) {
    System.exit(-1)
}

System.exit(0)

def setupContext(cmdArgs) {
    def cli = new CliBuilder(usage:"${this.class.name}")
    cli._(longOpt: "configFile", required: true, args: 1, "Path to config.json file")
    cli._(longOpt: "codeFolder", required: true, args: 1, "Path to code folder")
    cli.parse(cmdArgs)
}
