import groovy.json.JsonSlurper
import groovy.util.FileNameFinder

class Config {
  static String DEFAULT_RULES = "/usr/src/app/ruleset.xml"
  def args
  def appContext
  def parsedConfig
  def filesToAnalyze
  def filesList

  Config(appContext) {
    this.appContext = appContext
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

      def specifiedRules = new File(appContext.codeFolder, configFile)
      if(specifiedRules.exists()) {
        return specifiedRules.absolutePath
      } else {
        System.err.println "Config file ${configFile} not found"
        System.exit(1)
      }
    }

    return DEFAULT_RULES
  }

  def filesListPath() {
    filesList.absolutePath
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
}
