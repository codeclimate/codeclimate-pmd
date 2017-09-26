import groovy.json.JsonSlurper
import groovy.util.FileNameFinder

class Config {
  static String DEFAULT_RULES = "java-basic"
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
    def config = parsedConfig.config

    switch(config) {
      case String:
        return specifiedRules(config)
        break
      case Map:
        return rulesOrFile(config)
        break
    }

    def defaultFile = new File(appContext.codeFolder, "ruleset.xml")
    if(defaultFile.exists()) {
      return defaultFile.absolutePath
    }

    return DEFAULT_RULES
  }

  def filesListPath() {
    filesList.absolutePath
  }

  private def rulesOrFile(config) {
    validate(config)
    if(config.rules) {
      return config.rules.join(",")
    }
    return specifiedRules(config.file)
  }

  private def validate(config) {
    if(config.file && config.rules) {
      throw new IllegalArgumentException("Config should contain 'file' OR 'rules'")
    }
  }

  private def specifiedRules(configFile) {
    def rules = new File(appContext.codeFolder, configFile)
    if(rules.exists()) {
      return rules.absolutePath
    } else {
      System.err.println "Config file ${configFile} not found"
      System.exit(1)
    }
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
